package com.caspian.client.impl.module.combat;

import com.caspian.client.Caspian;
import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.setting.NumberDisplay;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.asm.accessor.AccessorPlayerInteractEntityC2SPacket;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.player.RotationUtil;
import com.caspian.client.util.time.Timer;
import com.caspian.client.util.world.EntityUtil;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 *
 */
public class AutoCrystalModule extends ToggleModule
{
    // GENERAL SETTINGS
    Config<Boolean> multitaskConfig = new BooleanConfig("Multitask",
            "Allows attacking while using items", false);
    Config<Float> targetRangeConfig = new NumberConfig<>("EnemyRange",
            "Range to search for potential enemies", 1.0f, 10.0f, 13.0f);
    Config<Boolean> awaitConfig = new BooleanConfig("Instant",
            "Instantly attacks crystals when they spawn", false);
    // Config<Boolean> raytraceConfig = new BooleanConfig("Raytrace", "",
    //        false);
    Config<Sequential> sequentialConfig = new EnumConfig<>("Sequential",
            "", Sequential.NORMAL, Sequential.values());
    // ROTATE SETTINGS
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotate" +
            "before placing and breaking", false);
    Config<Boolean> ignoreExpectedTickConfig = new BooleanConfig(
            "RotateHold", "", false);
    Config<YawStep> yawStepConfig = new EnumConfig<>("YawStep", "",
            YawStep.OFF, YawStep.values());
    Config<Integer> yawStepThresholdConfig = new NumberConfig<>(
            "YawStepThreshold", "", 1, 180, 180, NumberDisplay.DEGREES);
    Config<Integer> yawStepTicksConfig = new NumberConfig<>("YawStepTicks",
            "", 0, 0, 5);
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
            "Speed to break crystals", 0.1f, 20.0f, 20.0f);
    Config<Float> randomSpeedConfig = new NumberConfig<>("RandomSpeed",
            "Randomized delay for breaking crystals", 0.0f, 0.0f, 10.0f);
    Config<Integer> ticksExistedConfig = new NumberConfig<>("TicksExisted",
            "Minimum ticks alive to consider crystals for attack", 0, 0, 10);
    Config<Float> breakRangeConfig = new NumberConfig<>("BreakRange",
            "Range to break crystals", 0.1f, 4.0f, 5.0f);
    Config<Float> breakWallRangeConfig = new NumberConfig<>(
            "BreakWallRange", "Range to break crystals through walls", 0.1f,
            4.0f, 5.0f);
    Config<Swap> antiWeaknessConfig = new EnumConfig<>("AntiWeakness",
            "Swap to tools before attacking crystals", Swap.OFF,
            Swap.values());
    Config<Float> swapDelayConfig = new NumberConfig<>("SwapDelay", "", 0.0f,
            0.0f, 10.0f);
    //
    Config<Boolean> inhibitConfig = new BooleanConfig("Inhibit",
            "Prevents unnecessary attacks", true);
    // default NCP config
    // limitforseconds:
    //        half: 9
    //        one: 14
    //        two: 39
    //        four: 55
    //        eight: 100
    Config<Integer> attackFrequencyConfig = new NumberConfig<>(
            "AttackFrequency", "Limit of attack packets sent for each time " +
            "interval", 1, 14, 20);
    // Config<Boolean> manualConfig = new BooleanConfig("Manual",
    //        "Always breaks manually placed crystals", false);
    // PLACE SETTINGS
    Config<Boolean> placeConfig = new BooleanConfig("Place", "Places crystals" +
            " to damage enemies. Place settings will only function if this " +
            "setting is enabled.", true);
    Config<Float> placeSpeedConfig = new NumberConfig<>("PlaceSpeed",
            "Speed to place crystals", 0.1f, 20.0f, 20.0f);
    Config<Float> placeRangeConfig = new NumberConfig<>("PlaceRange",
            "Range to place crystals", 0.1f, 4.0f, 5.0f);
    Config<Float> placeWallRangeConfig = new NumberConfig<>(
            "PlaceWallRange", "Range to place crystals through walls", 0.1f,
            4.0f, 5.0f);
    Config<Boolean> placeRangeEyeConfig = new BooleanConfig(
            "PlaceRangeEye", "", false);
    Config<Boolean> placeRangeCenterConfig = new BooleanConfig(
            "PlaceRangeCenter", "", true);
    Config<Boolean> placeLowConfig = new BooleanConfig("PlaceLow", "Allow " +
            "placements at a lower bounding", false);
    Config<Swap> swapConfig = new EnumConfig<>("Swap", "", Swap.OFF,
            Swap.values());
    Config<Boolean> breakValidateConfig = new BooleanConfig(
            "BreakValidate", "Only places crystals that can be attacked",
            false);
    Config<Boolean> strictDirectionConfig = new BooleanConfig(
            "StrictDirection", "", false);
    Config<Placements> placementsConfig = new EnumConfig<>("Placements",
            "", Placements.NATIVE, Placements.values());
    // DAMAGE SETTINGS
    Config<Float> minDamageConfig = new NumberConfig<>("MinDamage",
            "", 1.0f, 4.0f, 10.0f);
    Config<Float> armorScaleConfig = new NumberConfig<>("ArmorScale",
            "", 0.0f, 5.0f, 20.0f, NumberDisplay.PERCENT);
    Config<Float> lethalMultiplier = new NumberConfig<>(
            "LethalMultiplier", "", 0.0f, 0.5f, 4.0f);
    Config<Boolean> safetyConfig = new BooleanConfig("Safety",  "",
            true);
    Config<Float> safetyBalanceConfig = new NumberConfig<>(
            "SafetyBalance", "", 1.0f, 3.0f, 5.0f);
    Config<Float> maxLocalDamageConfig = new NumberConfig<>(
            "MaxLocalDamage", "", 4.0f, 12.0f, 20.0f);
    Config<Boolean> blockDestructionConfig = new BooleanConfig(
            "BlockDestruction", "Accounts for explosion block destruction " +
            "when calculating damages", false);
    // RENDER SETTINGS
    Config<Boolean> renderConfig = new BooleanConfig("Render",
            "Renders the current placement", true);
    Config<Boolean> renderAttackConfig = new BooleanConfig(
            "RenderAttack", "Renders the current attack", false);
    Config<Boolean> renderSpawnConfig = new BooleanConfig("RenderSpawn",
            "Indicates if the current placement was spawned", false);
    //
    private final ThreadPoolExecutor pool = (ThreadPoolExecutor)
            Executors.newCachedThreadPool();
    //
    private DamageData<BlockPos> place;
    private DamageData<EndCrystalEntity> attack;
    // Calculated placements and attacks will be added to their respective
    // stacks. When the main loop requires a placement/attack, simply pop the
    // last calculated from the stack.
    private final CalcFactory factory = new CalcFactory();
    private TickCalculation calc;
    // Set of attempted placements and attacks
    private final Set<BlockPos> placements =
            Collections.synchronizedSet(new ConcurrentSet<>());
    private final Set<Integer> attacks =
            Collections.synchronizedSet(new ConcurrentSet<>());
    // RANDOM
    private final Timer randomTime = new Timer();
    private final Random random = new SecureRandom();
    private long currRandom;
    //
    private final Timer freqInterval = new Timer();
    private int attackFreq;
    //
    private boolean attacking, placing;
    private final Timer lastPlace = new Timer();
    private final Timer lastBreak = new Timer();
    private final Timer lastSwap = new Timer();
    // private final Timer lastClean = new Timer();
    // ROTATIONS
    //
    private Vec3d facing;
    //
    private float[] yaws;
    private float pitch;
    //
    private float end;
    private int rotating;

    /**
     *
     */
    public AutoCrystalModule()
    {
        super("AutoCrystal", "Attacks entities with end crystals",
                ModuleCategory.COMBAT);
        pool.setCorePoolSize(Runtime.getRuntime().availableProcessors() / 2);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        attack = null;
        place = null;
        facing = null;
        attacking = false;
        placing = false;
        attackFreq = 0;
        lastBreak.reset();
        lastPlace.reset();
        freqInterval.reset();
        attacks.clear();
        placements.clear();
        calc = factory.build(getCrystalSphere(mc.player.getEyePos(),
                        breakRangeConfig.getValue() + 0.5),
                getSphere(placeRangeEyeConfig.getValue() ?
                                mc.player.getEyePos() : mc.player.getPos(),
                        placeRangeConfig.getValue() + 0.5));
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        calc = null;
        try
        {
            pool.shutdown();
            boolean timeout = !pool.awaitTermination(100,
                TimeUnit.MILLISECONDS);
            if (timeout)
            {
                Caspian.error("Process timed out! Shutting down pool!");
                // pool.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            Caspian.error("Failed to shutdown pool! Maintained interrupt " +
                    "state!");
            Thread.currentThread().interrupt();
        }
        finally
        {
            pool.shutdownNow();
        }
    }

    /*
    public void clean()
    {
        attacks.removeIf(e ->
        {
            Entity c = mc.world.getEntityById(e);
            if (c != null)
            {
                return !c.isAlive();
            }
            return false;
        });
        placements.clear();
    }
     */

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
                if (freqInterval.passed(1, TimeUnit.SECONDS))
                {
                    attackFreq = 0;
                }
                // MAIN LOOP
                try
                {
                    calc.retrieve();
                }
                catch (InterruptedException | ExecutionException e)
                {
                    Caspian.error("Failed calculation %x!", calc.getId()
                            .getMostSignificantBits());
                    e.printStackTrace();
                }
                if (calc.isDone())
                {
                    Caspian.info("Calc done in %dms!", calc.getCalcTime());
                    DamageData<EndCrystalEntity> attackData =
                            calc.getCalcAttack();
                    attacking = attackData != null && evaluate(attackData);
                    if (attacking)
                    {
                        attack = attackData;
                    }
                    DamageData<BlockPos> placeData =
                            calc.getCalcPlace();
                    placing = placeData != null && evaluate(placeData);
                    if (placing)
                    {
                        place = placeData;
                    }
                    if (attack != null)
                    {
                        Vec3d dest = attack.src().getEyePos();
                        if (rotateConfig.getValue())
                        {
                            if (facing != null && isNearlyEqual(facing, dest,
                                    0.05f))
                            {
                                rotating--;
                            }
                            else
                            {
                                rotating = setRotation(dest);
                            }
                        }
                        float delay = (((NumberConfig<Float>) breakSpeedConfig).getMax()
                                - breakSpeedConfig.getValue()) * 50;
                        if (lastBreak.passed(delay))
                        {
                            if (attack(attack.src()))
                            {
                                lastBreak.reset();
                                attack = null;
                                attackFreq++;
                            }
                        }
                    }
                    if (place != null)
                    {
                        float delay = (((NumberConfig<Float>) placeSpeedConfig).getMax()
                                - placeSpeedConfig.getValue()) * 50;
                        if (lastPlace.passed(delay))
                        {
                            facing = place.src().toCenterPos();
                            if (place(place.src()))
                            {
                                lastPlace.reset();
                                place = null;
                            }
                        }
                    }
                    calc = factory.build(getCrystalSphere(mc.player.getEyePos(),
                                    breakRangeConfig.getValue() + 0.5),
                            getSphere(placeRangeEyeConfig.getValue() ?
                                            mc.player.getEyePos() : mc.player.getPos(),
                                    placeRangeConfig.getValue() + 0.5));
                }
            }
        }
    }

    /**
     *
     *
     *
     */
    @EventListener
    public void onRenderWorld()
    {
        if (renderConfig.getValue())
        {
            int color = Modules.COLORS.getRGB();
            if (renderAttackConfig.getValue())
            {
                if (attack != null)
                {
                    RenderManager.renderBoundingBox(attack.src().getBoundingBox(),
                            1.5f, color);
                }
            }
            if (place != null)
            {
                RenderManager.renderBox(place.src(), color);
                RenderManager.renderBoundingBox(place.src(), 1.5f, color);
            }
        }
    }

    /**
     * Returns <tt>true</tt> if the {@link DamageData} is valid.
     * There are few reasons why data can be invalid, for example:
     * <p><ul>
     * <li> If the target no longer exists
     * <li> If the damage source no longer exists
     * </ul></p>
     *
     * @param d The data
     * @return Returns <tt>true</tt> if the {@link DamageData} is valid.
     */
    private boolean evaluate(DamageData<?> d)
    {
        Entity damaged = d.damaged();
        if (damaged != null && damaged.isAlive())
        {
            // if (d.src() instanceof BlockPos src)
            // {

            // }
            if (d.src() instanceof EndCrystalEntity src)
            {
                return src.isAlive();
            }
        }
        return false;
    }

    private void setAttackHard(DamageData<EndCrystalEntity> attack)
    {

    }

    private void setPlaceHard(DamageData<BlockPos> place)
    {

    }
    
    /**
     *
     *
     * @return
     */
    public boolean isAttacking()
    {
        return attacking;
    }
    
    /**
     *
     *
     * @return
     */
    public boolean isPlacing()
    {
        return placing;
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
            if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet)
            {
                if (((AccessorPlayerInteractEntityC2SPacket) packet).hookGetTypeHandler().getType()
                        == PlayerInteractEntityC2SPacket.InteractType.ATTACK)
                {
                    MinecraftServer server = mc.player.getServer();
                    if (server != null)
                    {
                        RegistryKey<World> world = mc.world.getRegistryKey();
                        Entity e = packet.getEntity(server.getWorld(world));
                        if (e != null && e.isAlive() && e instanceof EndCrystalEntity)
                        {

                        }
                    }
                }
            }
            else if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet)
            {
                if (getCrystalHand() != null)
                {

                }
            }
            else if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket)
            {
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
            if (event.getPacket() instanceof EntitySpawnS2CPacket packet)
            {
                // crystal spawned
                if (packet.getEntityType() == EntityType.END_CRYSTAL)
                {
                    BlockPos base = BlockPos.ofFloored(packet.getX() - 0.5,
                            packet.getY() - 1.0, packet.getZ() - 0.5);
                    if (awaitConfig.getValue())
                    {
                        if (placements.remove(base))
                        {
                            facing = base.toCenterPos()
                                    .add(0.0, 0.5, 0.0);
                            if (attack(packet.getId()))
                            {
                                lastBreak.reset();
                                attackFreq++;
                            }
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof ExplosionS2CPacket packet)
            {
                for (Entity e : mc.world.getEntities())
                {
                    if (e != null && e.isAlive() && e instanceof EndCrystalEntity)
                    {
                        if (e.squaredDistanceTo(packet.getX(), packet.getY(),
                                packet.getZ()) < packet.getRadius() * packet.getRadius())
                        {
                            // only set dead our crystals
                            if (attacks.remove(e.getId())) 
                            {
                                e.kill();
                            }
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof PlaySoundS2CPacket packet)
            {
                if (packet.getSound().value() == SoundEvents.ENTITY_GENERIC_EXPLODE)
                {
                    for (Entity e : mc.world.getEntities())
                    {
                        if (e != null && e.isAlive() && e instanceof EndCrystalEntity)
                        {
                            if (e.squaredDistanceTo(packet.getX(),
                                    packet.getY(), packet.getZ()) < 144.0)
                            {
                                if (attacks.remove(e.getId()))
                                {
                                    e.kill();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param e
     * @return
     */
    public boolean attack(EndCrystalEntity e)
    {
        return attack(e.getId());
    }

    /**
     *
     *
     * @param e
     * @return
     */
    public boolean attack(int e)
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
                    swap(slot);
                    attackDirect(e);
                    if (swapConfig.getValue() == Swap.SILENT)
                    {
                        swap(prev);
                    }
                }
            }
            else
            {
                attackDirect(e);
            }
            attacks.add(e);
            return true;
        }
        return false;
    }

    /**
     * 
     * 
     * @param e
     */
    private void attackDirect(int e) 
    {
        // retarded hack to set entity id
        PlayerInteractEntityC2SPacket packet =
                PlayerInteractEntityC2SPacket.attack(null,
                        mc.player.isSneaking());
        ((AccessorPlayerInteractEntityC2SPacket) packet).hookSetEntityId(e);
        Managers.NETWORK.sendPacket(packet);
        Managers.NETWORK.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    /**
     *
     *
     * @param e
     * @return
     */
    public boolean preAttackCheck(int e)
    {
        if (inhibitConfig.getValue())
        {
            return !attacks.contains(e);
        }
        if (multitaskConfig.getValue())
        {
            return !mc.player.isUsingItem() || getCrystalHand() == Hand.OFF_HAND;
        }
        long swapDelay = (long) (swapDelayConfig.getValue() * 25);
        if (lastSwap.passed(swapDelay))
        {
            if (currRandom <= 0)
            {
                currRandom = random.nextLong((long)
                        (randomSpeedConfig.getValue() * 10.0f + 1.0f));
            }
            if (randomSpeedConfig.getValue() > ((NumberConfig<Float>) randomSpeedConfig).getMin()
                    && randomTime.passed(currRandom))
            {
                randomTime.reset();
                currRandom = -1;
                return attackFreq <= attackFrequencyConfig.getValue();
            }
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if the placement of an {@link EndCrystalItem} on
     * the param position was successful.
     *
     * @param p The position to place the crystal
     * @return <tt>true</tt> if the placement was successful
     *
     * @see PlayerInteractBlockC2SPacket
     */
    public boolean place(BlockPos p)
    {
        if (canUseOnBlock(p))
        {
            Direction dir = Direction.UP;
            if (strictDirectionConfig.getValue())
            {
                if (p.getY() > mc.player.getY() + mc.player.getStandingEyeHeight())
                {

                }
            }
            else
            {
                BlockHitResult result = mc.world.raycast(new RaycastContext(
                        mc.player.getEyePos(), new Vec3d(p.getX() + 0.5,
                        p.getY() + 0.5, p.getZ() + 0.5),
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE, mc.player));
                if (result != null && result.getType() == HitResult.Type.BLOCK)
                {
                    dir = result.getSide();
                    if (p.getY() > mc.world.getHeight())
                    {
                        dir = Direction.DOWN;
                    }
                }
            }
            BlockHitResult result = new BlockHitResult(p.toCenterPos(), dir,
                    p,  false);
            Hand hand = getCrystalHand();
            if (hand != null)
            {
                placeDirect(p, hand, result);
            }
            else
            {
                int prev = mc.player.getInventory().selectedSlot;
                if (swapConfig.getValue() != Swap.OFF)
                {
                    swap(EndCrystalItem.class);
                }
                placeDirect(p, Hand.MAIN_HAND, result);
                if (swapConfig.getValue() == Swap.SILENT)
                {
                    swap(prev);
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
     */
    private void placeDirect(BlockPos p, Hand hand, BlockHitResult result)
    {
        Managers.NETWORK.sendSequencedPacket(id ->
                new PlayerInteractBlockC2SPacket(hand, result, id));
        Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
        placements.add(p);
    }

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
                double d = p2.getX();
                double e = p2.getY();
                double f = p2.getZ();
                List<Entity> list = getCollisionList(p, new Box(d, e, f,
                        d + 1.0, e + (placeLowConfig.getValue() ? 1.0 : 2.0),
                        f + 1.0));
                return list.isEmpty();
            }
        }
    }

    /**
     * The {@link EndCrystalItem} cannot be placed on a {@link Box} with
     * other end crystals. However, we can predict
     *
     * @param p
     * @param box
     * @return
     */
    public List<Entity> getCollisionList(BlockPos p, Box box)
    {
        List<Entity> collisions = new CopyOnWriteArrayList<>(
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
                double dist = mc.player.distanceTo(e);
                if (dist > breakRangeConfig.getValue())
                {
                    continue;
                }
                BlockHitResult result = mc.world.raycast(new RaycastContext(
                        mc.player.getEyePos(), e.getPos(),
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE, mc.player));
                if (result != null && dist > breakWallRangeConfig.getValue())
                {
                    continue;
                }
                if (e.age < ticksExistedConfig.getValue() && !inhibitConfig.getValue())
                {
                    continue;
                }
                if (e.getPos().distanceTo(toSource(p)) <= 0.5)
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
        if (mc.player.getOffHandStack().getItem() instanceof EndCrystalItem)
        {
            return Hand.OFF_HAND;
        }
        else if (mc.player.getMainHandStack().getItem() instanceof EndCrystalItem)
        {
            return Hand.MAIN_HAND;
        }
        return null;
    }

    /**
     *
     *
     * @param clazz
     */
    public int swap(Class<? extends Item> clazz)
    {
        int slot = -1;
        for (int i = 0; i < 9; ++i)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && clazz.isInstance(stack.getItem()))
            {
                slot = i;
                break;
            }
        }
        if (slot != -1)
        {
            swap(slot);
            return slot;
        }
        return -1;
    }

    /**
     * 
     * 
     * @param slot
     */
    public void swap(int slot) 
    {
        mc.player.getInventory().selectedSlot = slot;
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    public void swapAlt(int slot)
    {

    }

    /**
     * Returns the {@link Vec3d} position of the damage source given a
     * {@link BlockPos} base.
     *
     * @param base The source base
     * @return The vector position of the damage src
     */
    private Vec3d toSource(BlockPos base)
    {
        return Vec3d.ofBottomCenter(base).add(0.0, 1.0, 0.0);
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
    private boolean isFeet(PlayerEntity e, BlockPos p)
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
     * @param o
     * @param radius
     * @return
     */
    private List<EndCrystalEntity> getCrystalSphere(Vec3d o, double radius)
    {
        List<EndCrystalEntity> sphere = new ArrayList<>();
        for (Entity e : mc.world.getEntities())
        {
            if (e != null && e.isAlive() && e instanceof EndCrystalEntity)
            {
                double dist = o.distanceTo(e.getPos());
                if (dist > radius)
                {
                    continue;
                }
                sphere.add((EndCrystalEntity) e);
            }
        }
        return sphere;
    }

    /**
     *
     *
     * @param o
     * @param radius
     * @return
     */
    private List<BlockPos> getSphere(Vec3d o, double radius)
    {
        List<BlockPos> sphere = new ArrayList<>();
        double rad = Math.ceil(radius);
        for (double x = -rad; x <= rad; ++x)
        {
            for (double y = -rad; y <= rad; ++y)
            {
                for (double z = -rad; z <= rad; ++z)
                {
                    Vec3i p = new Vec3i((int) (o.getX() + x),
                            (int) (o.getY() + y), (int) (o.getZ() + z));
                    double dist = placeRangeCenterConfig.getValue() ?
                            p.getSquaredDistanceFromCenter(o.getX(), o.getY(),
                                    o.getZ()) : p.getSquaredDistance(o);
                    if (dist <= radius * radius)
                    {
                        sphere.add(new BlockPos(p));
                    }
                }
            }
        }
        return sphere;
    }

    /**
     *
     *
     * @param e
     * @param src
     * @return
     */
    public double getDamage(Entity e, Vec3d src)
    {
        if (blockDestructionConfig.getValue())
        {

        }
        return 0.0;
    }

    /**
     *
     *
     * @param dest
     */
    private int setRotation(Vec3d dest)
    {
        float[] rots = RotationUtil.getRotationsTo(mc.player.getEyePos(), dest);
        // PLEASE GOD LET THIS BE THE LAST HACK
        if (isNearlyEqual(facing, dest, 0.05f))
        {
            return rotating;
        }
        else
        {
            facing = dest;
            if (yawStepConfig.getValue() != YawStep.OFF)
            {
                float diff = rots[0] - mc.player.getYaw(); // yaw diff
                if (Math.abs(diff) > 180.0f)
                {
                    diff += diff > 0.0f ? -360.0f : 360.0f;
                }
                int dir = diff > 0.0f ? 1 : -1;
                // partition yaw
                int tick = yawStepTicksConfig.getValue();
                float step = Math.abs(diff) / tick;
                if (step > yawStepThresholdConfig.getValue())
                {
                    tick = (int) Math.ceil(Math.abs(diff) / yawStepThresholdConfig.getValue());
                    step = Math.abs(diff) / tick;
                }
                yaws = new float[tick];
                float total = 0;
                for (int i = 0; i < tick; ++i)
                {
                    float curr = step * dir;
                    if (Math.abs(curr) > 0.05f)
                    {
                        total += curr;
                        yaws[i] = total;
                    }
                }
                pitch = rots[0];
                return yaws.length;
            }
            else
            {
                yaws = new float[1];
                yaws[0] = rots[0];
                pitch = rots[1];
                return 1;
            }
        }
    }

    /**
     * Returns <tt>true</tt> if the two {@link Vec3d} positions are close and
     * within the allowed error range
     *
     * @param v1 The first vector
     * @param v2 The second vector
     * @param allowed Allowed error range
     * @return Returns <tt>true</tt> if the two positions are the same
     */
    public boolean isNearlyEqual(Vec3d v1, Vec3d v2, float allowed)
    {
        double x = Math.abs(v1.x - v2.x);
        double y = Math.abs(v1.y - v2.y);
        double z = Math.abs(v1.z - v2.z);
        return x <= allowed && y <= allowed && z <= allowed;
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
        NONE
    }

    public enum Placements
    {
        NATIVE,
        PROTOCOL
    }
    
    public enum YawStep 
    {
        FULL,
        SEMI,
        OFF
    }

    /**
     *
     */
    private class CalcFactory
    {
        /**
         *
         *
         * @param crystalSrc
         * @param placeSrc
         * @return
         */
        public TickCalculation build(Iterable<EndCrystalEntity> crystalSrc,
                                     Iterable<BlockPos> placeSrc)
        {
            TickCalculation calc = new TickCalculation(crystalSrc, placeSrc);
            calc.submit();
            return calc;
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
        private final ExecutorCompletionService<Process> service =
                new ExecutorCompletionService<>(pool);
        // Src
        private final Iterable<EndCrystalEntity> crystalSrc;
        private final Iterable<BlockPos> placeSrc;
        // Calculated
        private DamageData<EndCrystalEntity> attackCalc;
        private DamageData<BlockPos> placeCalc;
        // Calculation time information
        private long start, done;

        /**
         *
         *
         * @param crystalSrc
         * @param placeSrc
         */
        public TickCalculation(Iterable<EndCrystalEntity> crystalSrc,
                               Iterable<BlockPos> placeSrc)
        {
            this.id = UUID.randomUUID();
            this.crystalSrc = crystalSrc;
            this.placeSrc = placeSrc;
        }

        /**
         *
         *
         * @see ExecutorCompletionService
         *
         * @see #getCrystal(Iterable)
         * @see #getPlace(Iterable)
         */
        public void submit()
        {
            service.submit(() -> toProcess());
            start = System.currentTimeMillis();
        }

        /**
         *
         *
         * @return
         */
        private Process toProcess()
        {
            return new Process(getCrystal(crystalSrc), getPlace(placeSrc));
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
        public void retrieve() throws InterruptedException,
                ExecutionException
        {
            if (!isDone())
            {
                Future<Process> result = service.take();
                if (result != null)
                {
                    Process data = result.get();
                    if (data != null)
                    {
                        placeCalc = data.place();
                        attackCalc = data.attack();
                    }
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
        public long getCalcTime()
        {
            return done - start;
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

        /**
         *
         *
         * @return
         *
         * @see #getDamage(Entity, Vec3d)
         * @see #getCrystalSphere(Vec3d, double)
         */
        private DamageData<EndCrystalEntity> getCrystal(Iterable<EndCrystalEntity> src)
        {
            if (mc.world != null && mc.player != null)
            {
                TreeMap<Double, DamageData<EndCrystalEntity>> min = new TreeMap<>();
                for (EndCrystalEntity c : src)
                {
                    double dist = mc.player.distanceTo(c);
                    if (dist > breakRangeConfig.getValue())
                    {
                        continue;
                    }
                    BlockHitResult result = mc.world.raycast(new RaycastContext(
                            mc.player.getEyePos(), c.getPos(),
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE, mc.player));
                    if (result != null && dist > breakWallRangeConfig.getValue())
                    {
                        continue;
                    }
                    if (c.age < ticksExistedConfig.getValue() && !inhibitConfig.getValue())
                    {
                        continue;
                    }
                    double local = getDamage(mc.player, c.getPos());
                    // player safety
                    if (safetyConfig.getValue() && !mc.player.isCreative())
                    {
                        if (local + 0.5 > mc.player.getHealth() + mc.player.getAbsorptionAmount())
                        {
                            continue;
                        }
                        if (local > maxLocalDamageConfig.getValue())
                        {
                            continue;
                        }
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
                                double pdist = mc.player.distanceTo(e);
                                if (pdist > targetRangeConfig.getValue())
                                {
                                    continue;
                                }
                                double target = getDamage(e, c.getPos());
                                min.put(target, new DamageData<>(isLethal(e,
                                        target) ? 999.0 : target, local, e, c)); // BIG UGLY HACK
                            }
                        }
                    }
                }
                if (!min.isEmpty())
                {
                    Map.Entry<Double, DamageData<EndCrystalEntity>> f =
                            min.lastEntry();
                    if (f.getKey() > minDamageConfig.getValue())
                    {
                        return f.getValue();
                    }
                }
            }
            return null;
        }

        /**
         *
         *
         * @return
         *
         * @see #getDamage(Entity, Vec3d)
         * @see #getSphere(Vec3d, double)
         */
        private DamageData<BlockPos> getPlace(Iterable<BlockPos> src)
        {
            if (mc.world != null && mc.player != null)
            {
                TreeMap<Double, DamageData<BlockPos>> min = new TreeMap<>();
                // placement processing
                for (BlockPos p : src)
                {
                    Vec3d pos = placeRangeEyeConfig.getValue() ?
                            mc.player.getEyePos() : mc.player.getPos();
                    double dist = placeRangeCenterConfig.getValue() ?
                            p.getSquaredDistanceFromCenter(pos.getX(), pos.getY(),
                                    pos.getZ()) : p.getSquaredDistance(pos);
                    if (dist > placeRangeConfig.getValue() * placeRangeConfig.getValue())
                    {
                        continue;
                    }
                    Vec3d expected = new Vec3d(p.getX() + 0.5,
                            p.getY() + 2.70000004768372, p.getZ() + 0.5);
                    BlockHitResult result = mc.world.raycast(new RaycastContext(
                            mc.player.getEyePos(), expected,
                            RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE, mc.player));
                    float maxDist = 36.0f;
                    if (result != null && result.getType() == HitResult.Type.BLOCK
                            && result.getBlockPos() != p)
                    {
                        maxDist = 9.0f;
                        if (dist > placeWallRangeConfig.getValue() * placeWallRangeConfig.getValue())
                        {
                            continue;
                        }
                    }
                    if (breakValidateConfig.getValue() && dist > maxDist)
                    {
                        continue;
                    }
                    double local = getDamage(mc.player, toSource(p));
                    // player safety
                    if (safetyConfig.getValue() && !mc.player.isCreative())
                    {
                        if (local + 0.5 > mc.player.getHealth() + mc.player.getAbsorptionAmount())
                        {
                            continue;
                        }
                        if (local > maxLocalDamageConfig.getValue())
                        {
                            continue;
                        }
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
                            if (p.getSquaredDistance(e.getPos()) > 144.0f)
                            {
                                continue;
                            }
                            if (isEnemy(e))
                            {
                                double pdist = mc.player.distanceTo(e);
                                // double edist = e.squaredDistanceTo(p.toCenterPos());
                                if (pdist > targetRangeConfig.getValue())
                                {
                                    continue;
                                }
                                double target = getDamage(e, toSource(p));
                                min.put(target, new DamageData<>(isLethal(e,
                                        target) ? 999.0 : target, local, e, p)); // BIG UGLY HACK
                            }
                        }
                    }
                }
                if (!min.isEmpty())
                {
                    Map.Entry<Double, DamageData<BlockPos>> f = min.lastEntry();
                    if (f.getKey() > minDamageConfig.getValue())
                    {
                        return f.getValue();
                    }
                }
            }
            return null; // no valid placements
        }

        /**
         *
         *
         * @param e
         * @param damage
         * @return
         */
        public boolean isLethal(Entity e, double damage)
        {
            float ehealth = 144.0f;
            float earmor = 100.0f;
            if (e instanceof LivingEntity)
            {
                ehealth = ((LivingEntity) e).getHealth() + ((LivingEntity) e).getAbsorptionAmount();
                if (armorScaleConfig.getValue() != 0.0f)
                {
                    float dmg = 0.0f, t = 0.0f;
                    for (ItemStack a : e.getArmorItems())
                    {
                        dmg += a.getDamage();
                        t += a.getMaxDamage();
                    }
                    earmor = dmg / t;
                }
            }
            double lethal = lethalMultiplier.getValue() * damage;
            return lethal + 0.5 > ehealth
                    || earmor < armorScaleConfig.getValue();
        }

        /**
         *
         *
         * @param attack
         * @param place
         */
        private record Process(DamageData<EndCrystalEntity> attack,
                              DamageData<BlockPos> place)
        {

        }
    }

    /**
     *
     *
     * @param target
     * @param local
     * @param damaged
     * @param src
     *
     * @param <T> The damage source type
     */
    public record DamageData<T>(double target, double local, Entity damaged,
                                T src)
    {

    }
}