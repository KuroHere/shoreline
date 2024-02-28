package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.NumberDisplay;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.manager.world.tick.TickSync;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.RotationModule;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.network.PlayerUpdateEvent;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.world.EndCrystalUtil;
import com.caspian.client.util.world.EntityUtil;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Threaded AutoCrystal implementation.
 *
 * @author linus
 * @since 1.0
 */
public class AutoCrystalModule extends RotationModule
{
    // GENERAL SETTINGS
    Config<Boolean> multitaskConfig = new BooleanConfig("Multitask",
            "Allows attacking while using items", false);
    Config<Boolean> whileMiningConfig = new BooleanConfig("WhileMining",
            "Allows attacking while mining blocks", false);
    Config<Float> targetRangeConfig = new NumberConfig<>("EnemyRange",
            "Range to search for potential enemies", 1.0f, 10.0f, 13.0f);
    Config<Boolean> multithreadConfig = new BooleanConfig("Concurrent",
            "Attempts to run calculations in the background of the main " +
                    "minecraft ticks on a concurrent thread pool", false);
    Config<Boolean> instantConfig = new BooleanConfig("Instant",
            "Instantly attacks crystals when they spawn", false);
    Config<Boolean> instantCalcConfig = new BooleanConfig("SpawnTime-Calc",
            "Calculates a crystal when it spawns and attacks if it meets " +
                    "MINIMUM requirements, this will result in non-ideal " +
                    "crystal attacks", false, () -> instantConfig.getValue());
    Config<Boolean> instantMaxConfig = new BooleanConfig("InstantMax",
            "Attacks crystals instantly if they exceed the previous max " +
                    "attack damage (Note: This is still not a perfect check " +
                    "because the next tick could have better damages", true,
            () -> instantConfig.getValue());
    Config<Float> instantDamageConfig = new NumberConfig<>("InstantDamage",
            "Minimum damage to attack crystals instantly", 1.0f, 6.0f, 10.0f,
            () -> instantConfig.getValue() && instantCalcConfig.getValue());
    Config<Float> calcSleepConfig = new NumberConfig<>("CalcTimeout", "Time " +
            "to sleep and pause calculation directly after completing a " +
            "calculation", 0.00f, 0.03f, 0.05f);
    Config<Boolean> latencyPositionConfig = new BooleanConfig(
            "LatencyPosition", "Targets the latency positions of enemies", false);
    Config<Integer> maxLatencyConfig = new NumberConfig<>("MaxLatency",
            "Maximum latency factor when calculating positions", 50, 250,
            1000, () -> latencyPositionConfig.getValue());
    Config<TickSync> latencySyncConfig = new EnumConfig<>("LatencyTimeout",
            "Latency calculations for time between crystal packets",
            TickSync.AVERAGE, TickSync.values());
    // Config<Boolean> raytraceConfig = new BooleanConfig("Raytrace", "",
    //        false);
    Config<Sequential> sequentialConfig = new EnumConfig<>("Sequential",
            "Calculates sequentially, so placements occur once the " +
                    "expected crystal is broken", Sequential.NORMAL,
            Sequential.values());
    Config<Boolean> preSequentialCalcConfig = new BooleanConfig(
            "PreSequential-Calc", "", false,
            () -> sequentialConfig.getValue() != Sequential.NONE);
    Config<Boolean> swingConfig = new BooleanConfig("Swing",
            "Swing hand when placing and attacking crystals", true);
    // ROTATE SETTINGS
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotate" +
            "before placing and breaking", false);
    Config<Rotate> strictRotateConfig = new EnumConfig<>("Rotate-Strict",
            "Rotates yaw over multiple ticks to prevent certain rotation  " +
                    "flags in NCP", Rotate.OFF, Rotate.values(),
            () -> rotateConfig.getValue());
    Config<Integer> rotateLimitConfig = new NumberConfig<>(
            "RotateLimit", "Maximum yaw rotation in degrees for one tick",
            1, 180, 180, NumberDisplay.DEGREES,
            () -> rotateConfig.getValue() && strictRotateConfig.getValue() != Rotate.OFF);
    Config<Integer> yawTicksConfig = new NumberConfig<>("YawTicks",
            "Minimum ticks to rotate yaw", 1, 1, 5,
            () -> rotateConfig.getValue() && strictRotateConfig.getValue() != Rotate.OFF);
    Config<Integer> rotateTimeoutConfig = new NumberConfig<>(
            "RotateTimeout", "Minimum ticks to hold the rotation yaw after " +
            "reaching the rotation", 0, 0, 5, () -> rotateConfig.getValue());
    Config<Integer> rotateMaxConfig = new NumberConfig<>("Rotate-LeniencyTicks",
            "Maximum ticks the current rotation has to complete before the " +
                    "next rotation overrides the rotation", 0, 1, 3,
            () -> rotateConfig.getValue() && strictRotateConfig.getValue() != Rotate.OFF);
    Config<Boolean> rotateTickFactorConfig = new BooleanConfig("Rotate-TickReduction",
            "Factors in angles when calculating crystals to minimize " +
                    "attack ticks and speed up the break/place loop", false,
            () -> rotateConfig.getValue() && strictRotateConfig.getValue() != Rotate.OFF);
    Config<Float> rotateDamageConfig = new NumberConfig<>("Rotate-MaxDamage",
            "Maximum allowed damage loss when minimizing tick rotations", 0.0f,
            2.0f, 10.0f, () -> rotateConfig.getValue()
            && strictRotateConfig.getValue() != Rotate.OFF
            && rotateTickFactorConfig.getValue());
    Config<Boolean> vectorBorderConfig = new BooleanConfig("VectorBorder",
            "Rotates to the border between attack/place", true,
            () -> rotateConfig.getValue());
    Config<Boolean> randomVectorConfig = new BooleanConfig("RandomVector",
            "Randomizes attack rotations", false, () -> rotateConfig.getValue());
    Config<Boolean> offsetFacingConfig = new BooleanConfig("InteractOffset",
            "Rotates to the side of interact (only applies to PLACE " +
                    "rotations)", false, () -> rotateConfig.getValue());
    Config<Integer> rotatePreserveTicksConfig = new NumberConfig<>(
            "PreserveTicks", "Time to preserve rotations before switching " +
            "back", 0, 20, 20, () -> rotateConfig.getValue());
    // ENEMY SETTINGS
    Config<Boolean> playersConfig = new BooleanConfig("Players",
            "Target players", true);
    Config<Boolean> monstersConfig = new BooleanConfig("Monsters",
            "Target monsters", false);
    Config<Boolean> neutralsConfig = new BooleanConfig("Neutrals",
            "Target neutrals", false);
    Config<Boolean> animalsConfig = new BooleanConfig("Animals",
            "Target animals", false);
    // BREAK SETTINGS
    Config<Float> breakSpeedConfig = new NumberConfig<>("BreakSpeed",
            "Speed to break crystals", 0.1f, 18.0f, 20.0f);
    Config<Float> attackDelayConfig = new NumberConfig<>("AttackDelay",
            "Added delays", 0.0f, 0.0f, 5.0f);
    Config<Float> randomSpeedConfig = new NumberConfig<>("RandomSpeed",
            "Randomized delay for breaking crystals", 0.0f, 0.0f, 10.0f);
    Config<Float> breakTimeoutConfig = new NumberConfig<>("BreakTimeout",
            "Time after waiting for the average break time before considering" +
                    " a crystal attack failed", 0.0f, 3.0f, 10.0f);
    Config<Float> minTimeoutConfig = new NumberConfig<>("MinTimeout",
            "Minimum time before considering a crystal break/place failed",
            0.0f, 5.0f, 20.0f);
    Config<Integer> ticksExistedConfig = new NumberConfig<>("TicksExisted",
            "Minimum ticks alive to consider crystals for attack", 0, 0, 10);
    Config<Boolean> postRangeConfig = new BooleanConfig("PreRangeCheck",
            "Checks ranges when validating calculations", false);
    Config<Float> breakRangeConfig = new NumberConfig<>("BreakRange",
            "Range to break crystals", 0.1f, 4.0f, 5.0f);
    Config<Float> strictBreakRangeConfig = new NumberConfig<>(
            "StrictBreakRange", "NCP range to break crystals", 0.1f, 4.0f,
            5.0f);
    Config<Float> maxYOffsetConfig = new NumberConfig<>("MaxYOffset",
            "Maximum crystal y-offset difference", 1.0f, 5.0f, 10.0f);
    Config<Float> breakWallRangeConfig = new NumberConfig<>(
            "BreakWallRange", "Range to break crystals through walls", 0.1f,
            4.0f, 5.0f);
    Config<Swap> antiWeaknessConfig = new EnumConfig<>("AntiWeakness",
            "Swap to tools before attacking crystals", Swap.OFF,
            Swap.values());
    Config<Float> swapDelayConfig = new NumberConfig<>("SwapPenalty", "Delay " +
            "for attacking after swapping items which prevents NCP flags", 0.0f,
            0.0f, 10.0f);
    // fight.speed:
    //        limit: 13
    // shortterm:
    //        ticks: 8
    Config<Boolean> breakCommitConfig = new BooleanConfig("BreakCommit",
            "Completes the pre-check calculations for crystals and " +
                    "skips the \"post processing\" of calculations", false);
    Config<Inhibit> inhibitConfig = new EnumConfig<>("Inhibit",
            "Prevents excessive attacks", Inhibit.NONE, Inhibit.values());
    Config<Integer> inhibitTicksConfig = new NumberConfig<>("InhibitTicks",
            "Counts crystals for x amount of ticks before determining that " +
                    "the attack won't violate NCP attack speeds (aka " +
                    "shortterm ticks)", 5, 8, 15,
            () -> inhibitConfig.getValue() == Inhibit.FULL);
    Config<Integer> inhibitLimitConfig = new NumberConfig<>("InhibitLimit",
            "Limit to crystal attacks that would flag NCP attack limits",
            1, 13, 20, () -> inhibitConfig.getValue() == Inhibit.FULL);
    // default NCP config
    // limitforseconds:
    //        half: 8
    //        one: 15
    //        two: 30
    //        four: 60
    //        eight: 100
    Config<Integer> attackFreqConfig = new NumberConfig<>(
            "AttackFreq-Half", "Limit of attack packets sent for each " +
            "half-second interval", 1, 8, 20,
            () -> inhibitConfig.getValue() != Inhibit.NONE);
    Config<Integer> attackFreqFullConfig = new NumberConfig<>(
            "AttackFreq-Full", "Limit of attack packets sent for each " +
            "one-second interval", 10, 15, 30,
            () -> inhibitConfig.getValue() != Inhibit.NONE);
    Config<Integer> attackFreqMaxConfig = new NumberConfig<>(
            "AttackFreq-Max", "Limit of attack packets sent for each " +
            "eight-second interval", 80, 100, 150,
            () -> inhibitConfig.getValue() != Inhibit.NONE);
    Config<Boolean> manualConfig = new BooleanConfig("Manual",
            "Always breaks manually placed crystals", false);
    // PLACE SETTINGS
    Config<Boolean> placeConfig = new BooleanConfig("Place", "Places crystals" +
            " to damage enemies. Place settings will only function if this " +
            "setting is enabled.", true);
    Config<Float> placeSpeedConfig = new NumberConfig<>("PlaceSpeed",
            "Speed to place crystals", 0.1f, 18.0f, 20.0f,
            () -> placeConfig.getValue());
    Config<Float> placeTimeoutConfig = new NumberConfig<>("PlaceTimeout",
            "Time after waiting for the average place time before considering" +
                    " a crystal placement failed", 0.0f, 3.0f, 10.0f,
            () -> placeConfig.getValue());
    Config<Float> placeRangeConfig = new NumberConfig<>("PlaceRange",
            "Range to place crystals", 0.1f, 4.0f, 5.0f,
            () -> placeConfig.getValue());
    Config<Float> strictPlaceRangeConfig = new NumberConfig<>(
            "StrictPlaceRange", "NCP range to place crystals", 0.1f, 4.0f,
            5.0f, () -> placeConfig.getValue());
    Config<Float> placeWallRangeConfig = new NumberConfig<>(
            "PlaceWallRange", "Range to place crystals through walls", 0.1f,
            4.0f, 5.0f, () -> placeConfig.getValue());
    Config<Boolean> minePlaceConfig = new BooleanConfig("MinePlace",
            "Places on mining blocks that when broken, can be placed on to " +
                    "damage enemies. Instantly destroys items spawned from " +
                    "breaking block and allows faster placing", false,
            () -> placeConfig.getValue());
    Config<Boolean> boundsConfig = new BooleanConfig("Bounds", "Targets " +
            "closest bounded rotations", false);
    Config<Boolean> placeRangeEyeConfig = new BooleanConfig(
            "PlaceRangeEye", "Calculates place ranges starting from the eye " +
            "position of the player, which is how NCP calculates ranges",
            false, () -> placeConfig.getValue());
    Config<Boolean> placeRangeCenterConfig = new BooleanConfig(
            "PlaceRangeCenter", "Calculates place ranges to the center of the" +
            " block, which is how NCP calculates ranges", true,
            () -> placeConfig.getValue());
    Config<Boolean> halfCrystalConfig = new BooleanConfig("HalfBB-Place",
            "Allow placements at a lower bounding", false,
            () -> placeConfig.getValue());
    Config<Boolean> antiTotemConfig = new BooleanConfig("AntiTotem",
            "Predicts totems and places crystals to instantly double pop and " +
                    "kill the target", false, () -> placeConfig.getValue());
    Config<Swap> autoSwapConfig = new EnumConfig<>("Swap", "Swaps to an end " +
            "crystal before placing if the player is not holding one", Swap.OFF,
            Swap.values(), () -> placeConfig.getValue());
    // Config<Boolean> swapSyncConfig = new BooleanConfig("SwapSync",
    //        "", false);
    Config<Float> alternateSpeedConfig = new NumberConfig<>("AlternateSpeed",
            "Speed for alternative swapping crystals", 1.0f, 18.0f, 20.0f,
            () -> placeConfig.getValue() && autoSwapConfig.getValue() == Swap.SILENT_ALT);
    Config<Boolean> antiSurroundConfig = new BooleanConfig(
            "AntiSurround", "Places crystals to block the enemy's feet and " +
            "prevent them from using Surround", false,
            () -> placeConfig.getValue());
    Config<Boolean> breakValidConfig = new BooleanConfig(
            "BreakValid-Test", "Only places crystals that can be attacked",
            false, () -> placeConfig.getValue());
    Config<Boolean> strictDirectionConfig = new BooleanConfig(
            "StrictDirection", "Interacts with only visible directions when " +
            "placing crystals", false, () -> placeConfig.getValue());
    Config<Boolean> exposedDirectionConfig = new BooleanConfig(
            "StrictDirection-Exposed", "Interacts with only exposed " +
            "directions when placing crystals", false,
            () -> placeConfig.getValue());
    Config<Placements> placementsConfig = new EnumConfig<>("Placements",
            "Version standard for placing end crystals", Placements.NATIVE,
            Placements.values(), () -> placeConfig.getValue());
    // DAMAGE SETTINGS
    Config<Float> minDamageConfig = new NumberConfig<>("MinDamage",
            "Minimum damage required to consider attacking or placing an end " +
                    "crystal", 1.0f, 4.0f, 10.0f);
    Config<Boolean> armorBreakerConfig = new BooleanConfig("ArmorBreaker",
            "Attempts to break enemy armor with crystals", true);
    Config<Float> armorScaleConfig = new NumberConfig<>("ArmorScale",
            "Armor damage scale before attempting to break enemy armor with " +
                    "crystals", 1.0f, 5.0f, 20.0f, NumberDisplay.PERCENT,
            () -> armorBreakerConfig.getValue());
    Config<Float> lethalMultiplier = new NumberConfig<>(
            "LethalMultiplier", "If we can kill an enemy with this many " +
            "crystals, disregard damage values", 0.0f, 1.5f, 4.0f);
    Config<Boolean> safetyConfig = new BooleanConfig("Safety", "Accounts for" +
            " total player safety when attacking and placing crystals", true);
    Config<Boolean> safetyBalanceConfig = new BooleanConfig(
            "SafetyBalance", "Target damage must be greater than player " +
            "damage for a position to be considered", false);
    Config<Boolean> safetyOverride = new BooleanConfig("SafetyOverride",
            "Overrides the safety checks if the crystal will kill an enemy",
            false);
    Config<Float> maxLocalDamageConfig = new NumberConfig<>(
            "MaxLocalDamage", "", 4.0f, 12.0f, 20.0f);
    Config<Boolean> blockDestructionConfig = new BooleanConfig(
            "BlockDestruction", "Accounts for explosion block destruction " +
            "when calculating damages", false);
    // EXTRAPOLATION
    Config<Boolean> extrapolateRangeConfig = new BooleanConfig(
            "ExtrapolateRange", "Accounts for motion when calculating ranges",
            false);
    Config<Boolean> extrapolateGravityConfig = new BooleanConfig(
            "Gravity-Extrapolation", "Accounts for gravity when extrapolating" +
            " positions", false);
    Config<Integer> extrapolateTicksConfig = new NumberConfig<>(
            "ExtrapolationTicks", "Accounts for motion when calculating " +
            "enemy positions, not fully accurate.", 0, 0, 10);
    Config<Integer> selfExtrapolateTicksConfig = new NumberConfig<>(
            "Self-ExtrapolationTicks", "Accounts for motion when calculating " +
            "player positions, not fully accurate.", 0, 0, 10);
    // RENDER SETTINGS
    Config<Boolean> renderConfig = new BooleanConfig("Render",
            "Renders the current placement", true);
    Config<Boolean> renderAttackConfig = new BooleanConfig(
            "RenderAttack", "Renders the current attack", false);
    Config<Boolean> renderSpawnConfig = new BooleanConfig("RenderSpawn",
            "Indicates if the current placement was spawned", false);
    Config<Boolean> damageNametagConfig = new BooleanConfig("DamageNametag",
            "Renders the current expected damage of a place/attack", false,
            () -> renderConfig.getValue());
    //
    private BlockPos pos;

    /**
     *
     */
    public AutoCrystalModule()
    {
        super("AutoCrystal", "Attacks entities with end crystals",
                ModuleCategory.COMBAT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerUpdate(PlayerUpdateEvent event)
    {
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
        ArrayList<Entity> entities =
                Lists.newArrayList(mc.world.getEntities());
        List<BlockPos> blocks = getSphere(mc.player.getPos());
        DamageData<EndCrystalEntity> attackCrystal =
                calculateAttackCrystal(entities);
        DamageData<BlockPos> placeCrystal =
                calculatePlaceCrystal(blocks, entities);
        final Hand hand = getCrystalHand();
        if (attackCrystal != null)
        {
            attackInternal(attackCrystal.getDamageData(), hand);
        }
        if (placeCrystal != null)
        {
            pos = placeCrystal.getDamageData();
            BlockHitResult result = new BlockHitResult(pos.toCenterPos(),
                    Direction.UP, pos, false);
            placeInternal(result, hand);
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (pos != null)
        {
            RenderManager.renderBox(event.getMatrices(), pos,
                    Modules.COLORS.getRGB(60));
            RenderManager.renderBoundingBox(event.getMatrices(), pos, 1.5f,
                    Modules.COLORS.getRGB(145));
        }
    }

    public boolean isAttacking()
    {
        return false;
    }

    public boolean isPlacing()
    {
        return false;
    }

    private void attackInternal(EndCrystalEntity entity, Hand hand)
    {
        attackInternal(entity.getId(), hand);
    }

    private void attackInternal(int id, Hand hand)
    {
        hand = hand != null ? hand : Hand.MAIN_HAND;
        EndCrystalEntity crystalEntity = new EndCrystalEntity(mc.world, 0.0, 0.0, 0.0);
        crystalEntity.setId(id);
        PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(crystalEntity,
                mc.player.isSneaking());
        Managers.NETWORK.sendPacket(packet);
        if (swingConfig.getValue())
        {
            mc.player.swingHand(hand);
        }
        else
        {
            Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
        }
    }

    private void placeInternal(BlockHitResult result, Hand hand)
    {
        if (hand == null)
        {
            return;
        }
        Managers.NETWORK.sendSequencedPacket(id ->
                new PlayerInteractBlockC2SPacket(hand, result, id));
        if (swingConfig.getValue())
        {
            mc.player.swingHand(hand);
        }
        else
        {
            Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
        }
    }

    /**
     *
     * @param entities
     * @return
     */
    private DamageData<EndCrystalEntity> calculateAttackCrystal(List<Entity> entities)
    {
        if (entities.isEmpty())
        {
            return null;
        }
        double playerDamage = 0.0;
        double bestDamage = 0.0;
        EndCrystalEntity crystalEntity = null;
        Entity attackTarget = null;
        for (Entity crystal : entities)
        {
            if (!(crystal instanceof EndCrystalEntity crystal1) || !crystal.isAlive())
            {
                continue;
            }
            if (crystal.age < ticksExistedConfig.getValue()
                    && inhibitConfig.getValue() == Inhibit.NONE)
            {
                continue;
            }
            if (attackRangeCheck(crystal1))
            {
                continue;
            }
            double selfDamage = EndCrystalUtil.getDamageTo(mc.player,
                    crystal.getPos(), blockDestructionConfig.getValue());
            for (Entity entity : entities)
            {
                if (entity == null || !entity.isAlive() || entity == mc.player
                        || !isValidTarget(entity)
                        || Managers.SOCIAL.isFriend(entity.getUuid()))
                {
                    continue;
                }
                double crystalDist = crystal.squaredDistanceTo(entity);
                if (crystalDist > 144.0f)
                {
                    continue;
                }
                double dist = mc.player.squaredDistanceTo(entity);
                if (dist > targetRangeConfig.getValue() * targetRangeConfig.getValue())
                {
                    continue;
                }
                double damage = EndCrystalUtil.getDamageTo(entity,
                        crystal.getPos(), blockDestructionConfig.getValue());
                if (damage > bestDamage)
                {
                    bestDamage = damage;
                    playerDamage = selfDamage;
                    crystalEntity = crystal1;
                    attackTarget = entity;
                }
            }
        }
        if (crystalEntity == null || bestDamage < minDamageConfig.getValue())
        {
            return null;
        }
        return new DamageData<>(crystalEntity, attackTarget,
                bestDamage, playerDamage);
    }

    /**
     *
     * @param entity
     * @return
     */
    private boolean attackRangeCheck(EndCrystalEntity entity)
    {
        double dist = mc.player.distanceTo(entity);
        if (dist > breakRangeConfig.getValue() * breakRangeConfig.getValue())
        {
            return true;
        }
        double yOff = Math.abs(entity.getY() - mc.player.getY());
        if (yOff > maxYOffsetConfig.getValue())
        {
            return true;
        }
        BlockHitResult result = mc.world.raycast(new RaycastContext(
                mc.player.getEyePos(), entity.getPos(),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player));
        return result != null && dist > breakWallRangeConfig.getValue();
    }

    /**
     *
     * @param placeBlocks
     * @param entities
     * @return
     */
    private DamageData<BlockPos> calculatePlaceCrystal(List<BlockPos> placeBlocks,
                                                       List<Entity> entities)
    {
        if (placeBlocks.isEmpty() || entities.isEmpty())
        {
            return null;
        }
        double playerDamage = 0.0;
        double bestDamage = 0.0;
        BlockPos placeCrystal = null;
        Entity attackTarget = null;
        for (BlockPos pos : placeBlocks)
        {
            if (placeRangeCheck(pos))
            {
                continue;
            }
            double selfDamage = EndCrystalUtil.getDamageTo(mc.player,
                    crystalDamageVec(pos));
            for (Entity entity : entities)
            {
                if (entity == null || !entity.isAlive() || entity == mc.player
                        || !isValidTarget(entity)
                        || Managers.SOCIAL.isFriend(entity.getUuid()))
                {
                    continue;
                }
                double blockDist = pos.getSquaredDistance(entity.getPos());
                if (blockDist > 144.0f)
                {
                    continue;
                }
                double dist = mc.player.squaredDistanceTo(entity);
                if (dist > targetRangeConfig.getValue() * targetRangeConfig.getValue())
                {
                    continue;
                }
                double damage = EndCrystalUtil.getDamageTo(entity,
                        crystalDamageVec(pos), blockDestructionConfig.getValue());
                if (damage > bestDamage)
                {
                    bestDamage = damage;
                    playerDamage = selfDamage;
                    placeCrystal = pos;
                    attackTarget = entity;
                }
            }
        }
        if (placeCrystal == null || bestDamage < minDamageConfig.getValue())
        {
            return null;
        }
        return new DamageData<>(placeCrystal, attackTarget,
                bestDamage, playerDamage);
    }

    /**
     *
     * @param pos
     * @return
     */
    private boolean placeRangeCheck(BlockPos pos)
    {
        double dist = placeRangeCenterConfig.getValue() ?
                mc.player.squaredDistanceTo(pos.toCenterPos()) : pos.getSquaredDistance(mc.player.getPos());
        if (dist > placeRangeConfig.getValue() * placeRangeConfig.getValue())
        {
            return true;
        }
        Vec3d raytrace = pos.toCenterPos().add(0.0, 2.200000047683716, 0.0);
        BlockHitResult result = mc.world.raycast(new RaycastContext(
                mc.player.getEyePos(), raytrace,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player));
        float maxDist = breakRangeConfig.getValue() * breakRangeConfig.getValue();
        if (result != null && result.getType() == HitResult.Type.BLOCK
                && result.getBlockPos() != pos)
        {
            maxDist = breakWallRangeConfig.getValue() * breakWallRangeConfig.getValue();
            if (dist > placeWallRangeConfig.getValue() * placeWallRangeConfig.getValue())
            {
                return true;
            }
        }
        return breakValidConfig.getValue() && dist > maxDist;
    }

    private Vec3d crystalDamageVec(BlockPos pos)
    {
        return Vec3d.of(pos).add(0.5, 1.0, 0.5);
    }

    /**
     * Returns <tt>true</tt> if the {@link Entity} is a valid enemy to attack.
     *
     * @param e The potential enemy entity
     * @return <tt>true</tt> if the entity is an enemy
     */
    private boolean isValidTarget(Entity e)
    {
        return e instanceof PlayerEntity && playersConfig.getValue()
                || EntityUtil.isMonster(e) && monstersConfig.getValue()
                || EntityUtil.isNeutral(e) && neutralsConfig.getValue()
                || EntityUtil.isPassive(e) && animalsConfig.getValue();
    }

    //
    private static final Box FULL_CRYSTAL_BB = new Box(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);
    private static final Box HALF_CRYSTAL_BB = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    /**
     * Returns <tt>true</tt> if an {@link EndCrystalItem} can be used on the
     * param {@link BlockPos}.
     *
     * @param p The block pos
     * @return Returns <tt>true</tt> if the crystal item can be placed on the
     * block
     */
    public boolean canUseCrystalOnBlock(BlockPos p)
    {
        BlockState state = mc.world.getBlockState(p);
        if (!state.isOf(Blocks.OBSIDIAN) && !state.isOf(Blocks.BEDROCK))
        {
            return false;
        }
        BlockPos p2 = p.up();
        BlockState state2 = mc.world.getBlockState(p2);
        // ver 1.12.2 and below
        if (placementsConfig.getValue() == Placements.PROTOCOL)
        {
            BlockPos p3 = p2.up();
            BlockState state3 = mc.world.getBlockState(p3);
            if (!mc.world.isAir(p2) && !state3.isOf(Blocks.FIRE))
            {
                return false;
            }
        }
        if (!mc.world.isAir(p2) && !state2.isOf(Blocks.FIRE))
        {
            return false;
        }
        else
        {
            final Box bb = halfCrystalConfig.getValue() ? HALF_CRYSTAL_BB :
                    FULL_CRYSTAL_BB;
            double d = p2.getX();
            double e = p2.getY();
            double f = p2.getZ();
            List<Entity> list = getEntitiesBlockingCrystal(new Box(d, e, f,
                    d + bb.maxX, e + bb.maxY, f + bb.maxZ));
            return list.isEmpty();
        }
    }

    /**
     *
     * @param box
     * @return
     */
    private List<Entity> getEntitiesBlockingCrystal(Box box)
    {
        List<Entity> entities = new CopyOnWriteArrayList<>(
                mc.world.getOtherEntities(null, box));
        //
        for (Entity entity : entities)
        {
            if (entity == null || !entity.isAlive()
                    || entity instanceof ExperienceOrbEntity)
            {
                entities.remove(entity);
            }
            else if (entity instanceof EndCrystalEntity
                    && entity.getPos().squaredDistanceTo(box.minX, box.minY, box.minZ) <= 1.0)
            {
                entities.remove(entity);
            }
        }
        return entities;
    }

    private List<BlockPos> getSphere(Vec3d origin)
    {
        List<BlockPos> sphere = new ArrayList<>();
        double rad = Math.ceil(placeRangeConfig.getValue());
        for (double x = -rad; x <= rad; ++x)
        {
            for (double y = -rad; y <= rad; ++y)
            {
                for (double z = -rad; z <= rad; ++z)
                {
                    Vec3i pos = new Vec3i((int) (origin.getX() + x),
                            (int) (origin.getY() + y), (int) (origin.getZ() + z));
                    final BlockPos p = new BlockPos(pos);
                    //
                    if (canUseCrystalOnBlock(p))
                    {
                        sphere.add(p);
                    }
                }
            }
        }
        return sphere;
    }

    private Hand getCrystalHand()
    {
        final ItemStack offhand = mc.player.getOffHandStack();
        final ItemStack mainhand = mc.player.getMainHandStack();
        if (offhand.getItem() instanceof EndCrystalItem)
        {
            return Hand.OFF_HAND;
        }
        else if (mainhand.getItem() instanceof EndCrystalItem)
        {
            return Hand.MAIN_HAND;
        }
        return null;
    }


    public enum Swap
    {
        NORMAL,
        SILENT,
        SILENT_ALT,
        OFF
    }

    public enum Sequential
    {
        NORMAL,
        STRICT,
        FAST,
        NONE
    }

    public enum Placements
    {
        NATIVE,
        PROTOCOL
    }

    public enum Inhibit
    {
        FULL,
        SEMI,
        NONE
    }
    
    public enum Rotate
    {
        FULL,
        SEMI,
        OFF
    }

    private static class DamageData<T>
    {
        private final T damageData;
        private final Entity attackTarget;
        //
        private final double damage, selfDamage;

        /**
         *
         * @param damageData
         * @param attackTarget
         * @param damage
         * @param selfDamage
         */
        public DamageData(T damageData, Entity attackTarget, double damage, double selfDamage)
        {
            this.damageData = damageData;
            this.attackTarget = attackTarget;
            this.damage = damage;
            this.selfDamage = selfDamage;
        }

        public T getDamageData()
        {
            return damageData;
        }

        public Entity getAttackTarget()
        {
            return attackTarget;
        }

        public double getDamage()
        {
            return damage;
        }

        public double getSelfDamage()
        {
            return selfDamage;
        }
    }
}
