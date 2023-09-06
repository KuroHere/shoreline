package com.caspian.client.impl.module.combat;

import com.caspian.client.Caspian;
import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.setting.NumberDisplay;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.manager.tick.TickSync;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.RotationModule;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.RunTickEvent;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.TickTimer;
import com.caspian.client.util.math.timer.Timer;
import com.caspian.client.util.ncp.DirectionChecks;
import com.caspian.client.util.player.PlayerUtil;
import com.caspian.client.util.player.RotationUtil;
import com.caspian.client.util.world.EndCrystalUtil;
import com.caspian.client.util.world.EntityUtil;
import com.caspian.client.util.world.FakePlayerEntity;
import com.caspian.client.util.world.VecUtil;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.security.SecureRandom;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Threaded AutoCrystal implementation.
 *
 * @author linus
 * @since 1.0
 *
 * @see TickCalculation
 * @see ThreadCalcProcessor
 * @see DamageData
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
    private int tick;
    //
    private DamageData<BlockPos> placeData, lastPlaceData;
    private DamageData<EndCrystalEntity> attackData, lastAttackData;
    private BlockPos mining;
    //
    private BlockPos preSequence;
    private EndCrystalEntity sequence, postSequence;
    private final Timer tickSequence = new TickTimer();
    private final Timer startSequence = new CacheTimer();
    // Calculated placements and attacks will be added to their respective
    // stacks. When the main loop requires a placement/attack, simply pop the
    // last calculated from the stack.
    private final ThreadCalcProcessor processor = new ThreadCalcProcessor();
    private boolean pauseCalcAttack, pauseCalcPlace;
    // Set of attempted placements and attacks
    private final Map<BlockPos, Long> placements =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    private final Map<BlockPos, Long> manuals =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    private final Map<Integer, Long> attacks =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    private final Map<Integer, Long> explosions =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    private int shortTermCount;
    private int shortTermTick;
    // RANDOM
    private final Random random = new SecureRandom();
    private long randomTime = -1;
    //
    private final Map<PlayerEntity, Long> pops =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    //
    private final Timer freqInterval = new CacheTimer();
    private int freq;
    private final ArrayList<Integer> attackFreq =
            new ArrayList<>(Collections.nCopies(16, 0));
    //
    private boolean attacking, placing;
    private final Timer lastPlace = new CacheTimer();
    private final Timer lastBreak = new CacheTimer();
    //
    private final Deque<Long> breakTimes = new ArrayDeque<>(20);
    private final Deque<Long> placeTimes = new ArrayDeque<>(20);
    private final Timer lastSwap = new CacheTimer();
    private final Timer lastSwapAlt = new CacheTimer();
    private final Timer autoSwapTimer = new CacheTimer();
    // private final Timer lastClean = new Timer();
    // ROTATIONS
    //
    private final Timer rotateTimer = new TickTimer();
    private int rotating;
    private Rotate rotateTarget = Rotate.OFF;
    //
    private float[] yawLimits;
    private float yaw, pitch;
    // RENDER
    private final AtomicReference<Box> renderBreak =
            new AtomicReference<>();
    private final AtomicReference<BlockPos> renderPlace =
            new AtomicReference<>();
    private BlockPos renderSpawn;

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
     */
    @Override
    public void onEnable()
    {
        if (mc.player != null && mc.world != null)
        {
            freqInterval.reset();
            freq = 0;
            // run calc
            final ArrayList<Entity> entities =
                    Lists.newArrayList(mc.world.getEntities());
            Vec3d pos = Managers.POSITION.getPos();
            Vec3d eyepos = Managers.POSITION.getEyePos();
            if (extrapolateRangeConfig.getValue())
            {
                pos = extrapolatePosition(mc.player, pos,
                        mc.player.getBoundingBox(), selfExtrapolateTicksConfig.getValue());
                eyepos = VecUtil.toEyePos(mc.player, pos);
            }
            final Iterable<EndCrystalEntity> crystals = getCrystalSphere(eyepos);
            final Iterable<BlockPos> blocks =
                    getSphere(placeRangeEyeConfig.getValue() ? eyepos : pos);
            processor.runCalc(entities, crystals, blocks);
        }
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        processor.shutdown();
        clear();
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onDisconnect(DisconnectEvent event)
    {
        disable();
    }

    /**
     *
     *
     */
    private void tickCalc()
    {
        final TickCalculation calc = processor.getCurrentCalc();
        if (calc != null)
        {
            try
            {
                calc.checkDone();
            }
            catch (InterruptedException | ExecutionException e)
            {
                Caspian.error(Modules.AUTO_CRYSTAL, "Failed calculation {}!",
                        calc.getId());
                e.printStackTrace();
            }
            if (calc.isDone())
            {
                Caspian.info(Modules.AUTO_CRYSTAL, "Calc done for tick " +
                                "calculations in {}ms!", calc.getCalcTime());
                attackData = calc.getCalcAttack();
                final DamageData<BlockPos> calcPlace =
                        calc.getCalcPlace();
                mining = calcPlace != null && calcPlace.isMineDamage() ?
                    calcPlace.getSrc() : null;
                placeData = calcPlace;
                // IMPORTANT NOTE FOR ROTATIONS:
                // If we have found new data, stop the current rotation and
                // start a new one.
                // This means that if the rotations steps >= 1,
                // then there is a possibility that the current rotation may
                // "fall through" and never actually complete.
                if (rotating > 0 && isRotationModified())
                {
                    rotating = 0;
                    rotateTimer.setElapsedTime(Timer.MAX_TIME);
                }
                lastAttackData = attackData;
                lastPlaceData = placeData;
                // run calc
                final ArrayList<Entity> entities =
                        Lists.newArrayList(mc.world.getEntities());
                Vec3d pos = Managers.POSITION.getPos();
                Vec3d eyepos = Managers.POSITION.getEyePos();
                if (extrapolateRangeConfig.getValue())
                {
                    pos = extrapolatePosition(mc.player, pos,
                            mc.player.getBoundingBox(), selfExtrapolateTicksConfig.getValue());
                    eyepos = VecUtil.toEyePos(mc.player, pos);
                }
                final Iterable<EndCrystalEntity> crystals = getCrystalSphere(eyepos);
                final Iterable<BlockPos> blocks =
                        getSphere(placeRangeEyeConfig.getValue() ? eyepos : pos);
                processor.scheduleCalc(entities, crystals, blocks,
                        calcSleepConfig.getValue() * 1000L);
            }
        }
    }

    /**
     *
     */
    public void clear()
    {
        attackData = null;
        placeData = null;
        mining = null;
        preSequence = null;
        postSequence = null;
        sequence = null;
        lastAttackData = null;
        lastPlaceData = null;
        yawLimits = null;
        renderBreak.set(null);
        renderPlace.set(null);
        pauseCalcAttack = false;
        pauseCalcPlace = false;
        attacking = false;
        placing = false;
        rotateTarget = Rotate.OFF;
        tick = 0;
        shortTermTick = 0;
        shortTermCount = 0;
        rotating = 0;
        randomTime = -1;
        freq = 0;
        lastBreak.reset();
        lastPlace.reset();
        startSequence.reset();
        attacks.clear();
        explosions.clear();
        placements.clear();
        manuals.clear();
    }

    /**
     * Returns the latency between the time of the last attack and the time of
     * the corresponding crystal explosion, i.e. the crystal latency.
     *
     * @return The crystal latency in ms
     */
    @Override
    public String getMetaData()
    {
        return String.format("%dms", (int) getLatency(breakTimes));
    }

    /**
     *
     */
    private void cleanCrystals()
    {
        if (lastBreak.passed(1000))
        {
            attacks.clear();
            explosions.clear();
            placements.clear();
            return;
        }
        final float breakTimeout = Math.max(getLatency(breakTimes) + (50.0f * breakTimeoutConfig.getValue()),
                50.0f * minTimeoutConfig.getValue());
        attacks.entrySet().removeIf(t ->
        {
            long time = System.currentTimeMillis() - t.getValue();
            return time > breakTimeout;
        });
        final float explodeTimeout = breakTimeout + 50.0f;
        explosions.entrySet().removeIf(t ->
        {
            long time = System.currentTimeMillis() - t.getValue();
            return time > explodeTimeout;
        });
        final float placeTimeout = Math.max(getLatency(placeTimes) + (50.0f * placeTimeoutConfig.getValue()),
                50.0f * minTimeoutConfig.getValue());
        placements.entrySet().removeIf(t ->
        {
            long time = System.currentTimeMillis() - t.getValue();
            return time > placeTimeout;
        });
    }

    /**
     *
     *
     * @return
     */
    private boolean isRotationModified()
    {
        if (rotateMaxConfig.getValue() > 0
                && strictRotateConfig.getValue() != Rotate.OFF
                && lastPlaceData != null && lastAttackData != null
                && placeData != null && attackData != null)
        {
            float maxYaw = Math.max(rotateMaxConfig.getValue() * rotateLimitConfig.getValue(), 180.0f);
            if (rotateTarget == Rotate.FULL)
            {
                return !attackData.isSrcEqual(lastAttackData)
                        && attackData.getYawDiff(lastAttackData) > maxYaw;
            }
            else if (rotateTarget == Rotate.SEMI)
            {
                return !placeData.isSrcEqual(lastPlaceData)
                        && placeData.getYawDiff(lastPlaceData) > maxYaw;
            }
        }
        return false;
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onRunTick(RunTickEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (isRotationBlocked())
            {
                return;
            }
            if (attackDelayConfig.getValue() <= 0.0f)
            {
                return;
            }
            if (rotating != 0)
            {
                return;
            }
            attacking = attackData != null && attackData.isValid();
            if (attacking)
            {
                if (rotateConfig.getValue()
                        && checkFacing(attackData.getBoundingBox()))
                {
                    float[] dest = attackData.getRotations(Managers.POSITION.getEyePos());
                    rotating = setRotation(dest, strictRotateConfig.getValue() != Rotate.OFF);
                    rotateTimer.reset();
                    rotateTarget = Rotate.FULL;
                    return;
                }
                // This delay is accurate to ms
                if (randomTime < 0)
                {
                    randomTime = random.nextLong((long)
                            (randomSpeedConfig.getValue() * 10.0f + 1.0f));
                }
                float delay = attackDelayConfig.getValue() * 50.0f + randomTime;
                if (lastBreak.passed(delay))
                {
                    if (attack(attackData))
                    {
                        lastBreak.reset();
                        addCrystalsInRange(attackData);
                        countCrystals();
                        randomTime = -1;
                    }
                    attackData = null;
                }
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onMovementPackets(MovementPacketsEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getStage() == EventStage.PRE)
            {
                ++tick;
                // MAIN LOOP
                cleanCrystals();
                tickCalc();
                if (isRotationBlocked())
                {
                    return;
                }
                if (rotating > 0 && yawLimits != null)
                {
                    yaw = yawLimits[--rotating];
                }
                // rotateTimer.reset();
                if (!rotateTimer.passed(rotatePreserveTicksConfig.getValue()))
                {
                    setRotation(yaw, pitch);
                    event.cancel();
                }
                if (rotating != 0)
                {
                    return;
                }
                if (attackDelayConfig.getValue() <= 0.0f)
                {
                    attacking = attackData != null && attackData.isValid();
                    if (attacking)
                    {
                        if (rotateConfig.getValue()
                                && checkFacing(attackData.getBoundingBox()))
                        {
                            float[] dest = attackData.getRotations(Managers.POSITION.getEyePos());
                            rotating = setRotation(dest, strictRotateConfig.getValue() != Rotate.OFF);
                            rotateTimer.reset();
                            rotateTarget = Rotate.FULL;
                            // rotate instantly
                            if (!event.isCanceled() && yawLimits != null)
                            {
                                yaw = yawLimits[--rotating];
                                setRotation(yaw, pitch);
                                event.cancel();
                            }
                            return;
                        }
                        if (randomTime < 0)
                        {
                            randomTime = random.nextLong((long)
                                    (randomSpeedConfig.getValue() * 10.0f + 1.0f));
                        }
                        float delay = 1000.0f - breakSpeedConfig.getValue() * 50.0f
                                + randomTime;
                        if (instantConfig.getValue())
                        {
                            float timeout = Math.max(getLatency(breakTimes) + (50.0f * breakTimeoutConfig.getValue()),
                                    50.0f * minTimeoutConfig.getValue());
                            if (sequentialConfig.getValue() == Sequential.STRICT)
                            {
                                timeout += 50.0f;
                            }
                            delay = timeout;
                        }
                        if (lastBreak.passed(delay))
                        {
                            if (attack(attackData))
                            {
                                lastBreak.reset();
                                addCrystalsInRange(attackData.getId(),
                                        attackData.getPos());
                                countCrystals();
                                randomTime = -1;
                            }
                            attackData = null;
                        }
                    }
                }
                placing = placeData != null && placeData.isValid();
                if (placing)
                {
                    final Direction dir = placeData.getInteractDirection();
                    if (rotateConfig.getValue()
                            && checkFacing(placeData.getBoundingBox()))
                    {
                        float[] dest = offsetFacingConfig.getValue() ?
                                placeData.getRotations(Managers.POSITION.getEyePos(), dir) :
                                placeData.getRotations(Managers.POSITION.getEyePos());
                        rotating = setRotation(dest, strictRotateConfig.getValue() == Rotate.FULL);
                        rotateTimer.reset();
                        rotateTarget = Rotate.SEMI;
                        // rotate instantly
                        if (!event.isCanceled() && yawLimits != null)
                        {
                            yaw = yawLimits[--rotating];
                            setRotation(yaw, pitch);
                            event.cancel();
                        }
                        return;
                    }
                    float delay = 1000.0f - placeSpeedConfig.getValue() * 50.0f;
                    if (lastPlace.passed(delay))
                    {
                        if (place(placeData, dir))
                        {
                            lastPlace.reset();
                        }
                        placeData = null;
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (renderConfig.getValue() && getCrystalHand() != null)
            {
                final Color c = Modules.COLORS.getColor();
                int color = c.getRGB();
                if (renderAttackConfig.getValue())
                {
                    final Box rb = renderBreak.get();
                    if (rb != null)
                    {
                        RenderManager.renderBoundingBox(event.getMatrices(),
                                rb, 1.5f, color);
                    }
                }
                final BlockPos rp = renderPlace.get();
                if (rp != null)
                {
                    if (renderSpawn != null && renderSpawn.equals(rp)
                            && renderSpawnConfig.getValue())
                    {
                        int alpha = c.getAlpha() + 55;
                        color = (color & 0x00ffffff) | (alpha << 24);
                    }
                    RenderManager.renderBox(event.getMatrices(), rp, color);
                    RenderManager.renderBoundingBox(event.getMatrices(), rp, 1.5f, color);
                }
            }
        }
    }

    /**
     *
     *
     * @param attack
     */
    private void setAttackHard(final DamageData<EndCrystalEntity> attack)
    {
        this.attackData = attack;
        pauseCalcAttack = true;
    }

    /**
     *
     *
     * @param place
     */
    private void setPlaceHard(final DamageData<BlockPos> place)
    {
        this.placeData = place;
        pauseCalcPlace = true;
    }
    
    /**
     * Returns <tt>true</tt> if the {@link AutoCrystalModule} has found a
     * crystal to attack this tick.
     *
     * @return <tt>true</tt> if the AutoCrystal is attacking
     */
    public boolean isAttacking()
    {
        return attacking;
    }

    /**
     * Returns <tt>true</tt> if the {@link AutoCrystalModule} has found a
     * block to place a crystal on this tick.
     *
     * @return <tt>true</tt> if the AutoCrystal is placing
     */
    public boolean isPlacing()
    {
        return placing;
    }

    /**
     * Returns <tt>true</tt> if the {@link AutoCrystalModule} is rotation to
     * a block or crystal.
     *
     * @return <tt>true</tt> if the AutoCrystal is rotating
     */
    public boolean isRotating()
    {
        return rotating > 0;
    }

    /**
     * Returns the average time in ms of the last 20 break/place attempts. Logs
     * the time between the time of the sending of
     * {@link PlayerInteractEntityC2SPacket} and receiving of
     * {@link ExplosionS2CPacket}.
     *
     * @param times
     *
     * @return The average crystal latency time in ms
     */
    private float getLatency(final Deque<Long> times)
    {
        return switch (latencySyncConfig.getValue())
        {
            case CURRENT ->
            {
                for (long time : times)
                {
                    if (time > 1000)
                    {
                        continue;
                    }
                    yield time;
                }
                yield 0.0f;
            }
            case AVERAGE ->
            {
                int size = times.size();
                //
                float avg = 0.0f;
                for (long time : times)
                {
                    if (time > 1000)
                    {
                        size--;
                        continue;
                    }
                    avg += time;
                }
                if (size > 0)
                {
                    yield avg / size;
                }
                yield 0.0f;
            }
            case MINIMAL ->
            {
                float max = 0.0f;
                for (long time : times)
                {
                    if (time > 1000)
                    {
                        continue;
                    }
                    if (time > max)
                    {
                        max = time;
                    }
                }
                if (max < 1000)
                {
                    yield max;
                }
                yield 0.0f;
            }
            // MC calc ping
            case NONE -> Managers.NETWORK.getClientLatency();
        };
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.world != null && mc.player != null)
        {
            if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet
                    && !event.isCached())
            {
                ItemStack stack = mc.player.getStackInHand(packet.getHand());
                if (stack.getItem() == Items.END_CRYSTAL)
                {
                    final BlockHitResult result = packet.getBlockHitResult();
                    manuals.put(result.getBlockPos(),
                            System.currentTimeMillis());
                }
            }
            else if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket)
            {
                if (!event.isCached())
                {
                    autoSwapTimer.reset();
                }
                lastSwap.reset();
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.world != null && mc.player != null)
        {
            if (event.getPacket() instanceof EntityStatusS2CPacket packet)
            {
                if (packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING)
                {
                    final Entity e = packet.getEntity(mc.world);
                    if (e instanceof PlayerEntity p)
                    {
                        pops.put(p, System.currentTimeMillis());
                    }
                }
            }
            else if (event.getPacket() instanceof EntitySpawnS2CPacket packet)
            {
                // crystal spawned
                if (packet.getEntityType() == EntityType.END_CRYSTAL)
                {
                    // Position data of the player
                    Vec3d pos = Managers.POSITION.getPos();
                    Vec3d eyepos = Managers.POSITION.getEyePos();
                    if (extrapolateRangeConfig.getValue())
                    {
                        pos = extrapolatePosition(mc.player, pos,
                                mc.player.getBoundingBox(), selfExtrapolateTicksConfig.getValue());
                        eyepos = VecUtil.toEyePos(mc.player, pos);
                    }
                    //
                    final Vec3d cpos = new Vec3d(packet.getX() - 0.5,
                            packet.getY(), packet.getZ() - 0.5);
                    final BlockPos sblock = BlockPos.ofFloored(cpos);
                    renderSpawn = sblock;
                    if (instantConfig.getValue())
                    {
                        final Vec3d base = cpos.subtract(0.0, 1.0, 0.0);
                        if (preBreakRangeCheck(eyepos, cpos))
                        {
                            return;
                        }
                        cleanCrystals();
                        final BlockPos floor = BlockPos.ofFloored(base);
                        Long time = placements.remove(floor);
                        if (manualConfig.getValue() && manuals.containsKey(floor))
                        {
                            time = manuals.remove(floor);
                        }
                        if (time != null)
                        {
                            if (mining != null && mining.equals(floor))
                            {
                                return;
                            }
                            placeTimes.push(System.currentTimeMillis() - time);
                            if (rotateConfig.getValue()
                                    && checkFacing(new Box(sblock)))
                            {
                                // Can't take advantage of instant break,
                                // most that can be done is signal to
                                // clear the next break ASAP
                                if (strictRotateConfig.getValue() != Rotate.OFF
                                        || attackDelayConfig.getValue() > 0.0f)
                                {
                                    lastBreak.setElapsedTime(Timer.MAX_TIME);
                                    return;
                                }
                                attacking = true;
                                float[] dest = RotationUtil.getRotationsTo(Managers.POSITION.getEyePos(),
                                        cpos);
                                rotating = setRotation(dest, strictRotateConfig.getValue() != Rotate.OFF);
                                rotateTimer.reset();
                                rotateTarget = Rotate.FULL;
                            }
                            if (attack(packet.getId(), cpos))
                            {
                                lastBreak.reset();
                                addCrystalsInRange(packet.getId(), cpos);
                                countCrystals();
                            }
                        }
                        else if (instantCalcConfig.getValue())
                        {
                            final double local = getDamage(mc.player, cpos);
                            // player safety
                            boolean unsafe = false;
                            if (safetyConfig.getValue() && !mc.player.isCreative())
                            {
                                float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                                unsafe = local + 0.5 > health
                                        || local > maxLocalDamageConfig.getValue();
                            }
                            for (Entity e : mc.world.getEntities())
                            {
                                if (e != null && e != mc.player && e.isAlive()
                                        && !Managers.SOCIAL.isFriend(e.getUuid()))
                                {
                                    if (e instanceof EndCrystalEntity)
                                    {
                                        continue;
                                    }
                                    Position tpos = e.getPos();
                                    if (e instanceof PlayerEntity player
                                            && latencyPositionConfig.getValue())
                                    {
                                        FakePlayerEntity t = Managers.LATENCY.getTrackedPlayer(pos,
                                                player, maxLatencyConfig.getValue());
                                        if (t != null)
                                        {
                                            tpos = t.getPos();
                                        }
                                    }
                                    double dist = cpos.squaredDistanceTo(tpos.getX(),
                                            tpos.getY(), tpos.getZ());
                                    if (dist > 144.0f)
                                    {
                                        continue;
                                    }
                                    double tdist = pos.squaredDistanceTo(tpos.getX(),
                                            tpos.getY(), tpos.getZ());
                                    // double edist = e.squaredDistanceTo(p.toCenterPos());
                                    if (tdist > targetRangeConfig.getValue()
                                            * targetRangeConfig.getValue())
                                    {
                                        continue;
                                    }
                                    if (isEnemy(e))
                                    {
                                        final double dmg = getDamage(e, cpos);
                                        if (safetyBalanceConfig.getValue() && dmg < local)
                                        {
                                            continue;
                                        }
                                        float ehealth = 0.0f;
                                        if (e instanceof LivingEntity)
                                        {
                                            ehealth = ((LivingEntity) e).getHealth()
                                                    + ((LivingEntity) e).getAbsorptionAmount();
                                        }
                                        final DamageData<BlockPos> pdata =
                                                new DamageData<>(e,
                                                        BlockPos.ofFloored(cpos),
                                                        dmg, local);
                                        final DamageData<EndCrystalEntity> data =
                                                getSrcData(pdata, packet.getId());
                                        if (checkAntiTotem(e, ehealth, dmg))
                                        {
                                            data.addTag("antitotem");
                                        }
                                        if (checkLethal(e, ehealth, dmg))
                                        {
                                            data.addTag("lethal");
                                            if (safetyOverride.getValue())
                                            {
                                                unsafe = false;
                                            }
                                        }
                                        final List<ItemStack> armor =
                                                Lists.newArrayList(e.getArmorItems());
                                        if (!armor.isEmpty() && checkArmor(e, armor))
                                        {
                                            data.addTag("armorbreak");
                                        }
                                        double mindmg = attackData.getDamage();
                                        if (!unsafe && (instantMaxConfig.getValue()
                                                && data.getDamage() >= mindmg
                                                || data.isDamageValid(instantDamageConfig.getValue())))
                                        {
                                            if (rotateConfig.getValue()
                                                    && checkFacing(new Box(sblock)))
                                            {
                                                if (strictRotateConfig.getValue() != Rotate.OFF
                                                        || attackDelayConfig.getValue() > 0.0f)
                                                {
                                                    lastBreak.setElapsedTime(Timer.MAX_TIME);
                                                    return;
                                                }
                                                attacking = true;
                                                float[] dest = RotationUtil.getRotationsTo(
                                                        Managers.POSITION.getEyePos(),
                                                        cpos);
                                                rotating = setRotation(dest, strictRotateConfig.getValue() != Rotate.OFF);
                                                rotateTimer.reset();
                                            }
                                            if (attack(packet.getId(), cpos))
                                            {
                                                lastBreak.reset();
                                                addCrystalsInRange(packet.getId(),
                                                        cpos);
                                                countCrystals();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof BlockUpdateS2CPacket packet)
            {
                // Player position data
                Vec3d pos = Managers.POSITION.getPos();
                Vec3d eyepos = placeRangeEyeConfig.getValue() ?
                        Managers.POSITION.getEyePos() : pos;
                // Position data of the player
                if (extrapolateRangeConfig.getValue())
                {
                    pos = extrapolatePosition(mc.player, pos,
                            mc.player.getBoundingBox(), selfExtrapolateTicksConfig.getValue());
                    eyepos = VecUtil.toEyePos(mc.player, pos);
                }
                //
                final BlockPos block = packet.getPos();
                final BlockState state = packet.getState();
                final BlockPos mine = Modules.SPEEDMINE.getBlockTarget();
                if (mine != null && mine.equals(block) && state.isAir())
                {
                    for (Entity e : mc.world.getEntities())
                    {
                        if (e != null && e.isAlive()
                                && e instanceof EndCrystalEntity crystal)
                        {
                            if (preBreakRangeCheck(eyepos, crystal.getPos()))
                            {
                                continue;
                            }
                            if (mine.toCenterPos().distanceTo(crystal.getPos()) < 12.0)
                            {
                                if (rotateConfig.getValue()
                                        && checkFacing(crystal.getBoundingBox()))
                                {
                                    float[] dest = RotationUtil.getRotationsTo(
                                            Managers.POSITION.getEyePos(),
                                            crystal.getPos());
                                    rotating = setRotation(dest, strictRotateConfig.getValue() != Rotate.OFF);
                                    rotateTimer.reset();
                                    rotateTarget = Rotate.FULL;
                                    if (strictRotateConfig.getValue() != Rotate.OFF
                                            || attackDelayConfig.getValue() > 0.0f)
                                    {
                                        setAttackHard(new DamageData<>(null,
                                                crystal, -1.0, -1.0));
                                        return;
                                    }
                                }
                                attacking = true;
                                if (attack(crystal.getId(), crystal.getPos()))
                                {
                                    lastBreak.reset();
                                    addCrystalsInRange(crystal.getId(),
                                            crystal.getPos());
                                    countCrystals();
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (sequence != null && sequentialConfig.getValue() != Sequential.NONE
                    && sequentialConfig.getValue() != Sequential.FAST)
            {
                boolean sequential = false;
                double dx = 0.0;
                double dy = 0.0;
                double dz = 0.0;
                float radius = -1.0f;
                if (event.getPacket() instanceof PlaySoundS2CPacket packet)
                {
                    if (packet.getSound().value() == SoundEvents.ENTITY_GENERIC_EXPLODE)
                    {
                        dx = packet.getX();
                        dy = packet.getY();
                        dz = packet.getZ();
                        radius = 12.0f;
                    }
                }
                else if (event.getPacket() instanceof ExplosionS2CPacket packet)
                {
                    dx = packet.getX();
                    dy = packet.getY();
                    dz = packet.getZ();
                    radius = packet.getRadius();
                }
                else if (event.getPacket() instanceof EntitiesDestroyS2CPacket packet)
                {
                    for (int id : packet.getEntityIds())
                    {
                        if (id == sequence.getId())
                        {
                            sequential = true;
                            break;
                        }
                    }
                }
                if (radius != -1.0f)
                {
                    sequential = sequence.squaredDistanceTo(dx, dy, dz) < radius * radius;
                    for (Entity e : mc.world.getEntities())
                    {
                        if (e != null && e.isAlive() && e instanceof EndCrystalEntity crystal
                                && crystal.squaredDistanceTo(dx, dy, dz) < radius * radius)
                        {
                            final Long breakTime =
                                    attacks.remove(crystal. getId());
                            if (breakTime != null)
                            {
                                // crystal.kill();
                                crystal.remove(Entity.RemovalReason.KILLED);
                                crystal.onRemoved();
                                breakTimes.push(System.currentTimeMillis() - breakTime);
                            }
                        }
                    }
                }
                if (sequential)
                {
                    if (preSequentialCalcConfig.getValue() && preSequence != null)
                    {
                        final Vec3d pos = Managers.POSITION.getPos();
                        //
                        final BlockPos dpos = preSequence;
                        final Vec3d src = toSource(dpos);
                        final double local = getDamage(mc.player, src);
                        // player safety
                        boolean unsafe = false;
                        if (safetyConfig.getValue() && !mc.player.isCreative())
                        {
                            float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                            unsafe = local + 0.5 > health
                                    || local > maxLocalDamageConfig.getValue();
                        }
                        for (Entity e : mc.world.getEntities())
                        {
                            if (e != null && e != mc.player && e.isAlive()
                                    && !Managers.SOCIAL.isFriend(e.getUuid()))
                            {
                                if (e instanceof EndCrystalEntity)
                                {
                                    continue;
                                }
                                if (isEnemy(e))
                                {
                                    Position tpos = e.getPos();
                                    if (e instanceof PlayerEntity player
                                            && latencyPositionConfig.getValue())
                                    {
                                        FakePlayerEntity t = Managers.LATENCY.getTrackedPlayer(pos,
                                                player, maxLatencyConfig.getValue());
                                        if (t != null)
                                        {
                                            tpos = t.getPos();
                                        }
                                    }
                                    double dist = src.squaredDistanceTo(tpos.getX(),
                                            tpos.getY(), tpos.getZ());
                                    if (dist > 144.0f)
                                    {
                                        continue;
                                    }
                                    double tdist = pos.squaredDistanceTo(tpos.getX(),
                                            tpos.getY(), tpos.getZ());
                                    // double edist = e.squaredDistanceTo(p.toCenterPos());
                                    if (tdist > targetRangeConfig.getValue()
                                            * targetRangeConfig.getValue())
                                    {
                                        continue;
                                    }
                                    final double dmg = getDamage(e, src);
                                    if (safetyBalanceConfig.getValue() && dmg < local)
                                    {
                                        continue;
                                    }
                                    float ehealth = 0.0f;
                                    if (e instanceof LivingEntity)
                                    {
                                        ehealth = ((LivingEntity) e).getHealth()
                                                + ((LivingEntity) e).getAbsorptionAmount();
                                    }
                                    final DamageData<BlockPos> data =
                                            new DamageData<>(e, preSequence,
                                                    dmg, local);
                                    if (checkAntiTotem(e, ehealth, dmg))
                                    {
                                        data.addTag("antitotem");
                                    }
                                    if (checkLethal(e, ehealth, dmg))
                                    {
                                        data.addTag("lethal");
                                        if (safetyOverride.getValue())
                                        {
                                            unsafe = false;
                                        }
                                    }
                                    final List<ItemStack> armor =
                                            Lists.newArrayList(e.getArmorItems());
                                    if (!armor.isEmpty() && checkArmor(e, armor))
                                    {
                                        data.addTag("armorbreak");
                                    }
                                    if (data.isDamageValid(minDamageConfig.getValue())
                                            && !unsafe)
                                    {
                                       placing = true;
                                       setPlaceHard(data);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        tickCalc();
                        placing = placeData != null && placeData.isValid();
                    }
                    if (placing)
                    {
                        final Direction dir = placeData.getInteractDirection();
                        if (rotateConfig.getValue()
                                && checkFacing(placeData.getBoundingBox()))
                        {
                            if (strictRotateConfig.getValue() != Rotate.OFF)
                            {
                                lastPlace.setElapsedTime(Timer.MAX_TIME);
                                return;
                            }
                            float[] dest = offsetFacingConfig.getValue() ?
                                    placeData.getRotations(Managers.POSITION.getEyePos(), dir) :
                                    placeData.getRotations(Managers.POSITION.getEyePos());
                            rotating = setRotation(dest, strictRotateConfig.getValue() == Rotate.FULL);
                            rotateTimer.reset();
                            rotateTarget = Rotate.SEMI;
                        }
                        if (place(placeData, dir))
                        {
                            lastPlace.reset();
                        }
                        placeData = null;
                    }
                    // End sequence after place
                    preSequence = null;
                    sequence = null;
                    postSequence = null;
                }
            }
        }
    }

    /**
     *
     *
     * @param data
     */
    private void addCrystalsInRange(final DamageData<EndCrystalEntity> data)
    {
        addCrystalsInRange(data.getId(), data.getPos());
    }

    /**
     *
     *
     * @param id
     * @param pos
     */
    private void addCrystalsInRange(final int id, final Vec3d pos)
    {
        attacks.put(id, System.currentTimeMillis());
        for (Entity entity : mc.world.getEntities())
        {
            if (entity instanceof EndCrystalEntity crystal
                    && pos.squaredDistanceTo(crystal.getPos()) < 144.0f)
            {
                explosions.put(crystal.getId(), System.currentTimeMillis());
            }
        }
    }

    /**
     *
     *
     * @return
     */
    private int countCrystals()
    {
        final int attacks = attackFreq.get(freq) + 1;
        long interval = freqInterval.getElapsedTime() / 500;
        if (interval > 0)
        {
            freq += interval;
            freqInterval.reset();
        }
        if (freq > 15)
        {
            Collections.fill(attackFreq, 0);
            freq = 0;
        }
        attackFreq.set(freq, attacks);
        return attacks;
    }

    /**
     *
     *
     * @param pdata
     * @param id
     * @return
     */
    private DamageData<EndCrystalEntity> getSrcData(final DamageData<BlockPos> pdata,
                                                    final int id)
    {
        EndCrystalEntity fakeCrystal = new EndCrystalEntity(mc.world,
                pdata.getX(), pdata.getY(), pdata.getZ());
        fakeCrystal.setId(id);
        return new DamageData<>(pdata.getTarget(), fakeCrystal,
                pdata.getDamage(), pdata.getLocal());
    }

    /**
     *
     *
     * @param data
     * @return
     */
    private boolean attack(DamageData<EndCrystalEntity> data)
    {
        return attack(data.getId(), data.getPos());
    }

    /**
     *
     *
     * @param e
     * @param pos
     * @return
     * 
     * @see #attackDirect(int, Vec3d)
     * @see #preAttackCheck(int) 
     */
    private boolean attack(int e, Vec3d pos)
    {
        if (preAttackCheck(e))
        {
            StatusEffectInstance weakness =
                    mc.player.getStatusEffect(StatusEffects.WEAKNESS);
            StatusEffectInstance strength =
                    mc.player.getStatusEffect(StatusEffects.STRENGTH);
            if (antiWeaknessConfig.getValue() != Swap.OFF && weakness != null
                    && (strength == null || weakness.getAmplifier() > strength.getAmplifier()))
            {
                int slot = -1;
                for (int i = 0; i < 9; ++i)
                {
                    ItemStack stack = mc.player.getInventory().getStack(i);
                    if (!stack.isEmpty() && (stack.getItem() instanceof SwordItem
                            || stack.getItem() instanceof AxeItem
                            || stack.getItem() instanceof PickaxeItem))
                    {
                        slot = i;
                        break;
                    }
                }
                if (slot != -1)
                {
                    int prev = mc.player.getInventory().selectedSlot;
                    boolean swapped = false;
                    if (preSwapCheck())
                    {
                        swapped = autoSwapConfig.getValue() != Swap.SILENT_ALT ?
                            swap(slot) : swapAlt(slot + 36);
                    }
                    if (swapped)
                    {
                        attackDirect(e, pos);
                        if (antiWeaknessConfig.getValue() == Swap.SILENT)
                        {
                            swap(prev);
                        }
                        else if (antiWeaknessConfig.getValue() == Swap.SILENT_ALT)
                        {
                            swapAlt(slot + 36);
                            lastSwapAlt.reset();
                        }
                    }
                }
            }
            attackDirect(e, pos);
            return true;
        }
        return false;
    }

    /**
     * Attacks the {@link Entity} associated with the param id and swings the
     * player {@link Hand}.
     * 
     * @param id The target entity id
     * @param pos
     *
     * @see PlayerInteractEntityC2SPacket
     */
    private void attackDirect(int id, Vec3d pos)
    {
        // retarded hack to set entity id
        final EndCrystalEntity crystalEntity = new EndCrystalEntity(mc.world,
                pos.getX(), pos.getY(), pos.getZ());
        crystalEntity.setId(id);
        PlayerInteractEntityC2SPacket packet = PlayerInteractEntityC2SPacket.attack(crystalEntity,
                        mc.player.isSneaking());
        // ((AccessorPlayerInteractEntityC2SPacket) packet).hookSetEntityId(id);
        Managers.NETWORK.sendPacket(packet);
        Hand hand = getCrystalHand();
        swingDirect(hand != null ? hand : Hand.MAIN_HAND);
        if (sequentialConfig.getValue() == Sequential.FAST)
        {
            tickCalc();
            placing = placeData != null && placeData.isValid();
            if (placing)
            {
                final Direction dir = placeData.getInteractDirection();
                if (rotateConfig.getValue()
                        && checkFacing(placeData.getBoundingBox()))
                {
                    if (strictRotateConfig.getValue() != Rotate.OFF)
                    {
                        lastPlace.setElapsedTime(Timer.MAX_TIME);
                        return;
                    }
                    float[] dest = offsetFacingConfig.getValue() ?
                            placeData.getRotations(Managers.POSITION.getEyePos(), dir) :
                            placeData.getRotations(Managers.POSITION.getEyePos());
                    rotating = setRotation(dest, strictRotateConfig.getValue() == Rotate.FULL);
                    rotateTimer.reset();
                    rotateTarget = Rotate.SEMI;
                }
                if (place(placeData, dir))
                {
                    lastPlace.reset();
                }
                placeData = null;
            }
        }
        if (preSequence != null
                && pos.squaredDistanceTo(toSource(preSequence)) < 0.25f)
        {
            sequence = crystalEntity;
            startSequence.reset();
        }
    }

    /**
     *
     *
     * @param e
     * @return
     */
    private boolean preAttackCheck(int e)
    {
        if (inhibitConfig.getValue() != Inhibit.NONE)
        {
            int hits = 0;
            for (int i = 0; i < freq; i++)
            {
                hits += attackFreq.get(i);
            }
            int limit = attackFreqConfig.getValue();
            // May need configs in the future for TWO and FOUR but afaik
            // the times can be usually be derived from ONE
            int attackFreqTwo = attackFreqFullConfig.getValue() * 2;
            int attackFreqFour = attackFreqTwo * 2;
            if (freq == 1)
            {
                limit = attackFreqFullConfig.getValue();
            }
            else if (freq < 4)
            {
                limit = attackFreqTwo;
            }
            else if (freq < 8)
            {
                limit = attackFreqFour;
            }
            else if (freq < 16)
            {
                limit = attackFreqMaxConfig.getValue();
            }
            if (hits - limit > 0)
            {
                return false;
            }
            final Long time = attacks.get(e);
            if (time != null)
            {
                if (inhibitConfig.getValue() == Inhibit.FULL)
                {
                    float total = attacks.size() * 1000.0f / 2000.0f;
                    // Short term
                    if (tick < shortTermTick)
                    {
                        shortTermTick = tick;
                        shortTermCount = 1;
                    }
                    else if (tick - shortTermTick < inhibitTicksConfig.getValue())
                    {
                        shortTermCount++;
                    }
                    else
                    {
                        shortTermTick = tick;
                        shortTermCount = 1;
                    }
                    float shortTerm = (float) shortTermCount * 1000.0f
                            / (50.0f * inhibitTicksConfig.getValue());
                    float max = Math.max(shortTerm, total);
                    return max > inhibitLimitConfig.getValue();
                }
                return true;
            }
        }
        if (getCrystalHand() != Hand.OFF_HAND)
        {
            if (multitaskConfig.getValue())
            {
                return !mc.player.isUsingItem();
            }
            if (whileMiningConfig.getValue())
            {
                return !Managers.INTERACT.isBreakingBlock();
            }
        }
        float swapDelay = swapDelayConfig.getValue() * 25.0f;
        return lastSwap.passed(swapDelay);
    }

    /**
     * Returns <tt>true</tt> if the placement of an {@link EndCrystalItem} on
     * the param position was successful.
     *
     * @param data The position data to place the crystal
     * @param dir The placement interact direction
     * @return <tt>true</tt> if the placement was successful
     *
     * @see #placeDirect(BlockPos, Hand, BlockHitResult)
     * @see #prePlaceCheck()
     */
    private boolean place(final DamageData<BlockPos> data,
                          final Direction dir)
    {
        final BlockPos p = data.getSrc();
        if (prePlaceCheck())
        {
            BlockHitResult result = new BlockHitResult(p.toCenterPos(), dir,
                    p,  false);
            Hand hand = getCrystalHand();
            if (hand != null)
            {
                Managers.INVENTORY.syncSelectedSlot();
                placeDirect(p, hand, result);
                postSequence = null;
                preSequence = p;
                tickSequence.reset();
            }
            else if (autoSwapConfig.getValue() != Swap.OFF)
            {
                int slot = getCrystalSlot();
                int prev = mc.player.getInventory().selectedSlot;
                if (slot != -1)
                {
                    boolean swapped = false;
                    if (preSwapCheck())
                    {
                        swapped = autoSwapConfig.getValue() != Swap.SILENT_ALT ?
                                swap(slot) : swapAlt(slot + 36);
                    }
                    if (swapped)
                    {
                        placeDirect(p, Hand.MAIN_HAND, result);
                        postSequence = null;
                        preSequence = p;
                        tickSequence.reset();
                        if (autoSwapConfig.getValue() == Swap.SILENT)
                        {
                            swap(prev);
                        }
                        else if (autoSwapConfig.getValue() == Swap.SILENT_ALT)
                        {
                            swapAlt(slot + 36);
                            lastSwapAlt.reset();
                        }
                    }
                }
            }
            return true; // success
        }
        return false;
    }

    /**
     *
     *
     * @param p
     * @param hand
     * @param result
     *
     * @see PlayerInteractBlockC2SPacket
     */
    private void placeDirect(BlockPos p, Hand hand, BlockHitResult result)
    {
        Managers.NETWORK.sendSequencedPacket(id ->
                new PlayerInteractBlockC2SPacket(hand, result, id));
        swingDirect(hand);
        placements.put(p, System.currentTimeMillis());
    }

    /**
     *
     *
     * @return
     */
    public boolean prePlaceCheck()
    {
        if (mc.options.useKey.isPressed() || mc.options.attackKey.isPressed())
        {
            autoSwapTimer.reset();
        }
        if (sequentialConfig.getValue() == Sequential.STRICT)
        {
            float timeout = Math.max(getLatency(breakTimes) + (50.0f * breakTimeoutConfig.getValue()),
                    50.0f * minTimeoutConfig.getValue());
            if (startSequence.passed(timeout))
            {
                Caspian.error("Latest sequence timed out!");
                sequence = null;
            }
            return sequence == null;
        }
        return true;
    }

    /**
     *
     *
     * @return
     */
    public boolean preSwapCheck()
    {
        if (autoSwapConfig.getValue() == Swap.NORMAL)
        {
            return autoSwapTimer.passed(500);
        }
        return true;
    }

    /**
     *
     *
     * @param hand
     */
    private void swingDirect(Hand hand)
    {
        if (swingConfig.getValue() && !mc.player.handSwinging
                || mc.player.handSwingTicks >= PlayerUtil.getHandSwingDuration() / 2
                || mc.player.handSwingTicks < 0)
        {
            mc.player.handSwinging = true;
            mc.player.handSwingTicks = -1;
            mc.player.preferredHand = Modules.SWING.isEnabled() ?
                    Modules.SWING.getSwingHand() : hand;
        }
        Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
    }

    //
    private static final Box FULL_CRYSTAL_BB = new Box(0.0, 0.0, 0.0, 1.0, 2.0,
            1.0);
    private static final Box HALF_CRYSTAL_BB = new Box(0.0, 0.0, 0.0, 1.0, 1.0,
            1.0);

    /**
     * Returns <tt>true</tt> if an {@link EndCrystalItem} can be used on the
     * param {@link BlockPos}.
     *
     * @param p The block pos
     * @return Returns <tt>true</tt> if the crystal item can be placed on the
     * block
     */
    public boolean canUseOnBlock(BlockPos p)
    {
        BlockState state = mc.world.getBlockState(p);
        if (!state.isOf(Blocks.OBSIDIAN) && !state.isOf(Blocks.BEDROCK))
        {
            return false;
        }
        else
        {
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
                List<Entity> list = getCollisions(new Box(d, e, f,
                        d + bb.maxX, e + bb.maxY, f + bb.maxZ));
                return list.isEmpty();
            }
        }
    }

    /**
     * The {@link EndCrystalItem} cannot be placed on a {@link Box} with
     * other end crystals. However, we can predict

     * @param box
     * @return
     */
    private List<Entity> getCollisions(Box box)
    {
        final List<Entity> collisions = new CopyOnWriteArrayList<>(
                mc.world.getOtherEntities(null, box));
        //
        for (Entity e : collisions)
        {
            if (e == null || !e.isAlive() || e instanceof ExperienceOrbEntity)
            {
                collisions.remove(e);
            }
            else if (e instanceof EndCrystalEntity)
            {
                final long ticks = e.age * 50L;
                float timeout = Math.max(getLatency(breakTimes) + (50.0f * breakTimeoutConfig.getValue()),
                        50.0f * minTimeoutConfig.getValue());
                if (attacks.containsKey(e.getId()) && ticks < timeout)
                {
                    collisions.remove(e);
                }
                timeout += 50.0f;
                if (explosions.containsKey(e.getId()) && ticks < timeout)
                {
                    collisions.remove(e);
                }
            }
        }
        return collisions;
    }

    /**
     * Returns <tt>true</tt> if the {@link Entity} is a valid enemy to attack.
     *
     * @param e The potential enemy entity
     * @return <tt>true</tt> if the entity is an enemy
     *
     * @see EntityUtil
     */
    private boolean isEnemy(Entity e)
    {
        return e instanceof PlayerEntity && playersConfig.getValue()
                || EntityUtil.isMonster(e) && monstersConfig.getValue()
                || EntityUtil.isNeutral(e) && neutralsConfig.getValue()
                || EntityUtil.isPassive(e) && animalsConfig.getValue();
    }

    /**
     * Returns the {@link Hand} that is holding a {@link EndCrystalItem}
     * (prioritizing the offhand) or <tt>null</tt> if the player is not
     * holding an end crystal.
     * 
     * @return The hand that is holding an end crystal
     */
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

    /**
     * Returns the hotbar slot of an {@link EndCrystalItem}, if there is none
     * in the hotbar, then returns -1.
     *
     * @return The hotbar slot of an End Crystal item
     */
    private int getCrystalSlot()
    {
        int slot = -1;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty())
            {
                continue;
            }
            if (stack.getItem() instanceof EndCrystalItem)
            {
                slot = i;
                break;
            }
        }
        return slot;
    }

    /**
     * 
     * 
     * @param slot
     */
    private boolean swap(int slot)
    {
        if (slot < 9)
        {
            mc.player.getInventory().selectedSlot = slot;
            Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            return true;
        }
        return false;
    }

    /**
     *
     *
     * @param slot
     */
    private boolean swapAlt(int slot)
    {
        float delay = 1000.0f - alternateSpeedConfig.getValue() * 50.0f;
        if (slot > 35 && slot < 45 && lastSwapAlt.passed(delay))
        {
            final ScreenHandler screenHandler = mc.player.currentScreenHandler;
            final DefaultedList<Slot> slots = screenHandler.slots;
            // screenHandler.onSlotClick(slotId, button, actionType, player);
            int server = Managers.INVENTORY.getServerSlot() + 36;
            final Int2ObjectMap<ItemStack> diffs =
                    new Int2ObjectOpenHashMap<>();
            final ItemStack i1 = slots.get(slot).getStack();
            final ItemStack i2 = slots.get(server).getStack();
            diffs.put(slot, i1.copy());
            diffs.put(server, i2.copy());
            Managers.NETWORK.sendPacket(new ClickSlotC2SPacket(0,
                    screenHandler.getRevision(), slot, server,
                    SlotActionType.SWAP, ItemStack.EMPTY, diffs));
            // lastSwapAlt.reset();
            // if (swapSyncConfig.getValue())
            // {
            //     Managers.INVENTORY.syncSelectedSlot();
            // }
            return true;
        }
        return false;
    }

    /**
     * Returns the {@link Vec3d} position of the damage source given a
     * {@link BlockPos} base.
     *
     * @param base The source base
     * @return The vector position of the damage src
     */
    private Vec3d toSource(final BlockPos base)
    {
        return Vec3d.ofBottomCenter(base).add(0.0, 1.5, 0.0);
    }

    /**
     *
     *
     * @param e
     * @return
     */
    private BlockPos toBase(final EndCrystalEntity e)
    {
        return e.getBlockPos().down();
    }

    //
    private static final Direction[] HORIZONTALS = new Direction[]
            {
                    Direction.EAST, Direction.WEST, Direction.SOUTH,
                    Direction.NORTH
            };

    /**
     * Returns <tt>true</tt> if the given {@link BlockPos} position is at the
     * feet of the given {@link PlayerEntity}.
     *
     * @param e The player entity
     * @param p The position
     * @return Returns <tt>true</tt> if the given position is at the feet of
     * the given player
     */
    private boolean isFeet(final PlayerEntity e, final BlockPos p)
    {
        if (canUseOnBlock(p))
        {
            BlockPos feet = p.up(); // src
            for (Direction d : HORIZONTALS)
            {
                BlockPos off = feet.offset(d);
                if (mc.world.getOtherEntities(mc.player, new Box(off))
                        .contains(e))
                {
                    return true;
                }
                BlockPos off2 = off.offset(d);
                if (mc.world.getOtherEntities(mc.player, new Box(off2))
                        .contains(e))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     *
     * @param origin
     * @return
     */
    private List<EndCrystalEntity> getCrystalSphere(final Vec3d origin)
    {
        final List<EndCrystalEntity> sphere = new ArrayList<>();
        for (Entity e : mc.world.getEntities())
        {
            if (e != null && e.isAlive() && e instanceof EndCrystalEntity crystal)
            {
                if (preBreakRangeCheck(origin, crystal.getPos()))
                {
                    continue;
                }
                sphere.add(crystal);
            }
        }
        return sphere;
    }

    /**
     *
     *
     * @param origin
     * @return
     */
    private List<BlockPos> getSphere(final Vec3d origin)
    {
        final List<BlockPos> sphere = new ArrayList<>();
        double rad = Math.ceil(placeRangeConfig.getValue());
        for (double x = -rad; x <= rad; ++x)
        {
            for (double y = -rad; y <= rad; ++y)
            {
                for (double z = -rad; z <= rad; ++z)
                {
                    final Vec3i pos = new Vec3i((int) (origin.getX() + x),
                            (int) (origin.getY() + y), (int) (origin.getZ() + z));
                    final BlockPos p = new BlockPos(pos);
                    if (canUseOnBlock(p))
                    {
                        if (prePlaceRangeCheck(origin, p))
                        {
                            continue;
                        }
                        sphere.add(p);
                    }
                }
            }
        }
        return sphere;
    }

    /**
     *
     *
     * @param entity
     * @param pos
     * @param bb
     * @param ticks
     * @return
     */
    private Vec3d extrapolatePosition(LivingEntity entity, Vec3d pos, Box bb, int ticks)
    {
        Vec3d motion = getMovementVelocity(entity);
        //
        boolean xcoll = false;
        boolean ycoll = false;
        boolean zcoll = false;
        for (int i = 0; i < ticks; i++)
        {
            // Motion vector comp
            double x = motion.x;
            double y = motion.y;
            double z = motion.z;
            final Box xoff = bb.offset(x, 0.0, 0.0);
            if (!xcoll && !isInsideBlock(xoff))
            {
                bb = xoff;
                xcoll = true;
            }
            final Box yoff = bb.offset(0.0, y, 0.0);
            if (!ycoll && !isInsideBlock(yoff))
            {
                bb = yoff;
                ycoll = true;
            }
            final Box zoff = bb.offset(0.0, 0.0, z);
            if (!zcoll && !isInsideBlock(zoff))
            {
                bb = zoff;
                zcoll = true;
            }
            pos = pos.add(x, y, z);
            if (extrapolateGravityConfig.getValue())
            {
                y -= 0.08;
            }
            // Drag
            // x *= 0.91;
            y *= 0.9800000190734863;
            // z *= 0.91;
            motion = new Vec3d(x, y, z);
        }
        return pos;
    }

    /**
     *
     *
     * @param entity
     * @return
     */
    private Vec3d getMovementVelocity(final LivingEntity entity)
    {
        final Vec3d input = new Vec3d(entity.sidewaysSpeed, entity.upwardSpeed,
                entity.forwardSpeed);
        double d = input.lengthSquared();
        if (d < 1.0e-7)
        {
            return Vec3d.ZERO;
        }
        else
        {
            float yaw = entity.getYaw();
            float slipperiness = Blocks.DIRT.getSlipperiness();
            float speed = entity.isOnGround() ? entity.getMovementSpeed()
                    * (0.21600002f / (slipperiness * slipperiness * slipperiness)) : 0.02f;
            final Vec3d vec3d = (d > 1.0 ? input.normalize() : input).multiply(speed);
            float f = MathHelper.sin(yaw * 0.017453292f);
            float g = MathHelper.cos(yaw * 0.017453292f);
            return new Vec3d(vec3d.x * (double) g - vec3d.z * (double) f,
                    vec3d.y, vec3d.z * (double) g + vec3d.x * (double) f);
        }
    }

    /**
     *
     *
     * @param bb
     * @return
     */
    private boolean isInsideBlock(final Box bb)
    {
        return BlockPos.stream(bb).anyMatch((pos) ->
        {
            BlockState state = mc.world.getBlockState(pos);
            return !state.isAir();
        });
    }

    /**
     * Returns <tt>true</tt> if the crystals {@link Vec3d} position is
     * outside the player's maximum break range.
     *
     * @param pos The player position
     * @param cpos The crystal position
     * @return <tt>true</tt> if the crystal is outside the break range
     */
    private boolean preBreakRangeCheck(final Vec3d pos,
                                       final Vec3d cpos)
    {
        double dist = pos.distanceTo(cpos);
        if (dist > breakRangeConfig.getValue() * breakRangeConfig.getValue()
                && !isWithinStrictRange(pos, cpos,
                strictBreakRangeConfig.getValue()))
        {
            return true;
        }
        final double yoff = Math.abs(cpos.getY() - pos.getY());
        if (yoff > maxYOffsetConfig.getValue())
        {
            return true;
        }
        final BlockHitResult result = mc.world.raycast(new RaycastContext(
                Managers.POSITION.getCameraPosVec(1.0f),
                cpos, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player));
        return result != null && dist > breakWallRangeConfig.getValue();
    }

    /**
     *
     * @param entities
     * @param crystals
     * @param pos
     * @return
     *
     * @see #getDamage(Entity, Vec3d)
     * @see #getCrystalSphere(Vec3d)
     */
    private DamageData<EndCrystalEntity> getCrystal(Iterable<Entity> entities,
                                                    Iterable<EndCrystalEntity> crystals,
                                                    Vec3d pos)
    {
        if (mc.world != null && mc.player != null)
        {
            if (pauseCalcAttack && attackData != null)
            {
                pauseCalcAttack = false;
                return attackData;
            }
            //
            NavigableSet<DamageData<EndCrystalEntity>> min = new TreeSet<>();
            for (EndCrystalEntity c : crystals)
            {
                if (manualConfig.getValue() && manuals.remove(toBase(c)) != null)
                {
                    DamageData<EndCrystalEntity> data = new DamageData<>(null,
                            c, 0.0f, 0.0f);
                    data.addTag("manual");
                    return data;
                }
                if (c.age < ticksExistedConfig.getValue()
                        && inhibitConfig.getValue() == Inhibit.NONE)
                {
                    continue;
                }
                final double local = getDamage(mc.player, c.getPos());
                // player safety
                boolean unsafe = false;
                if (safetyConfig.getValue() && !mc.player.isCreative())
                {
                    float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                    unsafe = local + 0.5 > health
                            || local > maxLocalDamageConfig.getValue();
                }
                for (Entity e : entities)
                {
                    if (e != null && e != mc.player && e.isAlive()
                            && !Managers.SOCIAL.isFriend(e.getUuid()))
                    {
                        if (e instanceof EndCrystalEntity)
                        {
                            continue;
                        }
                        if (isEnemy(e))
                        {
                            Position tpos = e.getPos();
                            if (e instanceof PlayerEntity player
                                    && latencyPositionConfig.getValue())
                            {
                                FakePlayerEntity t = Managers.LATENCY.getTrackedPlayer(pos,
                                        player, maxLatencyConfig.getValue());
                                if (t != null)
                                {
                                    tpos = t.getPos();
                                }
                            }
                            double dist = c.squaredDistanceTo(tpos.getX(),
                                    tpos.getY(), tpos.getZ());
                            if (dist > 144.0f)
                            {
                                continue;
                            }
                            double tdist = pos.squaredDistanceTo(tpos.getX(),
                                    tpos.getY(), tpos.getZ());
                            if (tdist > targetRangeConfig.getValue() * targetRangeConfig.getValue())
                            {
                                continue;
                            }
                            float ehealth = 0.0f;
                            if (e instanceof LivingEntity)
                            {
                                ehealth = ((LivingEntity) e).getHealth()
                                        + ((LivingEntity) e).getAbsorptionAmount();
                            }
                            final double dmg = getDamage(e, c.getPos());
                            if (safetyBalanceConfig.getValue() && dmg < local)
                            {
                                continue;
                            }
                            final DamageData<EndCrystalEntity> data =
                                    new DamageData<>(e, c, dmg, local);
                            if (checkAntiTotem(e, ehealth, dmg))
                            {
                                data.addTag("antitotem");
                                return data;
                            }
                            if (checkLethal(e, ehealth, dmg))
                            {
                                data.addTag("lethal");
                                if (safetyOverride.getValue())
                                {
                                    unsafe = false;
                                }
                            }
                            final List<ItemStack> armor =
                                    Lists.newArrayList(e.getArmorItems());
                            if (!armor.isEmpty() && checkArmor(e, armor))
                            {
                                data.addTag("armorbreak");
                            }
                            if (data.isDamageValid(minDamageConfig.getValue())
                                    && !unsafe)
                            {
                                min.add(data);
                            }
                        }
                    }
                }
            }
            if (!min.isEmpty())
            {
                DamageData<EndCrystalEntity> f = min.last();
                renderBreak.set(f.getBoundingBox());
                return f;
            }
        }
        renderBreak.set(null);
        return null;
    }

    /**
     *
     *
     * @param pos
     * @param p
     * @return
     */
    private boolean prePlaceRangeCheck(final Vec3d pos,
                                       final BlockPos p)
    {
        double dist = placeRangeCenterConfig.getValue() ?
                p.getSquaredDistanceFromCenter(pos.getX(), pos.getY(),
                        pos.getZ()) : p.getSquaredDistance(pos);
        if (dist > placeRangeConfig.getValue() * placeRangeConfig.getValue()
                && !isWithinStrictRange(pos, toSource(p),
                strictPlaceRangeConfig.getValue()))
        {
            return true;
        }
        final Vec3d expected = new Vec3d(p.getX() + 0.5,
                p.getY() + 2.700000047683716, p.getZ() + 0.5);
        final BlockHitResult result = mc.world.raycast(new RaycastContext(
                Managers.POSITION.getCameraPosVec(1.0f),
                expected, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player));
        float maxDist = breakRangeConfig.getValue() * breakRangeConfig.getValue();
        if (result != null && result.getType() == HitResult.Type.BLOCK
                && result.getBlockPos() != p)
        {
            maxDist = breakWallRangeConfig.getValue() * breakWallRangeConfig.getValue();
            if (dist > placeWallRangeConfig.getValue() * placeWallRangeConfig.getValue())
            {
                return true;
            }
        }
        return breakValidConfig.getValue() && dist > maxDist;
    }

    /**
     *
     *
     * @param entities
     * @param blocks
     * @param pos
     * @return
     *
     * @see #getDamage(Entity, Vec3d)
     * @see #getSphere(Vec3d)
     */
    private DamageData<BlockPos> getPlace(Iterable<Entity> entities,
                                          Iterable<BlockPos> blocks,
                                          Vec3d pos)
    {
        if (mc.world != null && mc.player != null)
        {
            if (pauseCalcPlace && placeData != null)
            {
                pauseCalcPlace = false;
                return placeData;
            }
            // Position data of the player
            if (extrapolateRangeConfig.getValue())
            {
                pos = extrapolatePosition(mc.player, pos,
                        mc.player.getBoundingBox(), selfExtrapolateTicksConfig.getValue());
            }
            //
            final BlockPos mine = Modules.SPEEDMINE.getBlockTarget();
            //
            NavigableSet<DamageData<BlockPos>> min = new TreeSet<>();
            // placement processing
            for (BlockPos p : blocks)
            {
                BlockPos dpos = p;
                if (mine != null && mine.equals(p) && minePlaceConfig.getValue())
                {
                    dpos = p.down();
                }
                final Vec3d src = toSource(dpos);
                final double local = getDamage(mc.player, src);
                // player safety
                boolean unsafe = false;
                if (safetyConfig.getValue() && !mc.player.isCreative())
                {
                    float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                    unsafe = local + 0.5 > health
                            || local > maxLocalDamageConfig.getValue();
                }
                for (Entity e : entities)
                {
                    if (e != null && e != mc.player && e.isAlive()
                            && !Managers.SOCIAL.isFriend(e.getUuid()))
                    {
                        if (e instanceof EndCrystalEntity)
                        {
                            continue;
                        }
                        if (isEnemy(e))
                        {
                            Position tpos = e.getPos();
                            if (e instanceof PlayerEntity player
                                    && latencyPositionConfig.getValue())
                            {
                                FakePlayerEntity t = Managers.LATENCY.getTrackedPlayer(pos,
                                        player, maxLatencyConfig.getValue());
                                if (t != null)
                                {
                                    tpos = t.getPos();
                                }
                            }
                            double dist = src.squaredDistanceTo(tpos.getX(),
                                    tpos.getY(), tpos.getZ());
                            if (dist > 144.0f)
                            {
                                continue;
                            }
                            double tdist = pos.squaredDistanceTo(tpos.getX(),
                                    tpos.getY(), tpos.getZ());
                            // double edist = e.squaredDistanceTo(p.toCenterPos());
                            if (tdist > targetRangeConfig.getValue()
                                    * targetRangeConfig.getValue())
                            {
                                continue;
                            }
                            final double dmg = getDamage(e, src);
                            if (safetyBalanceConfig.getValue() && dmg < local)
                            {
                                continue;
                            }
                            float ehealth = 0.0f;
                            if (e instanceof LivingEntity)
                            {
                                ehealth = ((LivingEntity) e).getHealth()
                                        + ((LivingEntity) e).getAbsorptionAmount();
                            }
                            final DamageData<BlockPos> data =
                                    new DamageData<>(e, p, dmg, local);
                            if (mine != null && mine.equals(p)
                                    && minePlaceConfig.getValue())
                            {
                                data.addTag("minedmg");
                            }
                            if (checkAntiTotem(e, ehealth, dmg))
                            {
                                data.addTag("antitotem");
                                return data;
                            }
                            if (checkLethal(e, ehealth, dmg))
                            {
                                data.addTag("lethal");
                                if (safetyOverride.getValue())
                                {
                                    unsafe = false;
                                }
                            }
                            final List<ItemStack> armor =
                                    Lists.newArrayList(e.getArmorItems());
                            if (!armor.isEmpty() && checkArmor(e, armor))
                            {
                                data.addTag("armorbreak");
                            }
                            if (data.isDamageValid(minDamageConfig.getValue())
                                    && !unsafe)
                            {
                                min.add(data);
                            }
                        }
                    }
                }
            }
            if (!min.isEmpty())
            {
                DamageData<BlockPos> f = min.last();
                renderPlace.set(f.getSrc());
                return f;
            }
        }
        renderPlace.set(null);
        return null; // no valid placements
    }

    /**
     *
     *
     * @param e
     * @param health
     * @param damage
     * @return
     */
    private boolean checkLethal(final Entity e,
                                final float health,
                                final double damage)
    {
        if (e instanceof LivingEntity)
        {
            double lethal = (1.0f + lethalMultiplier.getValue()) * damage;
            return health - lethal > 0.5;
        }
        return false;
    }

    /**
     *
     *
     * @param entity
     * @param armor
     * @return
     */
    private boolean checkArmor(final Entity entity,
                               final List<ItemStack> armor)
    {
        if (entity instanceof LivingEntity && armorBreakerConfig.getValue())
        {
            float dmg = 0.0f;
            float max = 0.0f;
            for (ItemStack a : armor)
            {
                dmg += a.getDamage();
                max += a.getMaxDamage();
            }
            float scale = dmg / max;
            return scale < (100.0f - armorScaleConfig.getValue());
        }
        return false;
    }

    /**
     *
     *
     * @param entity
     * @param phealth
     * @param damage
     * @return
     */
    private boolean checkAntiTotem(final Entity entity,
                                   final float phealth,
                                   final double damage)
    {
        if (antiTotemConfig.getValue() && entity instanceof PlayerEntity p)
        {
            if (phealth <= 2.0f && phealth - damage < 0.5f)
            {
                final Long time = pops.get(p);
                if (time != null)
                {
                    return System.currentTimeMillis() - time <= 500;
                }
            }
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if the dest {@link Vec3d} is within the NCP
     * range of the src {@link Vec3d}.
     *
     * @param src The vector source
     * @param dest The vector destination
     * @param range The maximum range
     * @return <tt>true</tt> if the vector is within NCP ranges
     */
    private boolean isWithinStrictRange(final Vec3d src,
                                        final Vec3d dest,
                                        final double range)
    {
        // directly from NCP src
        double y1 = src.getY() + mc.player.getStandingEyeHeight();
        double y2 = dest.getY();
        if (y1 <= y2); // Keep the foot level y
        else if (y1 >= y2 + 2.0)
        {
            y2 += 2.0;
        }
        else
        {
            y2 = y1;
        }
        double x = dest.getX() - src.getX();
        double y = y2 - y1;
        double z = dest.getZ() - src.getZ();
        return MathHelper.squaredMagnitude(x, y, z) <= range * range;
    }

    /**
     *
     *
     * @param entity
     * @param src
     * @return
     */
    public double getDamage(final Entity entity,
                            final Vec3d src)
    {
        final Vec3d pos = Managers.POSITION.getPos();
        //
        Entity recieve = entity;
        if (recieve instanceof LivingEntity e)
        {
            if (entity instanceof PlayerEntity player && latencyPositionConfig.getValue())
            {
                FakePlayerEntity t = Managers.LATENCY.getTrackedPlayer(pos,
                        player, maxLatencyConfig.getValue());
                if (t != null)
                {
                    recieve = t;
                }
            }
            else if (extrapolateTicksConfig.getValue() > 0)
            {
                final Vec3d tpos = extrapolatePosition(e, entity.getPos(),
                        entity.getBoundingBox(), extrapolateTicksConfig.getValue());
                return EndCrystalUtil.getDamageToPos(tpos, entity, src,
                        blockDestructionConfig.getValue());
            }
        }
        return EndCrystalUtil.getDamageTo(recieve, src,
                blockDestructionConfig.getValue());
    }

    /**
     * Returns <tt>true</tt> if the player rotation vector is facing the param
     * bounding {@link Box} of the target.
     *
     * @param dest The bounding box of the target
     * @return <tt>true</tt> if the player is facing the target
     */
    private boolean checkFacing(final Box dest)
    {
        float yaw = Managers.ROTATION.getYaw();
        float pitch = Managers.ROTATION.getPitch();
        final Vec3d pos = Managers.POSITION.getCameraPosVec(1.0f);
        final Vec3d rot = Vec3d.fromPolar(pitch, yaw);
        final double maxReach = pos.distanceTo(dest.getCenter());
        BlockHitResult result = mc.world.raycast(new RaycastContext(pos,
                pos.add(rot.getX() * maxReach, rot.getY() * maxReach,
                        rot.getZ() * maxReach),
                RaycastContext.ShapeType.VISUAL,
                RaycastContext.FluidHandling.ANY, mc.player));
        return result != null && dest.contains(result.getPos());
    }

    /**
     * Returns the number of ticks the rotation will last after setting the
     * player rotations to the dest rotations. Yaws are only calculated in
     * this method, the yaw will not be updated until the main
     * loop runs.
     *
     * @param dest The rotation
     * @param yawstep Whether to split up the rotation into multiple yaws
     */
    private int setRotation(float[] dest, boolean yawstep)
    {
        int tick;
        if (yawstep)
        {
            float diff = Managers.ROTATION.getWrappedYaw() - dest[0]; // yaw diff
            float magnitude = Math.abs(diff);
            if (magnitude > 180.0f)
            {
                diff += diff > 0.0f ? -360.0f : 360.0f;
            }
            final int dir = diff > 0.0f ? 1 : -1;
            tick = yawTicksConfig.getValue();
            // partition yaw
            float deltaYaw = magnitude / tick;
            if (deltaYaw > rotateLimitConfig.getValue())
            {
                tick = MathHelper.ceil(magnitude / rotateLimitConfig.getValue());
                deltaYaw = magnitude / tick;
            }
            deltaYaw *= dir;
            int yawCount = tick;
            tick += rotateTimeoutConfig.getValue();
            yawLimits = new float[tick];
            int off = tick - 1;
            float yawTotal = 0.0f;
            for (int i = 0; i < tick; ++i)
            {
                if (i > yawCount)
                {
                    yawLimits[off - i] = 0.0f;
                    continue;
                }
                yawTotal += deltaYaw;
                yawLimits[off - i] = yawTotal;
            }
        }
        else
        {
            tick = rotateTimeoutConfig.getValue() + 1;
            yawLimits = new float[tick];
            int off = tick - 1;
            yawLimits[off] = dest[0];
            for (int i = 1; i < tick; ++i)
            {
                yawLimits[off - i] = 0.0f;
            }
        }
        pitch = dest[1];
        return yawLimits.length;
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

    /**
     * Processor for {@link TickCalculation}. Backed by a
     * {@link ThreadPoolExecutor} for concurrent execution of the
     * crystals calculations.
     *
     * <p>
     * Calculations can be run immediately by calling
     * {@link #runCalc(Iterable, Iterable, Iterable)} or scheduled by calling
     * {@link #scheduleCalc(Iterable, Iterable, Iterable, Number)}  (Remember
     * that these actions will also <b>TERMINATE</b> the current calculation
     * if not {@link TickCalculation#isDone()}!).
     * </p>
     *
     * @author linus
     * @since 1.0
     *
     * @see TickCalculation
     * @see ThreadPoolExecutor
     * @see ExecutorCompletionService
     */
    private class ThreadCalcProcessor
    {
        // Current calculation.
        private TickCalculation calc;
        //
        private long calcCount;
        //
        // The thread executor service. All calculations will be submitted to
        // this thread pool. The number of threads is FIXED at half of the
        // number of available processors.
        private final ThreadPoolExecutor pool = (ThreadPoolExecutor)
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        private final ExecutorCompletionService<DamageData<?>> service;

        /**
         *
         */
        public ThreadCalcProcessor()
        {
            service = new ExecutorCompletionService<>(pool);
        }

        /**
         *
         *
         * @param entities
         * @param crystals
         * @param blocks
         */
        public synchronized void runCalc(final Iterable<Entity> entities,
                                         final Iterable<EndCrystalEntity> crystals,
                                         final Iterable<BlockPos> blocks)
        {
            calcCount++;
            calc = new TickCalculation(this);
            calc.runCalc(entities, crystals, blocks);
        }

        /**
         * Schedules a {@link TickCalculation} to run after the param time 
         * delay. The accuracy may change between systems.
         *
         * @param entities The entities in the world 
         * @param crystals The crystals in range
         * @param blocks The crystal blocks in range
         * @param time The calculation delay in ms
         *             
         * @see TickCalculation#runDelayedCalc(Iterable, Iterable, Iterable, Number)
         */
        public synchronized void scheduleCalc(final Iterable<Entity> entities,
                                              final Iterable<EndCrystalEntity> crystals,
                                              final Iterable<BlockPos> blocks,
                                              final Number time)
        {
            calcCount++;
            // 
            long offset = 0;
            if (calc != null && calc.isDone()) 
            {
                offset = calc.getSleepTime();
            }
            calc = new TickCalculation(this);
            long delay = time.longValue() - offset;
            if (delay > 0)
            {
                calc.runDelayedCalc(entities, crystals, blocks, delay);
                return;
            }
            calc.runCalc(entities, crystals, blocks);
        }

        /**
         *
         *
         * @return
         */
        public TickCalculation getCurrentCalc()
        {
            return calc;
        }

        /**
         *
         *
         * @return
         */
        public long getCompletedCalcCount()
        {
            return calcCount;
        }

        /**
         *
         *
         * @param calc
         */
        public void submitCalc(Callable<DamageData<?>> calc)
        {
            service.submit(calc);
        }

        /**
         *
         *
         * @return
         * @throws InterruptedException
         */
        public Future<DamageData<?>> takeCalc() throws InterruptedException
        {
            return service.take();
        }

        /**
         * Waits for running processes to complete. If the processes
         * exceed the timeout, shutdown all running tasks in the
         * {@link ThreadPoolExecutor}.
         *
         * @see ThreadPoolExecutor#shutdown()
         * @see ThreadPoolExecutor#shutdownNow()
         */
        public void shutdown()
        {
            Caspian.info(Modules.AUTO_CRYSTAL, "Shutdown processor! Completed {} tasks",
                    getCompletedCalcCount());
            try
            {
                pool.shutdown();
                boolean timeout = !pool.awaitTermination(50,
                        TimeUnit.MILLISECONDS);
                if (timeout)
                {
                    Caspian.error(Modules.AUTO_CRYSTAL, "Process timed out! " +
                            "Shutting down pool!");
                    // pool.shutdownNow();
                }
            }
            catch (InterruptedException e)
            {
                Caspian.error(Modules.AUTO_CRYSTAL, "Failed to shutdown pool!" +
                        "  Maintained interrupt state!");
                Thread.currentThread().interrupt();
            }
            finally
            {
                pool.shutdownNow();
                calc = null;
            }
        }
    }

    /**
     *
     */
    private class TickCalculation
    {
        // Calculation unique id
        private final UUID id;
        // AutoCrystal calculation dedicated thread service. Takes in the src
        // list of damage sources and returns the best damage source's data.
        private final ThreadCalcProcessor processor;
        // Calculated
        private DamageData<EndCrystalEntity> attackCalc;
        private DamageData<BlockPos> placeCalc;
        // Calculation time information
        private long start, done;

        /**
         *
         *
         * @param processor
         */
        public TickCalculation(ThreadCalcProcessor processor)
        {
            this.id = UUID.randomUUID();
            this.processor = processor;
        }

        /**
         *
         *
         * @param entities
         * @param crystals
         * @param blocks
         *
         * @see #getCrystal(Iterable, Iterable, Vec3d)
         * @see #getPlace(Iterable, Iterable, Vec3d)
         */
        public void runCalc(final Iterable<Entity> entities,
                            final Iterable<EndCrystalEntity> crystals,
                            final Iterable<BlockPos> blocks)
        {
            start = System.currentTimeMillis();
            Vec3d pos = Managers.POSITION.getPos();
            // Position data of the player
            if (extrapolateRangeConfig.getValue())
            {
                pos = extrapolatePosition(mc.player, pos,
                        mc.player.getBoundingBox(), selfExtrapolateTicksConfig.getValue());
            }
            if (multithreadConfig.getValue())
            {
                final Vec3d finalPos = pos;
                processor.submitCalc(() -> getCrystal(entities, crystals, finalPos));
                if (placeConfig.getValue())
                {
                    processor.submitCalc(() -> getPlace(entities, blocks, finalPos));
                }
                return;
            }
            attackCalc = getCrystal(entities, crystals, pos);
            if (placeConfig.getValue())
            {
                placeCalc = getPlace(entities, blocks, pos);
            }
            //
            done = System.currentTimeMillis();
        }

        /**
         *
         *
         * @param entities
         * @param crystals
         * @param blocks
         * @param delay
         *
         * @see #getCrystal(Iterable, Iterable, Vec3d)
         * @see #getPlace(Iterable, Iterable, Vec3d)
         */
        public void runDelayedCalc(final Iterable<Entity> entities,
                                   final Iterable<EndCrystalEntity> crystals,
                                   final Iterable<BlockPos> blocks,
                                   final Number delay)
        {
            start = System.currentTimeMillis();
            Vec3d pos = Managers.POSITION.getPos();
            // Position data of the player
            if (extrapolateRangeConfig.getValue())
            {
                pos = extrapolatePosition(mc.player, pos,
                        mc.player.getBoundingBox(),
                        selfExtrapolateTicksConfig.getValue());
            }
            if (multithreadConfig.getValue())
            {
                final Vec3d finalPos = pos;
                processor.submitCalc(() ->
                {
                    try
                    {
                        Thread.sleep(delay.longValue());
                    }
                    catch (InterruptedException e)
                    {
                        Caspian.error(Modules.AUTO_CRYSTAL, "Thread " +
                                "interrupted for calc {}", id);
                        Thread.currentThread().interrupt();
                    }
                    return getCrystal(entities, crystals, finalPos);
                });
                if (placeConfig.getValue())
                {
                    processor.submitCalc(() -> getPlace(entities,
                            blocks, finalPos));
                }
                return;
            }
            // TODO: calc delays for main threading
            attackCalc = getCrystal(entities, crystals, pos);
            if (placeConfig.getValue())
            {
                placeCalc = getPlace(entities, blocks, pos);
            }
            done = System.currentTimeMillis();
        }

        /**
         *
         *
         * @return
         * @throws InterruptedException
         * @throws ExecutionException
         *
         * @see ExecutorCompletionService
         */
        @SuppressWarnings("unchecked")
        public void checkDone() throws InterruptedException,
                ExecutionException
        {
            if (!isDone())
            {
                boolean foundAttack = false;
                boolean foundPlace = false;
                for (int i = 0; i < 2; ++i)
                {
                    Future<DamageData<?>> result = processor.takeCalc();
                    if (result != null)
                    {
                        final DamageData<?> data = result.get();
                        if (data == null)
                        {
                            Caspian.error(Modules.AUTO_CRYSTAL, "Failed to " +
                                    "get data for calc {}!", getId());
                            return;
                        }
                        if (data.isAttackDamage())
                        {
                            foundAttack = true;
                            attackCalc = (DamageData<EndCrystalEntity>) data;
                        }
                        else if (data.isPlaceDamage())
                        {
                            foundPlace = true;
                            placeCalc = (DamageData<BlockPos>) data;
                        }
                    }
                }
                if (foundAttack && foundPlace)
                {
                    done = System.currentTimeMillis();
                }
            }
        }

        /**
         *
         *
         * @return
         */
        public UUID getId()
        {
            return id;
        }

        /**
         *
         *
         * @return
         */
        public long getSleepTime()
        {
            if (isDone())
            {
                return System.currentTimeMillis() - done;
            }
            return 0;
        }

        /**
         *
         *
         * @return
         */
        public long getCalcTime()
        {
            long calcTime = isDone() ? done : System.currentTimeMillis();
            return calcTime - start;
        }

        /**
         * Returns <tt>true</tt> if the calculation has completed its
         * calculation and has found {@link DamageData}.
         *
         * @return <tt>true</tt> if the calculation has completed
         */
        public boolean isDone()
        {
            return done != 0;
        }

        /**
         *
         *
         * @return
         */
        public DamageData<EndCrystalEntity> getCalcAttack()
        {
            return attackCalc;
        }

        /**
         *
         *
         * @return
         */
        public DamageData<BlockPos> getCalcPlace()
        {
            return placeCalc;
        }
    }

    /**
     *
     *
     * @param <T> The damage source type
     */
    private class DamageData<T> implements Position, Comparable<DamageData<?>>
    {
        //
        private final Entity target;
        private final T src;
        private final Box boundingBox;
        private final Vec3d pos;
        private final double damage, local;
        //
        private final Set<String> tags;

        /**
         *
         *
         * @param target
         * @param src
         * @param damage
         * @param local
         */
        public DamageData(final Entity target, 
                          final T src, 
                          final double damage,
                          final double local)
        {
            this.target = target;
            this.src = src;
            this.damage = damage;
            this.local = local;
            this.tags = new HashSet<>(10);
            //
            Vec3d pos = null;
            Box box = null;
            if (src instanceof BlockPos cpos)
            {
                tags.add("placedmg");
                final Vec3d center = cpos.toCenterPos();
                pos = placeRangeCenterConfig.getValue() ? center :
                        center.subtract(0.5, 0.5, 0.5);
                box = new Box(cpos);
            }
            else if (src instanceof EndCrystalEntity crystal)
            {
                tags.add("attackdmg");
                pos = crystal.getPos();
                box = crystal.getBoundingBox();
            }
            this.pos = pos;
            this.boundingBox = box;
        }

        /**
         * Returns <tt>true</tt> if the {@link DamageData} is valid.
         * There are few reasons why data can be invalid, for example:
         * <p><ul>
         * <li> If the target no longer exists
         * <li> If the damage source no longer exists
         * </ul></p>
         *
         * @return Returns <tt>true</tt> if the {@link DamageData} is valid.
         */
        public boolean isValid()
        {
            final Entity damaged = getTarget();
            if (damaged != null && damaged.isAlive())
            {
                // Player position data
                Vec3d pos = Managers.POSITION.getPos();
                Vec3d eyepos = Managers.POSITION.getEyePos();
                if (extrapolateRangeConfig.getValue())
                {
                    pos = extrapolatePosition(mc.player, pos,
                            mc.player.getBoundingBox(), selfExtrapolateTicksConfig.getValue());
                    eyepos = VecUtil.toEyePos(mc.player, pos);
                }
                if (src instanceof BlockPos cpos)
                {
                    return !postRangeConfig.getValue()
                            || !prePlaceRangeCheck(placeRangeEyeConfig.getValue() ? eyepos : pos, cpos);
                }
                else if (src instanceof EndCrystalEntity crystal)
                {
                    if (postRangeConfig.getValue()
                            && preBreakRangeCheck(eyepos, crystal.getPos()))
                    {
                        return false;
                    }
                    return crystal.isAlive();
                }
            }
            return false;
        }

        /**
         * Returns a valid interaction {@link Direction} based on the NCP
         * direction checks. The default values is <tt>Direction.UP</tt>.
         *
         * @return A valid interact direction
         *
         * @see DirectionChecks#getInteractableDirections(int, int, int, int, int, int, boolean)
         */
        public Direction getInteractDirection()
        {
            if (isPlaceDamage())
            {
                final Vec3d eye = Managers.POSITION.getEyePos();
                final BlockPos p = (BlockPos) getSrc();
                //
                Direction dir = Direction.UP;
                int dx = p.getX();
                int dy = p.getY();
                int dz = p.getZ();
                if (strictDirectionConfig.getValue())
                {
                    final BlockPos blockPos = Managers.POSITION.getBlockPos();
                    int x = blockPos.getX();
                    int y = (int) Math.floor(Managers.POSITION.getY()
                            + mc.player.getStandingEyeHeight());
                    int z = blockPos.getZ();
                    if (x != dx && y != dy && z != dz)
                    {
                        Set<Direction> dirs = DirectionChecks.getInteractableDirections(
                                x, y, z, dx, dy, dz, exposedDirectionConfig.getValue());
                        if (!dirs.isEmpty())
                        {
                            dir = dirs.stream()
                                    .min(Comparator.comparing(d -> eye.distanceTo(getCenterHitVec(p, d))))
                                    .orElse(Direction.UP);
                        }
                    }
                }
                else
                {
                    BlockHitResult result = mc.world.raycast(new RaycastContext(
                            mc.player.getEyePos(), new Vec3d(dx + 0.5,
                            dy + 0.5, dz + 0.5),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE, mc.player));
                    if (result != null && result.getType() == HitResult.Type.BLOCK)
                    {
                        dir = result.getSide();
                        if (mc.world.isInBuildLimit(p))
                        {
                            dir = Direction.DOWN;
                        }
                    }
                }
                return dir;
            }
            return Direction.UP;
        }

        /**
         *
         *
         * @param floor
         * @param hit
         * @return
         */
        public Vec3d getHitVec(final BlockPos floor,
                               final Direction hit)
        {
            return new Vec3d(floor.getX() + hit.getOffsetX(),
                    floor.getY() + hit.getOffsetY(),
                    floor.getZ() + hit.getOffsetZ());
        }

        /**
         *
         *
         * @param floor
         * @param hit
         * @return
         */
        public Vec3d getCenterHitVec(final BlockPos floor,
                                     final Direction hit)
        {
            return new Vec3d(hit.getOffsetX() * 0.5 + 0.5 + floor.getX(),
                    hit.getOffsetY() * 0.5 + 0.5 + floor.getY(),
                    hit.getOffsetZ() * 0.5 + 0.5 + floor.getZ());
        }

        /**
         * Returns a random {@link Vec3d} within the param bounding {@link Box}
         * based on a {@link SecureRandom} sampling of the vector space.
         *
         * @param floor The floored position to offset
         * @param bb The bounding box
         *
         * @see SecureRandom
         */
        private Vec3d getRandomVec(final BlockPos floor,
                                   final Box bb)
        {
            double x = random.nextDouble(bb.maxX);
            double y = random.nextDouble(bb.maxY);
            double z = random.nextDouble(bb.maxZ);
            return new Vec3d(floor.getX() + x, floor.getY() + y,
                    floor.getZ() + z);
        }

        /**
         * Compares this data with the specified data for order. Returns
         * a negative integer, zero, or a positive integer as this object is
         * less than, equal to, or greater than the specified object.
         *
         * @param other the data to be compared.
         * @return
         */
        @Override
        public int compareTo(final DamageData<?> other)
        {
            if (other.isAntiTotem())
            {
                return isAntiTotem() ? 0 : -1;
            }
            if (isAntiTotem())
            {
                return 1;
            }
            if (other.isLethal())
            {
                return isLethal() ? 0 : -1;
            }
            if (isLethal())
            {
                return 1;
            }
            // Diffs
            double d = getDamage() - other.getDamage();
            if (other.isMineDamage())
            {
                return isMineDamage() ? Double.compare(getDamage(),
                        other.getDamage()) : 1;
            }
            if (isMineDamage())
            {
                return -1;
            }
            if (Math.abs(d) > 0.2)
            {
                double diff = getRotationTickDiff(other);
                if (diff != 0.0)
                {
                    return diff >= Math.abs(d) ? 1 : -1;
                }
                return Double.compare(getDamage(), other.getDamage());
            }
            double l = getLocal() - other.getLocal();
            if (Math.abs(l) > 0.2)
            {
                return Double.compare(getLocal(), other.getLocal());
            }
            return Double.compare(squaredDistanceTo(getTargetEyePos()),
                    other.squaredDistanceTo(other.getTargetEyePos()));
        }

        /**
         *
         *
         * @param damage
         * @return
         */
        public boolean isDamageValid(final double damage)
        {
            return this.damage > damage
                    || isAntiTotem()
                    || isLethal()
                    || isArmorBreaker();
        }

        /**
         *
         *
         * @param other
         * @return
         */
        private double getRotationTickDiff(final DamageData<?> other)
        {
            if (rotateTickFactorConfig.getValue())
            {
                final Vec3d eyepos = Managers.POSITION.getEyePos();
                float[] rots1 = getRotations(eyepos);
                float[] rots2 = other.getRotations(eyepos);
                float yaw = Managers.ROTATION.getWrappedYaw();
                if (rots1 != null && rots2 != null)
                {
                    float ticks1 = Math.abs(yaw - rots1[0]) / rotateLimitConfig.getValue();
                    float ticks2 = Math.abs(yaw - rots2[0]) / rotateLimitConfig.getValue();
                    double diff = Math.ceil(ticks1) - Math.ceil(ticks2);
                    double d = getDamage() - other.getDamage();
                    if (d < 0.0 && diff < 0.0f || d > 0.0 && diff > 0.0f)
                    {
                        return Math.abs(diff) * rotateDamageConfig.getValue();
                    }
                }
            }
            return 0.0;
        }

        /**
         * Returns <tt>true</tt> if two {@link DamageData} damage sources are
         * the same
         *
         * @param other The comparing the data
         * @return Returns <tt>true</tt> if the two data are the same
         */
        public boolean isSrcEqual(DamageData<?> other)
        {
            if (src instanceof EndCrystalEntity src1
                    && other.getSrc() instanceof EndCrystalEntity src2)
            {
                return src1.getId() == src2.getId();
            }
            else if (src instanceof BlockPos src1
                    && other.getSrc() instanceof BlockPos src2)
            {
                return src1.getX() == src2.getX()
                        && src1.getY() == src2.getY()
                        && src1.getZ() == src2.getZ();
            }
            return Objects.equals(getSrc(), other.getSrc());
        }

        /**
         * Returns the rotation yaw and pitch from the {@link Vec3d} src
         * position to the {@link #getRotPos()} position of the damage source
         *
         * @param src The src position
         * @return The rotations to the damage source
         *
         * @see #getRotPos()
         */
        public float[] getRotations(final Vec3d src)
        {
            final Vec3d rpos = getRotPos();
            if (rpos != null)
            {
                return RotationUtil.getRotationsTo(src, rpos);
            }
            return null;
        }

        /**
         * Returns the rotation yaw and pitch from the {@link Vec3d} src
         * position to the {@link #getRotPos()} interact offset position of the
         * damage source
         *
         * @param src The src position
         * @param interact The interact direction
         * @return The rotations to the damage source
         *
         * @see #getRotPos()
         */
        public float[] getRotations(final Vec3d src,
                                    final Direction interact)
        {
            final Vec3d rpos = getRotPos(interact);
            if (rpos != null)
            {
                return RotationUtil.getRotationsTo(src, rpos);
            }
            return null;
        }

        /**
         * Returns the difference in yaw between this data' damage source
         * {@link #getRotations(Vec3d)} and the specified data's damage
         * source rotations. Note that this value can be <b>negative</b>.
         *
         * @param other The data to be compared
         * @return The difference in yaw between the two damage sources
         *
         * @see #getRotations(Vec3d)
         */
        public float getYawDiff(final DamageData<?> other)
        {
            Vec3d eye = Managers.POSITION.getEyePos();
            float[] rots1 = RotationUtil.getRotationsTo(eye, pos);
            float[] rots2 = RotationUtil.getRotationsTo(eye, other.getPos());
            return rots1[0] - rots2[0];
        }

        /**
         *
         *
         * @return
         */
        public int getId()
        {
            if (src instanceof EndCrystalEntity crystal)
            {
                return crystal.getId();
            }
            return FakePlayerEntity.CURRENT_ID.incrementAndGet();
        }

        /**
         * Returns the position {@link Vec3d} of the rotation to the data.
         *
         * @return The rotation vector
         */
        public Vec3d getRotPos()
        {
            if (src instanceof EndCrystalEntity crystal)
            {
                return randomVectorConfig.getValue() ?
                        getRandomVec(crystal.getBlockPos(), boundingBox) :
                        pos;
            }
            else if (src instanceof BlockPos cpos)
            {
                final Vec3d border = new Vec3d(cpos.getX() + 0.5,
                        cpos.getY() + 1.0, cpos.getZ() + 0.5);
                final Vec3d rot = vectorBorderConfig.getValue() ? border :
                        cpos.toCenterPos();
                return randomVectorConfig.getValue() ? getRandomVec(cpos,
                        boundingBox) : rot;
            }
            return null;
        }

        /**
         * Returns the position {@link Vec3d} of the rotation to the data
         * accounting for the interact offset.
         *
         * @param dir
         *
         * @return The rotation vector
         */
        public Vec3d getRotPos(final Direction dir)
        {
            if (src instanceof EndCrystalEntity crystal)
            {
                return randomVectorConfig.getValue() ?
                        getRandomVec(crystal.getBlockPos(), boundingBox) : pos;
            }
            else if (src instanceof BlockPos cpos)
            {
                if (randomVectorConfig.getValue())
                {
                    final Vec3d hitVec = getHitVec(cpos, dir);
                    Box interactBox = new Box(hitVec.getX(), hitVec.getY(),
                            hitVec.getZ(), boundingBox.maxX, boundingBox.maxY,
                            boundingBox.maxZ);
                    return getRandomVec(cpos, interactBox);
                }
                return getCenterHitVec(cpos, dir);
            }
            return null;
        }

        /**
         *
         *
         * @return
         */
        public Vec3d getPos()
        {
            return pos;
        }

        /**
         * Returns the bounding {@link Box} of the damage source. If
         * placement damage, the box is equal to the {@link BlockPos} of the
         * placement.
         *
         * @return The bounding box of the source
         */
        public Box getBoundingBox()
        {
            return boundingBox;
        }

        /**
         *
         *
         * @return
         */
        @Override
        public double getX()
        {
            return pos.getX();
        }

        /**
         *
         *
         * @return
         */
        @Override
        public double getY()
        {
            return pos.getY();
        }

        /**
         *
         *
         * @return
         */
        @Override
        public double getZ()
        {
            return pos.getZ();
        }

        /**
         * Returns the squared distance between this vector and the given vector.
         * Can be used for fast comparison between distances.
         *
         * @param to
         * @return The squared distance between the two vectors
         */
        public double squaredDistanceTo(Vec3d to)
        {
            return pos.squaredDistanceTo(to);
        }

        /**
         *
         *
         * @return
         */
        public Entity getTarget()
        {
            return target;
        }

        /**
         *
         *
         * @return
         */
        public Vec3d getTargetEyePos()
        {
            return target.getEyePos();
        }

        /**
         *
         *
         * @return
         */
        public T getSrc()
        {
            return src;
        }

        /**
         *
         *
         * @return
         */
        public double getDamage()
        {
            return damage;
        }

        /**
         *
         *
         * @return
         */
        public double getLocal()
        {
            return local;
        }

        /**
         *
         *
         * @return
         */
        public boolean isAntiTotem()
        {
            return tags.contains("antitotem");
        }

        /**
         *
         *
         * @return
         */
        public boolean isLethal()
        {
            return tags.contains("lethal");
        }

        /**
         *
         *
         * @return
         */
        public boolean isArmorBreaker()
        {
            return tags.contains("armorbreak");
        }

        /**
         * Returns <tt>true</tt> if the damage source is an
         * {@link EndCrystalEntity}
         *
         * @return <tt>true</tt> if the damage source is an end crystal
         */
        public boolean isAttackDamage()
        {
            return src instanceof EndCrystalEntity;
        }

        /**
         * Returns <tt>true</tt> if the damage source is a {@link BlockPos}
         * base of an {@link EndCrystalEntity}
         *
         * @return <tt>true</tt> if the damage source is a block position
         */
        public boolean isPlaceDamage()
        {
            return src instanceof BlockPos;
        }

        /**
         *
         *
         * @return
         */
        public boolean isMineDamage()
        {
            return tags.contains("minedmg");
        }

        /**
         * 
         * 
         * @param tag
         */
        public void addTag(String tag)
        {
            tags.add(tag);
        }
    }
}