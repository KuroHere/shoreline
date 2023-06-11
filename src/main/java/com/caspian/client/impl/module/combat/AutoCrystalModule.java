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
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.mixin.accessor.AccessorPlayerInteractEntityC2SPacket;
import com.caspian.client.util.math.NanoTimer;
import com.caspian.client.util.math.TickTimer;
import com.caspian.client.util.math.Timer;
import com.caspian.client.util.ncp.DirectionChecks;
import com.caspian.client.util.player.RotationUtil;
import com.caspian.client.util.world.EntityUtil;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Threaded AutoCrystal implementation.
 * <p>
 * TODO:
 * <li> Manual crystals
 * </p>
 *
 * @author linus
 * @since 1.0
 *
 * @see TickCalculation
 * @see ThreadCalcProcessor
 * @see DamageData
 */
public class AutoCrystalModule extends ToggleModule
{
    // GENERAL SETTINGS
    Config<Boolean> multitaskConfig = new BooleanConfig("Multitask",
            "Allows attacking while using items", false);
    Config<Boolean> whileMiningConfig = new BooleanConfig("WhileMining",
            "Allows attacking while mining blocks", false);
    Config<Float> targetRangeConfig = new NumberConfig<>("EnemyRange",
            "Range to search for potential enemies", 1.0f, 10.0f, 13.0f);
    Config<Boolean> instantConfig = new BooleanConfig("Instant",
            "Instantly attacks crystals when they spawn", false);
    Config<Boolean> multithreadConfig = new BooleanConfig("Concurrent",
            "Attempts to run calculations in the background of the main " +
                    "minecraft ticks on a seperate thread pool", false);
    Config<Boolean> instantCalcConfig = new BooleanConfig("Real-TimeCalc",
            "Calculates a crystal when it spawns and attacks if it meets " +
                    "MINIMUM requirements, this will result in non-ideal " +
                    "crystal attacks", false);
    Config<Float> calcSleepConfig = new NumberConfig<>("CalcSleep", "Time to " +
            "sleep and pause calculation directly after completing a " +
            "calculation", 0.0f, 0.05f, 0.1f);
    // Config<Boolean> raytraceConfig = new BooleanConfig("Raytrace", "",
    //        false);
    Config<Sequential> sequentialConfig = new EnumConfig<>("Sequential",
            "Calculates sequentially, so placements occur once the " +
                    "expected crystal is broken", Sequential.NORMAL,
            Sequential.values());
    Config<Boolean> swingConfig = new BooleanConfig("Swing",
            "Swing hand when placing and attacking crystals", true);
    // ROTATE SETTINGS
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotate" +
            "before placing and breaking", false);
    Config<Boolean> ignoreExpectedTickConfig = new BooleanConfig(
            "IgnoreLastTick", "Allow actions on tick before reaching rotation",
            false);
    Config<Rotate> strictAnglesConfig = new EnumConfig<>("StrictAngles",
            "Rotates yaw over multiple ticks to prevent certain rotation  " +
                    "flags in NCP", Rotate.OFF, Rotate.values());
    Config<Integer> angleThresholdConfig = new NumberConfig<>(
            "AngleThreshold", "Maximum yaw rotation in degrees for one tick",
            1, 180, 180, NumberDisplay.DEGREES);
    Config<Integer> yawTicksConfig = new NumberConfig<>("YawTicks",
            "Minimum ticks to rotate yaw", 1, 1, 5);
    Config<Integer> rotateSuspendConfig = new NumberConfig<>(
            "RotateSleep", "Minimum ticks to hold the rotation yaw after " +
            "reaching the rotation", 0, 0, 5);
    Config<Float> randomVectorConfig = new NumberConfig<>("RandomVector",
            "Randomizes attack rotations", 0.0f, 0.0f, 1.0f);
    Config<Boolean> offsetFacingConfig = new BooleanConfig("InteractOffset",
            "Rotates to the side of interact (only applies to PLACE " +
                    "rotations)", false);
    Config<Integer> rotatePreserveTicksConfig = new NumberConfig<>(
            "PreserveTicks", "Time to preserve rotations before switching " +
            "back", 0, 20, 20);
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
            "Added delays", 0.0f, 0.5f, 10.0f);

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
    Config<Float> breakRangeConfig = new NumberConfig<>("BreakRange",
            "Range to break crystals", 0.1f, 4.0f, 5.0f);
    Config<Float> strictBreakRangeConfig = new NumberConfig<>(
            "StrictBreakRange", "NCP range to break crystals", 0.1f, 4.0f,
            5.0f);
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
    Config<Boolean> inhibitConfig = new BooleanConfig("Inhibit",
            "Prevents excessive attacks", true);
    Config<Integer> inhibitTicksConfig = new NumberConfig<>("InhibitTicks",
            "Counts crystals for x amount of ticks before determining that " +
                    "the attack won't violate NCP attack speeds (aka " +
                    "shortterm ticks)", 5, 8, 15);
    Config<Integer> inhibitLimitConfig = new NumberConfig<>("CrystalLimit",
            "Limit to crystal attacks that would flag NCP attack limits",
            1, 13, 20);
    // default NCP config
    // limitforseconds:
    //        half: 8
    //        one: 15
    //        two: 30
    //        four: 60
    //        eight: 100
    Config<Integer> attackFreqConfig = new NumberConfig<>(
            "AttackFreq-Half", "Limit of attack packets sent for each " +
            "half-second interval", 1, 8, 20);
    Config<Integer> attackFreqFullConfig = new NumberConfig<>(
            "AttackFreq-Full", "Limit of attack packets sent for each " +
            "one-second interval", 10, 15, 30);
    Config<Integer> attackFreqMaxConfig = new NumberConfig<>(
            "AttackFreq-Max", "Limit of attack packets sent for each " +
            "eight-second interval", 80, 100, 150);
    // Config<Boolean> manualConfig = new BooleanConfig("Manual",
    //        "Always breaks manually placed crystals", false);
    // PLACE SETTINGS
    Config<Boolean> placeConfig = new BooleanConfig("Place", "Places crystals" +
            " to damage enemies. Place settings will only function if this " +
            "setting is enabled.", true);
    Config<Float> placeSpeedConfig = new NumberConfig<>("PlaceSpeed",
            "Speed to place crystals", 0.1f, 18.0f, 20.0f);
    Config<Float> placeTimeoutConfig = new NumberConfig<>("PlaceTimeout",
            "Time after waiting for the average place time before considering" +
                    " a crystal placement failed", 0.0f, 3.0f, 10.0f);
    Config<Float> placeRangeConfig = new NumberConfig<>("PlaceRange",
            "Range to place crystals", 0.1f, 4.0f, 5.0f);
    Config<Float> strictPlaceRangeConfig = new NumberConfig<>(
            "StrictPlaceRange", "NCP range to place crystals", 0.1f, 4.0f,
            5.0f);
    Config<Float> placeWallRangeConfig = new NumberConfig<>(
            "PlaceWallRange", "Range to place crystals through walls", 0.1f,
            4.0f, 5.0f);
    Config<Boolean> minePlaceConfig = new BooleanConfig("MinePlace",
            "Places on mining blocks that when broken, can be placed on to " +
                    "damage enemies. Instantly destroys items spawned from " +
                    "breaking block and allows faster placing", false);
    Config<Boolean> placeRangeEyeConfig = new BooleanConfig(
            "PlaceRangeEye", "Calculates place ranges starting from the eye " +
            "position of the player, which is how NCP calculates ranges",
            false);
    Config<Boolean> placeRangeCenterConfig = new BooleanConfig(
            "PlaceRangeCenter", "Calculates place ranges to the center of the" +
            " block, which is how NCP calculates ranges", true);
    Config<Boolean> placeLowConfig = new BooleanConfig("PlaceLow", "Allow " +
            "placements at a lower bounding", false);
    Config<Boolean> antiTotemConfig = new BooleanConfig("AntiTotem",
            "Predicts totems and places crystals to instantly double pop and " +
                    "kill the target", false);
    Config<Swap> swapConfig = new EnumConfig<>("Swap", "Swaps to an end " +
            "crystal before placing if the player is not holding one", Swap.OFF,
            Swap.values());
    Config<Boolean> swapSyncConfig = new BooleanConfig("SwapSync",
            "", false);
    Config<Float> alternateSpeedConfig = new NumberConfig<>("AlternateSpeed",
            "Speed for alternative swapping crystals", 1.0f, 18.0f, 20.0f);
    Config<Boolean> breakValidateConfig = new BooleanConfig(
            "BreakValidate", "Only places crystals that can be attacked",
            false);
    Config<Boolean> strictDirectionConfig = new BooleanConfig(
            "StrictDirection", "Interacts with only visible directions when " +
            "placing crystals", false);
    Config<Placements> placementsConfig = new EnumConfig<>("Placements",
            "Version standard for placing end crystals", Placements.NATIVE,
            Placements.values());
    // DAMAGE SETTINGS
    Config<Float> minDamageConfig = new NumberConfig<>("MinDamage",
            "Minimum damage required to consider attacking or placing an end " +
                    "crystal", 1.0f, 4.0f, 10.0f);
    Config<Float> armorScaleConfig = new NumberConfig<>("ArmorScale",
            "", 0.0f, 5.0f, 20.0f, NumberDisplay.PERCENT);
    Config<Float> lethalMultiplier = new NumberConfig<>(
            "LethalMultiplier", "If we can kill an enemy with this many " +
            "crystals, disregard damage values", 0.0f, 1.5f, 4.0f);
    Config<Boolean> safetyConfig = new BooleanConfig("Safety",  "Accounts for" +
            " total player safety when attacking and placing crystals", true);
    Config<Float> safetyBalanceConfig = new NumberConfig<>(
            "SafetyBalance", "", 1.0f, 3.0f, 5.0f);
    Config<Boolean> safetyOverride = new BooleanConfig("SafetyOverride",
            "Overrides the safety checks if the crystal will kill an enemy",
            false);
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
    private int tick;
    //
    private DamageData<BlockPos> placeData, lastPlaceData;
    private DamageData<EndCrystalEntity> attackData, lastAttackData;
    private BlockPos mining;
    //
    private BlockPos preSequence, postSequence;
    private Vec3d sequence;
    private final Timer tickSequence = new TickTimer();
    private final Timer startSequence = new NanoTimer();
    // Calculated placements and attacks will be added to their respective
    // stacks. When the main loop requires a placement/attack, simply pop the
    // last calculated from the stack.
    private final ThreadCalcProcessor processor = new ThreadCalcProcessor();
    private boolean pauseCalcAttack, pauseCalcPlace;
    // Set of attempted placements and attacks
    private final Map<BlockPos, Long> placements =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    private final Map<Integer, Long> attacks =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    private int shortTermCount;
    private int shortTermTick;
    // RANDOM
    private final Timer randomTime = new NanoTimer();
    private final Random random = new SecureRandom();
    private long currRandom;
    //
    private final Map<PlayerEntity, Long> pops =
            Collections.synchronizedMap(new ConcurrentHashMap<>());
    //
    private final Timer freqInterval = new NanoTimer();
    private final int[] attackFreq = new int[16];
    private int freq;
    //
    private boolean attacking, placing;
    private final Timer lastPlace = new NanoTimer();
    private final Timer lastBreak = new NanoTimer();
    //
    private final Deque<Long> breakTimes = new ArrayDeque<>(20);
    private final Deque<Long> placeTimes = new ArrayDeque<>(20);
    private final Timer lastSwap = new NanoTimer();
    private final Timer lastSwapAlt = new NanoTimer();
    private final Timer lastAutoSwap = new NanoTimer();
    // private final Timer lastClean = new Timer();
    // ROTATIONS
    //
    private final Timer rotateTimer = new TickTimer();
    private int rotating;
    //
    private float[] yaws;
    private boolean semi;
    private float yaw, pitch;
    // RENDER
    private final AtomicReference<Box> renderBreak =
            new AtomicReference<>();
    private final AtomicReference<BlockPos> renderPlace =
            new AtomicReference<>();

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
            // run calc
            Iterable<Entity> worldEntities = mc.world.getEntities();
            Iterable<EndCrystalEntity> crystals = getCrystalSphere(
                    Managers.POSITION.getCameraPosVec(1.0f),
                    breakRangeConfig.getValue() + 0.5);
            Iterable<BlockPos> blocks = getSphere(placeRangeEyeConfig.getValue() ?
                            Managers.POSITION.getEyePos() : Managers.POSITION.getPos(),
                    placeRangeConfig.getValue() + 0.5);
            ArrayList<Entity> entities = new ArrayList<>();
            worldEntities.forEach(e -> entities.add(e));
            processor.runCalc(entities, crystals, blocks);
        }
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        processor.shutdownNow();
        clear();
    }

    /**
     *
     *
     */
    private void tickCalc()
    {
        TickCalculation calc = processor.getCurrentCalc();
        if (calc != null)
        {
            try
            {
                calc.checkDone();
            }
            catch (InterruptedException | ExecutionException e)
            {
                Caspian.error("Failed calculation %s!", calc.getId());
                e.printStackTrace();
            }
            if (calc.isDone())
            {
                Caspian.info("Calc done for tick calculations in %dms!",
                        calc.getCalcTime());
                DamageData<EndCrystalEntity> attackData =
                        calc.getCalcAttack();
                if (!pauseCalcAttack)
                {
                    this.attackData = attackData;
                }
                DamageData<BlockPos> placeData =
                        calc.getCalcPlace();
                if (!pauseCalcPlace)
                {
                    mining = placeData.isMineDamage() ? placeData.getSrc() :
                            null;
                    this.placeData = placeData;
                }
                // IMPORTANT NOTE FOR ROTATIONS:
                // If we have found new data, stop the current rotation and
                // start a new one.
                // This means that if the rotations steps >= 1,
                // then there is a possibility that the current rotation may
                // fallthrough and never actually complete.
                if (rotating > 0
                        && (semi && lastAttackData.getYawDiff(this.attackData) > 30.0f
                        && !lastAttackData.isSrcEqual(this.attackData)
                        || lastPlaceData.getYawDiff(this.placeData) > 30.0f
                        && !lastPlaceData.isSrcEqual(this.placeData)))
                {
                    rotating = 0;
                    rotateTimer.setElapsedTime(Timer.MAX_TIME);
                }
                lastAttackData = this.attackData;
                lastPlaceData = this.placeData;
                // run calc
                Iterable<Entity> worldEntities = mc.world.getEntities();
                Iterable<EndCrystalEntity> crystals = getCrystalSphere(
                        Managers.POSITION.getCameraPosVec(1.0f),
                        breakRangeConfig.getValue() + 0.5);
                Iterable<BlockPos> blocks = getSphere(placeRangeEyeConfig.getValue() ?
                                Managers.POSITION.getEyePos() :
                                Managers.POSITION.getPos(),
                        placeRangeConfig.getValue() + 0.5);
                ArrayList<Entity> entities = new ArrayList<>();
                worldEntities.forEach(e -> entities.add(e));
                processor.scheduleCalc(entities, crystals, blocks,
                        calcSleepConfig.getValue());
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
        renderBreak.set(null);
        renderPlace.set(null);
        pauseCalcAttack = false;
        pauseCalcPlace = false;
        attacking = false;
        placing = false;
        semi = false;
        tick = 0;
        shortTermTick = 0;
        shortTermCount = 0;
        rotating = 0;
        currRandom = -1;
        lastBreak.reset();
        lastPlace.reset();
        startSequence.reset();
        freqInterval.reset();
        attacks.clear();
        placements.clear();
        // breakTimes.clear();
    }

    /**
     *
     */
    private void cleanCrystals()
    {
        float timeout = Math.max(getLatency(breakTimes) + (50.0f * breakTimeoutConfig.getValue()),
                50.0f * minTimeoutConfig.getValue());
        for (Map.Entry<Integer, Long> e : attacks.entrySet())
        {
            long time = System.currentTimeMillis() - e.getValue();
            if (time > timeout)
            {
                attacks.remove(e.getKey());
            }
        }
        timeout = Math.max(getLatency(placeTimes) + (50.0f * placeTimeoutConfig.getValue()),
                50.0f * minTimeoutConfig.getValue());
        for (Map.Entry<BlockPos, Long> e : placements.entrySet())
        {
            long time = System.currentTimeMillis() - e.getValue();
            if (time > timeout)
            {
                placements.remove(e.getKey());
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
                if (rotating > 0)
                {
                    rotateTimer.reset();
                }
                if (!rotateTimer.passed(rotatePreserveTicksConfig.getValue()))
                {
                    yaw = yaws[--rotating];
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                    event.cancel();
                }
                if (rotating > 0 && !ignoreExpectedTickConfig.getValue())
                {
                    return;
                }
                if (attackData != null && evaluate(attackData))
                {
                    attacking = true;
                    if (rotateConfig.getValue()
                            && checkFacing(attackData.getBoundingBox()))
                    {
                        semi = true;
                        float[] dest = attackData.getRotationsTo(Managers.POSITION.getEyePos());
                        rotating = setRotation(dest, strictAnglesConfig.getValue() != Rotate.OFF);
                        rotateTimer.reset();
                        // rotate instantly
                        if (!event.isCanceled())
                        {
                            yaw = yaws[--rotating];
                            event.setYaw(yaw);
                            event.setPitch(pitch);
                            event.cancel();
                        }
                        return;
                    }
                    if (currRandom < 0)
                    {
                        currRandom = random.nextLong((long)
                                (randomSpeedConfig.getValue() * 10.0f + 1.0f));
                    }
                    float delay = (((NumberConfig<Float>) breakSpeedConfig).getMax()
                            - breakSpeedConfig.getValue()) * 50.0f;
                    if (instantConfig.getValue())
                    {
                        float timeout = Math.max(getLatency(breakTimes) + (50.0f * breakTimeoutConfig.getValue()),
                                50.0f * minTimeoutConfig.getValue());
                        delay = timeout;
                    }
                    if (lastBreak.passed(delay) 
                            && randomTime.passed(currRandom))
                    {
                        if (attack(attackData))
                        {
                            lastBreak.reset();
                            randomTime.reset();
                            currRandom = -1;
                        }
                        attackData = null;
                        pauseCalcAttack = false;
                    }
                }
                if (placeData != null && evaluate(placeData))
                {
                    placing = true;
                    if (rotateConfig.getValue()
                            && checkFacing(placeData.getBoundingBox()))
                    {
                        semi = false;
                        float[] dest = placeData.getRotationsTo(Managers.POSITION.getEyePos());
                        rotating = setRotation(dest, strictAnglesConfig.getValue() == Rotate.FULL);
                        rotateTimer.reset();
                        // rotate instantly
                        if (!event.isCanceled())
                        {
                            yaw = yaws[--rotating];
                            event.setYaw(yaw);
                            event.setPitch(pitch);
                            event.cancel();
                        }
                        return;
                    }
                    float delay = (((NumberConfig<Float>) placeSpeedConfig).getMax()
                            - placeSpeedConfig.getValue()) * 50.0f;
                    if (lastPlace.passed(delay))
                    {
                        if (place(placeData))
                        {
                            lastPlace.reset();
                        }
                        placeData = null;
                        pauseCalcPlace = false;
                    }
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
        if (mc.player != null && mc.world != null)
        {
            if (renderConfig.getValue())
            {
                int color = Modules.COLORS.getRGB();
                if (renderAttackConfig.getValue())
                {
                    Box rb = renderBreak.get();
                    if (rb != null)
                    {
                        RenderManager.renderBoundingBox(rb,
                                1.5f, color);
                    }
                }
                BlockPos rp = renderPlace.get();
                if (rp != null)
                {
                    RenderManager.renderBox(rp, color);
                    RenderManager.renderBoundingBox(rp, 1.5f, color);
                }
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
        Entity damaged = d.getTarget();
        if (damaged != null && damaged.isAlive())
        {
            // if (d.src() instanceof BlockPos src)
            // {

            // }
            if (d.isAttackDamage())
            {
                return ((EndCrystalEntity) d.getSrc()).isAlive();
            }
        }
        return false;
    }

    /**
     *
     *
     * @param attack
     */
    private void setAttackHard(DamageData<EndCrystalEntity> attack)
    {
        this.attackData = attack;
        pauseCalcAttack = true;
    }

    /**
     *
     *
     * @param place
     */
    private void setPlaceHard(DamageData<BlockPos> place)
    {
        this.placeData = place;
        pauseCalcPlace = true;
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
     * @return
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
    private float getLatency(Deque<Long> times)
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
            return avg / size;
        }
        return 0.0f;
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
            if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet
                    && event.isCached())
            {
                if (((AccessorPlayerInteractEntityC2SPacket) packet).hookGetTypeHandler().getType()
                        == PlayerInteractEntityC2SPacket.InteractType.ATTACK)
                {
                    MinecraftServer server = mc.world.getServer();
                    RegistryKey<World> world = mc.world.getRegistryKey();
                    Entity e = packet.getEntity(server.getWorld(world));
                    if (e != null && e.isAlive()
                            && e instanceof EndCrystalEntity)
                    {
                        if (sequence != null)
                        {
                            float timeout = Math.max(getLatency(breakTimes) + (50.0f * breakTimeoutConfig.getValue()),
                                    50.0f * minTimeoutConfig.getValue());
                            if (startSequence.passed(timeout))
                            {
                                Caspian.error("Latest sequence timed out!");
                                sequence = null;
                            }
                            else
                            {
                                return;
                            }
                        }
                        sequence = e.getPos();
                        startSequence.reset();
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
                lastAutoSwap.reset();
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
                    Entity e = packet.getEntity(mc.world);
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
                    if (instantConfig.getValue())
                    {
                        Vec3d spawn = new Vec3d(packet.getX(), packet.getY(),
                                packet.getZ());
                        BlockPos sblock = BlockPos.ofFloored(spawn);
                        Vec3d base = new Vec3d(packet.getX() - 0.5,
                                packet.getY() - 1.0, packet.getZ() - 0.5);
                        if (postCheckBreakRange(spawn))
                        {
                            return;
                        }
                        cleanCrystals();
                        final BlockPos pos = BlockPos.ofFloored(base);
                        Long placeTime = placements.remove(pos);
                        if (placeTime != null)
                        {
                            if (mining != null && mining.equals(pos))
                            {
                                return;
                            }
                            placeTimes.push(System.currentTimeMillis() - placeTime);
                            if (rotateConfig.getValue()
                                    && checkFacing(new Box(sblock)))
                            {
                                // Can't take advantage of instant break,
                                // most that can be done is signal to
                                // clear the next break ASAP
                                if (strictAnglesConfig.getValue() != Rotate.OFF
                                        || attackDelayConfig.getValue() > 0.0f
                                        || !ignoreExpectedTickConfig.getValue())
                                {
                                    lastBreak.setElapsedTime(Timer.MAX_TIME);
                                    return;
                                }
                                attacking = true;
                                float[] dest = RotationUtil.getRotationsTo(Managers.POSITION.getEyePos(),
                                        spawn);
                                semi = true;
                                rotating = setRotation(dest, strictAnglesConfig.getValue() != Rotate.OFF);
                                rotateTimer.reset();
                            }
                            if (attack(packet.getId()))
                            {
                                lastBreak.reset();
                                attacks.put(packet.getId(),
                                        System.currentTimeMillis());
                            }
                        }
                        else if (instantCalcConfig.getValue())
                        {
                            double local = getDamage(mc.player, spawn);
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
                                    double pdist = Managers.POSITION.squaredDistanceTo(e);
                                    // double edist = e.squaredDistanceTo(p.toCenterPos());
                                    if (pdist > targetRangeConfig.getValue()
                                            * targetRangeConfig.getValue())
                                    {
                                        continue;
                                    }
                                    if (isEnemy(e))
                                    {
                                        double dmg = getDamage(e, spawn);
                                        float ehealth = 0.0f;
                                        if (e instanceof LivingEntity)
                                        {
                                            ehealth = ((LivingEntity) e).getHealth()
                                                    + ((LivingEntity) e).getAbsorptionAmount();
                                        }
                                        DamageData<BlockPos> pdata =
                                                new DamageData<>(e,
                                                        BlockPos.ofFloored(spawn),
                                                        dmg, local);
                                        DamageData<EndCrystalEntity> data =
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
                                        if (checkArmor(e))
                                        {
                                            data.addTag("armorbreak");
                                        }
                                        if (data.isDamageValid(minDamageConfig.getValue())
                                                && !unsafe)
                                        {
                                            if (rotateConfig.getValue()
                                                    && checkFacing(new Box(sblock)))
                                            {
                                                if (strictAnglesConfig.getValue() != Rotate.OFF
                                                        || attackDelayConfig.getValue() > 0.0f
                                                        || !ignoreExpectedTickConfig.getValue())
                                                {
                                                    lastBreak.setElapsedTime(Timer.MAX_TIME);
                                                    return;
                                                }
                                                attacking = true;
                                                float[] dest = RotationUtil.getRotationsTo(
                                                        Managers.POSITION.getEyePos(),
                                                        spawn);
                                                semi = true;
                                                rotating = setRotation(dest, strictAnglesConfig.getValue() != Rotate.OFF);
                                                rotateTimer.reset();
                                            }
                                            if (attack(packet.getId()))
                                            {
                                                lastBreak.reset();
                                                attacks.put(packet.getId(),
                                                        System.currentTimeMillis());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof ExplosionS2CPacket packet)
            {
                if (sequence != null && sequence.squaredDistanceTo(packet.getX(),
                        packet.getY(), packet.getZ()) < packet.getRadius() * packet.getRadius())
                {
                    sequence = null;
                    if (sequentialConfig.getValue() == Sequential.NORMAL)
                    {
                        tickCalc();
                        if (placeData != null && evaluate(placeData))
                        {
                            placing = true;
                            if (rotateConfig.getValue()
                                    && checkFacing(placeData.getBoundingBox()))
                            {
                                if (strictAnglesConfig.getValue() != Rotate.OFF
                                        || !ignoreExpectedTickConfig.getValue())
                                {
                                    lastPlace.setElapsedTime(Timer.MAX_TIME);
                                    return;
                                }
                                semi = false;
                                float[] dest = placeData.getRotationsTo(Managers.POSITION.getEyePos());
                                rotating = setRotation(dest, strictAnglesConfig.getValue() == Rotate.FULL);
                                rotateTimer.reset();
                            }
                            if (place(placeData))
                            {
                                lastPlace.reset();
                            }
                            placeData = null;
                            pauseCalcPlace = false;
                        }
                    }
                }
                for (Entity e : mc.world.getEntities())
                {
                    if (e != null && e.isAlive() && e instanceof EndCrystalEntity)
                    {
                        if (e.squaredDistanceTo(packet.getX(), packet.getY(),
                                packet.getZ()) < packet.getRadius() * packet.getRadius())
                        {
                            // only set dead our crystals
                            Long breakTime = attacks.remove(e.getId());
                            if (breakTime != null)
                            {
                                e.kill();
                                breakTimes.push(System.currentTimeMillis() - breakTime);
                            }
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof PlaySoundS2CPacket packet)
            {
                if (packet.getSound().value() == SoundEvents.ENTITY_GENERIC_EXPLODE)
                {
                    if (sequence != null && sequence.squaredDistanceTo(packet.getX(),
                            packet.getY(), packet.getZ()) < 144.0)
                    {
                        sequence = null;
                        if (sequentialConfig.getValue() == Sequential.NORMAL)
                        {
                            tickCalc();
                            if (placeData != null && evaluate(placeData))
                            {
                                placing = true;
                                if (rotateConfig.getValue()
                                        && checkFacing(placeData.getBoundingBox()))
                                {
                                    if (strictAnglesConfig.getValue() != Rotate.OFF
                                            || !ignoreExpectedTickConfig.getValue())
                                    {
                                        lastPlace.setElapsedTime(Timer.MAX_TIME);
                                        return;
                                    }
                                    semi = false;
                                    float[] dest = placeData.getRotationsTo(Managers.POSITION.getEyePos());
                                    rotating = setRotation(dest, strictAnglesConfig.getValue() == Rotate.FULL);
                                    rotateTimer.reset();
                                }
                                if (place(placeData))
                                {
                                    lastPlace.reset();
                                }
                                placeData = null;
                                pauseCalcPlace = false;
                            }
                        }
                    }
                    for (Entity e : mc.world.getEntities())
                    {
                        if (e != null && e.isAlive() && e instanceof EndCrystalEntity)
                        {
                            if (e.squaredDistanceTo(packet.getX(),
                                    packet.getY(), packet.getZ()) < 144.0)
                            {
                                Long breakTime = attacks.remove(e.getId());
                                if (breakTime != null)
                                {
                                    e.kill();
                                    breakTimes.push(System.currentTimeMillis() - breakTime);
                                }
                            }
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof BlockUpdateS2CPacket packet)
            {
                BlockPos pos = packet.getPos();
                BlockState state = packet.getState();
                BlockPos mine = Modules.SPEEDMINE.getBlockTarget();
                if (mine != null && mine.equals(pos) && state.isAir())
                {
                    for (Entity e : mc.world.getEntities())
                    {
                        if (e != null && e.isAlive()
                                && e instanceof EndCrystalEntity c)
                        {
                            if (postCheckBreakRange(c.getPos()))
                            {
                                continue;
                            }
                            if (mine.toCenterPos().distanceTo(c.getPos()) < 12.0)
                            {
                                if (rotateConfig.getValue()
                                        && checkFacing(c.getBoundingBox()))
                                {
                                    semi = true;
                                    float[] dest = RotationUtil.getRotationsTo(
                                            Managers.POSITION.getEyePos(),
                                            c.getPos());
                                    rotating = setRotation(dest, strictAnglesConfig.getValue() != Rotate.OFF);
                                    rotateTimer.reset();
                                    if (strictAnglesConfig.getValue() != Rotate.OFF
                                            || attackDelayConfig.getValue() > 0.0f
                                            || !ignoreExpectedTickConfig.getValue())
                                    {
                                        setAttackHard(new DamageData<>(null,
                                                c, -1.0, -1.0));
                                        return;
                                    }
                                    attacking = true;
                                }
                                if (attack(c.getId()))
                                {
                                    lastBreak.reset();
                                    attacks.put(c.getId(),
                                            System.currentTimeMillis());
                                }
                                break;
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
     * @param pdata
     * @param id
     * @return
     */
    private DamageData<EndCrystalEntity> getSrcData(final DamageData<BlockPos> pdata,
                                                    final int id)
    {
        EndCrystalEntity deepcopy = new EndCrystalEntity(mc.world,
                pdata.getX(), pdata.getY(), pdata.getZ());
        deepcopy.setId(id);
        return new DamageData<>(pdata.getTarget(), deepcopy,
                pdata.getDamage(), pdata.getLocal());
    }

    /**
     *
     *
     * @param data
     * @return
     */
    public boolean attack(DamageData<EndCrystalEntity> data)
    {
        return attack(data.getSrc().getId());
    }

    /**
     *
     *
     * @param e
     * @return
     * 
     * @see #attackDirect(int) 
     * @see #preAttackCheck(int) 
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
                    boolean swapped = false;
                    if (preSwapCheck())
                    {
                        swapped = swapConfig.getValue() != Swap.SILENT_ALT ?
                            swap(slot) : swapAlt(slot + 36);
                    }
                    if (swapped)
                    {
                        attackDirect(e);
                        if (antiWeaknessConfig.getValue() == Swap.SILENT)
                        {
                            swap(prev);
                        }
                        else if (antiWeaknessConfig.getValue() == Swap.SILENT_ALT)
                        {
                            swapAlt(prev + 36);
                            lastSwapAlt.reset();
                        }
                    }
                }
                return false;
            }
            attackDirect(e);
            attacks.put(e, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    /**
     * 
     * 
     * @param e
     *
     * @see PlayerInteractEntityC2SPacket
     */
    private void attackDirect(int e) 
    {
        // retarded hack to set entity id
        PlayerInteractEntityC2SPacket packet =
                PlayerInteractEntityC2SPacket.attack(null,
                        mc.player.isSneaking());
        ((AccessorPlayerInteractEntityC2SPacket) packet).hookSetEntityId(e);
        Managers.NETWORK.sendPacket(packet);
        Hand hand = getCrystalHand();
        swingDirect(hand != null ? hand : Hand.MAIN_HAND);
    }

    /**
     *
     *
     * @param e
     * @return
     */
    public boolean preAttackCheck(int e)
    {
        if (((NanoTimer) freqInterval).passed(0.5f, TimeUnit.SECONDS))
        {
            freq++;
            if (freq > 15)
            {
                for (int i = 0; i < 16; i++)
                {
                    attackFreq[i] = 0;
                }
                freq = 0;
            }
        }
        if (inhibitConfig.getValue())
        {
            attackFreq[freq] = attackFreq[freq] + 1;
            int sum = 0;
            for (int i = 0; i < freq; i++)
            {
                sum += attackFreq[i];
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
            if (sum - limit > 0)
            {
                return false;
            }
            Long time = attacks.get(e);
            if (time != null)
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
                float shortTerm = (float) shortTermCount * 1000f
                        / (50.0f * inhibitTicksConfig.getValue());
                float max = Math.max(shortTerm, total);
                return max > inhibitLimitConfig.getValue();
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
     * @return <tt>true</tt> if the placement was successful
     *
     * @see #placeDirect(BlockPos, Hand, BlockHitResult)
     * @see #prePlaceCheck()
     */
    public boolean place(DamageData<BlockPos> data)
    {
        BlockPos p = data.getSrc();
        if (prePlaceCheck())
        {
            Direction dir = data.getInteractDirection();
            BlockHitResult result = new BlockHitResult(p.toCenterPos(), dir,
                    p,  false);
            Hand hand = getCrystalHand();
            if (hand != null)
            {
                placeDirect(p, hand, result);
                postSequence = null;
                preSequence = p;
                tickSequence.reset();
            }
            else if (swapConfig.getValue() != Swap.OFF)
            {
                int slot = getCrystalSlot();
                int prev = mc.player.getInventory().selectedSlot;
                if (slot != -1)
                {
                    boolean swapped = false;
                    if (preSwapCheck())
                    {
                        swapped = swapConfig.getValue() != Swap.SILENT_ALT ?
                                swap(slot) : swapAlt(slot + 36);
                    }
                    if (swapped)
                    {
                        placeDirect(p, Hand.MAIN_HAND, result);
                        postSequence = null;
                        preSequence = p;
                        tickSequence.reset();
                        if (swapConfig.getValue() == Swap.SILENT)
                        {
                            swap(prev);
                        }
                        else if (swapConfig.getValue() == Swap.SILENT_ALT)
                        {
                            swapAlt(prev + 36);
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
            lastAutoSwap.reset();
        }
        if (sequentialConfig.getValue() != Sequential.NONE)
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
        if (swapConfig.getValue() == Swap.NORMAL)
        {
            return lastAutoSwap.passed(500);
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
                || mc.player.handSwingTicks >= getHandSwingDuration() / 2
                || mc.player.handSwingTicks < 0)
        {
            mc.player.handSwinging = true;
            mc.player.handSwingTicks = -1;
            mc.player.preferredHand = hand;
        }
        Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
    }

    /**
     *
     * @return
     */
    private int getHandSwingDuration()
    {
        if (StatusEffectUtil.hasHaste(mc.player))
        {
            return 6 - (1 + StatusEffectUtil.getHasteAmplifier(mc.player));
        }
        return mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE) ?
                6 + (1 + mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
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
                if (attacks.containsKey(e.getId()))
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
     */
    private int getCrystalSlot()
    {
        int slot = -1;
        for (int i = 0; i < 9; ++i)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof EndCrystalItem)
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
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
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
            ScreenHandler screenHandler = mc.player.currentScreenHandler;
            DefaultedList<Slot> slots = screenHandler.slots;
            List<ItemStack> list = Lists.newArrayListWithCapacity(slots.size());
            for (Slot s : slots)
            {
                list.add(s.getStack().copy());
            }
            // screenHandler.onSlotClick(slotId, button, actionType, player);
            Int2ObjectMap<ItemStack> diffs =
                    new Int2ObjectOpenHashMap<>();
            for (int i = 0; i < slots.size(); ++i)
            {
                ItemStack i1 = list.get(i);
                ItemStack i2 = slots.get(i).getStack();
                if (!ItemStack.areEqual(i1, i2))
                {
                    diffs.put(i, i2.copy());
                }
            }
            Managers.NETWORK.sendPacket(new ClickSlotC2SPacket(0,
                    screenHandler.getRevision(), slot,
                    0, SlotActionType.SWAP,
                    screenHandler.getCursorStack().copy(), diffs));
            // lastSwapAlt.reset();
            if (swapSyncConfig.getValue())
            {
                Managers.INVENTORY.syncSelectedSlot();
            }
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
    private Vec3d toSource(BlockPos base)
    {
        return Vec3d.ofBottomCenter(base).add(0.0, 1.0, 0.0);
    }

    /**
     *
     *
     * @param e
     * @return
     */
    private BlockPos toBase(EndCrystalEntity e)
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
                    Vec3i pos = new Vec3i((int) (o.getX() + x),
                            (int) (o.getY() + y), (int) (o.getZ() + z));
                    BlockPos p = new BlockPos(pos);
                    if (canUseOnBlock(p))
                    {
                        double dist = placeRangeCenterConfig.getValue() ?
                                p.getSquaredDistanceFromCenter(o.getX(), o.getY(),
                                        o.getZ()) : p.getSquaredDistance(o);
                        if (dist <= radius * radius)
                        {
                            sphere.add(p);
                        }
                    }
                }
            }
        }
        return sphere;
    }

    /**
     *
     *
     * @param cpos
     * @return
     */
    private boolean postCheckBreakRange(final Vec3d cpos)
    {
        Vec3d pos = Managers.POSITION.getEyePos();
        double dist = pos.distanceTo(cpos);
        Vec3d motion = mc.player.getVelocity();
        double x = Managers.POSITION.getX() + motion.getX();
        double y = Managers.POSITION.getY() + motion.getY();
        double z = Managers.POSITION.getZ() + motion.getZ();
        if (dist > breakRangeConfig.getValue() * breakRangeConfig.getValue()
                && !isWithinStrictRange(new Vec3d(x, y, z), cpos,
                strictBreakRangeConfig.getValue()))
        {
            return true;
        }
        BlockHitResult result = mc.world.raycast(new RaycastContext(
                Managers.POSITION.getCameraPosVec(1.0f),
                cpos, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player));
        if (result != null && dist > breakWallRangeConfig.getValue())
        {
            return true;
        }
        return false;
    }

    /**
     *
     *
     * @param entities
     * @param crystals
     * @return
     *
     * @see #getDamage(Entity, Vec3d)
     * @see #getCrystalSphere(Vec3d, double)
     */
    private DamageData<EndCrystalEntity> getCrystal(final Iterable<Entity> entities,
                                                    final Iterable<EndCrystalEntity> crystals)
    {
        if (mc.world != null && mc.player != null)
        {
            TreeSet<DamageData<EndCrystalEntity>> min = new TreeSet<>();
            for (EndCrystalEntity c : crystals)
            {
                if (postCheckBreakRange(c.getPos()))
                {
                    continue;
                }
                if (c.age < ticksExistedConfig.getValue()
                        && !inhibitConfig.getValue())
                {
                    continue;
                }
                double local = getDamage(mc.player, c.getPos());
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
                            double pdist = Managers.POSITION.squaredDistanceTo(e);
                            if (pdist > targetRangeConfig.getValue() * targetRangeConfig.getValue())
                            {
                                continue;
                            }
                            float ehealth = 0.0f;
                            if (e instanceof LivingEntity)
                            {
                                ehealth = ((LivingEntity) e).getHealth()
                                        + ((LivingEntity) e).getAbsorptionAmount();
                            }
                            double dmg = getDamage(e, c.getPos());
                            DamageData<EndCrystalEntity> data =
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
                            if (checkArmor(e))
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
                renderBreak.set(f.getSrc().getBoundingBox());
                return f;
            }
        }
        renderBreak.set(null);
        return null;
    }

    /**
     *
     *
     * @param p
     * @return
     */
    public boolean postCheckPlaceRange(final BlockPos p)
    {
        Vec3d pos = placeRangeEyeConfig.getValue() ?
                Managers.POSITION.getEyePos() : Managers.POSITION.getPos();
        double dist = placeRangeCenterConfig.getValue() ?
                p.getSquaredDistanceFromCenter(pos.getX(), pos.getY(),
                        pos.getZ()) : p.getSquaredDistance(pos);
        Vec3d motion = mc.player.getVelocity();
        double x = Managers.POSITION.getX() + motion.getX();
        double y = Managers.POSITION.getY() + motion.getY();
        double z = Managers.POSITION.getZ() + motion.getZ();
        if (dist > placeRangeConfig.getValue() * placeRangeConfig.getValue()
                && !isWithinStrictRange(new Vec3d(x, y, z),
                toSource(p),
                strictPlaceRangeConfig.getValue()))
        {
            return true;
        }
        Vec3d expected = new Vec3d(p.getX() + 0.5,
                p.getY() + 2.70000004768372, p.getZ() + 0.5);
        BlockHitResult result = mc.world.raycast(new RaycastContext(
                Managers.POSITION.getCameraPosVec(1.0f),
                expected, RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE, mc.player));
        float maxDist = 36.0f;
        if (result != null && result.getType() == HitResult.Type.BLOCK
                && result.getBlockPos() != p)
        {
            maxDist = 9.0f;
            if (dist > placeWallRangeConfig.getValue() * placeWallRangeConfig.getValue())
            {
                return true;
            }
        }
        return breakValidateConfig.getValue() && dist > maxDist;
    }

    /**
     *
     *
     * @param entities
     * @param blocks
     * @return
     *
     * @see #getDamage(Entity, Vec3d)
     * @see #getSphere(Vec3d, double)
     */
    private DamageData<BlockPos> getPlace(final Iterable<Entity> entities,
                                          final Iterable<BlockPos> blocks)
    {
        if (mc.world != null && mc.player != null)
        {
            TreeSet<DamageData<BlockPos>> min = new TreeSet<>();
            // placement processing
            for (BlockPos p : blocks)
            {
                if (postCheckPlaceRange(p))
                {
                    continue;
                }
                BlockPos dpos = p;
                BlockPos mine = Modules.SPEEDMINE.getBlockTarget();
                if (minePlaceConfig.getValue() && mine != null
                        && mine.equals(p))
                {
                    dpos = p.down();
                }
                double local = getDamage(mc.player, toSource(dpos));
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
                        double pdist = Managers.POSITION.squaredDistanceTo(e);
                        // double edist = e.squaredDistanceTo(p.toCenterPos());
                        if (pdist > targetRangeConfig.getValue()
                                * targetRangeConfig.getValue())
                        {
                            continue;
                        }
                        if (isEnemy(e))
                        {
                            double dmg = getDamage(e, toSource(dpos));
                            float ehealth = 0.0f;
                            if (e instanceof LivingEntity)
                            {
                                ehealth = ((LivingEntity) e).getHealth()
                                        + ((LivingEntity) e).getAbsorptionAmount();
                            }
                            DamageData<BlockPos> data =
                                    new DamageData<>(e, p, dmg, local);
                            if (minePlaceConfig.getValue() &&
                                    mine != null && mine.equals(p))
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
                            if (checkArmor(e))
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
            double lethal = lethalMultiplier.getValue() * damage;
            return health - lethal > 0.5;
        }
        return false;
    }

    /**
     *
     *
     * @param e
     * @return
     */
    private boolean checkArmor(final Entity e)
    {
        if (e instanceof LivingEntity)
        {
            float earmor = 100.0f;
            if (armorScaleConfig.getValue() != 0.0f)
            {
                float dmg = 0.0f;
                float t = 0.0f;
                for (ItemStack a : e.getArmorItems())
                {
                    dmg += a.getDamage();
                    t += a.getMaxDamage();
                }
                earmor = dmg / t;
            }
            return earmor < armorScaleConfig.getValue();
        }
        return false;
    }

    /**
     *
     *
     * @param e
     * @param phealth
     * @param damage
     * @return
     */
    private boolean checkAntiTotem(final Entity e,
                                   final float phealth,
                                   final double damage)
    {
        if (antiTotemConfig.getValue() && e instanceof PlayerEntity p)
        {
            if (phealth <= 2.0f && phealth - damage < 0.5f)
            {
                Long time = pops.get(p);
                if (time != null)
                {
                    return System.currentTimeMillis() - time <= 500;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    private boolean isWithinStrictRange(final Vec3d src, final Vec3d dest,
                                        final double range)
    {
        // directly from NCP src
        double y1 = src.getY() + Managers.POSITION.getActiveEyeHeight();
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
     * @return
     */
    private boolean checkFacing(final Box dest)
    {
        float yaw = Managers.ROTATION.getYaw();
        float pitch = Managers.ROTATION.getPitch();
        Vec3d pos = Managers.POSITION.getCameraPosVec(1.0f);
        Vec3d rot = Vec3d.fromPolar(pitch, yaw);
        final float maxReach = 6.0f;
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
            float diff = dest[0] - Managers.ROTATION.getYaw(); // yaw diff
            float magnitude = Math.abs(diff);
            if (magnitude > 180.0f)
            {
                diff += diff > 0.0f ? -360.0f : 360.0f;
            }
            int dir = diff > 0.0f ? 1 : -1;
            tick = yawTicksConfig.getValue();
            // partition yaw
            float deltaYaw = magnitude / tick;
            if (deltaYaw > angleThresholdConfig.getValue())
            {
                tick = MathHelper.ceil(magnitude / angleThresholdConfig.getValue());
                deltaYaw = magnitude / tick;
            }
            deltaYaw *= dir;
            int yawCount = tick;
            tick += rotateSuspendConfig.getValue();
            yaws = new float[tick];
            int off = tick - 1;
            float yawT = 0.0f;
            for (int i = 0; i < tick; ++i)
            {
                if (i > yawCount)
                {
                    yaws[off - i] = 0.0f;
                    continue;
                }
                yawT += deltaYaw;
                yaws[off - i] = yawT;
            }
            pitch = dest[1];
        }
        else
        {
            tick = rotateSuspendConfig.getValue() + 1;
            yaws = new float[tick];
            int off = tick - 1;
            yaws[off] = dest[0];
            for (int i = 1; i < tick; ++i)
            {
                yaws[off - i] = 0.0f;
            }
            pitch = dest[1];
        }
        return yaws.length;
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
         * @return
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
         *
         *
         * @param entities
         * @param crystals
         * @param blocks
         * @param time
         * @return
         */
        public synchronized void scheduleCalc(final Iterable<Entity> entities,
                                              final Iterable<EndCrystalEntity> crystals,
                                              final Iterable<BlockPos> blocks,
                                              final Number time)
        {
            calcCount++;
            calc = new TickCalculation(this);
            calc.runDelayedCalc(entities, crystals, blocks, time);
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
         */
        public void submitCalc(Callable<DamageData<?>> calcCallable)
        {
            service.submit(calcCallable);
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
        public void shutdownNow()
        {
            try
            {
                pool.shutdown();
                boolean timeout = !pool.awaitTermination(50,
                        TimeUnit.MILLISECONDS);
                if (timeout)
                {
                    Caspian.error("Process timed out! Shutting down pool! " +
                            "Completed %d tasks", getCompletedCalcCount());
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
         * @see #getCrystal(Iterable, Iterable)
         * @see #getPlace(Iterable, Iterable)
         */
        public void runCalc(final Iterable<Entity> entities,
                            final Iterable<EndCrystalEntity> crystals,
                            final Iterable<BlockPos> blocks)
        {
            start = System.currentTimeMillis();
            if (multithreadConfig.getValue())
            {
                processor.submitCalc(() -> getCrystal(entities, crystals));
                if (placeConfig.getValue())
                {
                    processor.submitCalc(() -> getPlace(entities, blocks));
                }
                return;
            }
            attackCalc = getCrystal(entities, crystals);
            if (placeConfig.getValue())
            {
                placeCalc = getPlace(entities, blocks);
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
         * @see #getCrystal(Iterable, Iterable)
         * @see #getPlace(Iterable, Iterable)
         */
        public void runDelayedCalc(final Iterable<Entity> entities,
                                   final Iterable<EndCrystalEntity> crystals,
                                   final Iterable<BlockPos> blocks,
                                   final Number delay)
        {
            start = System.currentTimeMillis();
            if (multithreadConfig.getValue())
            {
                processor.submitCalc(() ->
                {
                    try
                    {
                        Thread.sleep(delay.longValue() * 1000L);
                    }
                    catch (InterruptedException e)
                    {
                        Caspian.error("Thread interrupted for calc %s", id);
                        Thread.currentThread().interrupt();
                    }
                    return getCrystal(entities, crystals);
                });
                if (placeConfig.getValue())
                {
                    processor.submitCalc(() -> getPlace(entities, blocks));
                }
                return;
            }
            // TODO: calc delays for main threading
            attackCalc = getCrystal(entities, crystals);
            if (placeConfig.getValue())
            {
                placeCalc = getPlace(entities, blocks);
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
                boolean foundAttack = false, foundPlace = false;
                for (int i = 0; i < 2; ++i)
                {
                    Future<DamageData<?>> result = processor.takeCalc();
                    if (result != null)
                    {
                        DamageData<?> data = result.get();
                        if (data == null)
                        {
                            Caspian.error("Failed to get data for calc %s!",
                                    getId());
                            return;
                        }
                        if (data.isAttackDamage())
                        {
                            foundAttack = true;
                            attackCalc = (DamageData<EndCrystalEntity>) data.getSrc();
                        }
                        else if (data.isPlaceDamage())
                        {
                            foundPlace = true;
                            placeCalc = (DamageData<BlockPos>) data.getSrc();
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
        public long getCalcTime()
        {
            if (isDone())
            {
                return done - start;
            }
            return System.currentTimeMillis() - start;
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
        private final Position pos, rotpos;
        private Direction dir;
        private final double damage;
        private final double local;
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
            // this might not be a necessary calc since it would be more
            // efficient to calculate during placements; might remove in future
            this.dir = null;
            if (offsetFacingConfig.getValue())
            {
                dir = getInteractDirection();
            }
            this.tags = new HashSet<>(10);
            Position pos = null;
            Box box = null;
            if (src instanceof EndCrystalEntity crystal)
            {
                tags.add("attackdmg");
                pos = crystal.getPos();
                box = crystal.getBoundingBox();
            }
            else if (src instanceof BlockPos cpos)
            {
                tags.add("placedmg");
                pos = new Vec3d(cpos.getX(), cpos.getY(), cpos.getZ());
                box = new Box(cpos);
            }
            this.pos = pos;
            this.boundingBox = box;
            Position rotp = pos;
            if (randomVectorConfig.getValue() < 1.0f)
            {
                if (src instanceof EndCrystalEntity crystal)
                {
                    rotp = getRandomVec(crystal.getBlockPos(), boundingBox,
                            randomVectorConfig.getValue());
                }
                else if (src instanceof BlockPos cpos)
                {
                    Box interactBox = boundingBox;
                    if (dir != null && offsetFacingConfig.getValue())
                    {
                        Vec3d hitVec = getHitVec(cpos, dir, false);
                        interactBox = new Box(hitVec.getX(), hitVec.getY(),
                                hitVec.getZ(), box.maxX, box.maxY, box.maxZ);
                    }
                    rotp = getRandomVec(cpos, interactBox,
                            randomVectorConfig.getValue());
                }
            }
            else if (src instanceof BlockPos cpos)
            {
                rotp = dir != null && offsetFacingConfig.getValue() ?
                        getHitVec(cpos, dir, true) : cpos.toCenterPos();
            }
            this.rotpos = rotp;
        }

        /**
         *
         * @return
         */
        public Direction getInteractDirection()
        {
            if (dir != null)
            {
                return dir;
            }
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
                    BlockPos blockPos = Managers.POSITION.getBlockPos();
                    int x = blockPos.getX();
                    int y = (int) Math.floor(Managers.POSITION.getY()
                            + Managers.POSITION.getActiveEyeHeight());
                    int z = blockPos.getZ();
                    if (x != dx && y != dy && z != dz)
                    {
                        Set<Direction> dirs = DirectionChecks.getInteractableDirections(
                                x, y, z, dx, dy, dz);
                        if (!dirs.isEmpty())
                        {
                            dir = dirs.stream()
                                    .min(Comparator.comparing(d -> eye.distanceTo(getHitVec(p, d, true))))
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
                        if (p.getY() > mc.world.getHeight())
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
         * @param center
         * @return
         */
        public Vec3d getHitVec(final BlockPos floor,
                               final Direction hit,
                               final boolean center)
        {
            if (center)
            {
                return new Vec3d(hit.getOffsetX() * 0.5 + 0.5 + floor.getX(),
                        hit.getOffsetY() * 0.5 + 0.5 + floor.getY(),
                        hit.getOffsetZ() * 0.5 + 0.5 + floor.getZ());
            }
            return new Vec3d(floor.getX() + hit.getOffsetX(),
                    floor.getY() + hit.getOffsetY(),
                    floor.getZ() + hit.getOffsetZ());
        }

        /**
         *
         *
         * @param floor
         * @param bb
         * @param factor
         * @return
         */
        private Vec3d getRandomVec(final BlockPos floor,
                                   final Box bb,
                                   final float factor)
        {
            ArrayList<Vec3d> vecs = new ArrayList<>();
            Vec3d pos = new Vec3d(floor.getX(), floor.getY(), floor.getZ());
            for (double i = bb.minX; i < bb.maxX; i += factor)
            {
                for (double j = bb.minY; i < bb.maxY; i += factor)
                {
                    for (double k = bb.minZ; i < bb.maxZ; i += factor)
                    {
                        vecs.add(pos.add(i, j, k));
                    }
                }
            }
            final int randIdx = random.nextInt(vecs.size());
            return vecs.get(randIdx);
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
        public int compareTo(DamageData<?> other)
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
                return isMineDamage() ? Double.compare(getDamage(), other.getDamage()) : 1;
            }
            if (isMineDamage())
            {
                return -1;
            }
            if (Math.abs(d) > 0.2)
            {
                return Double.compare(getDamage(), other.getDamage());
            }
            double l = getLocal() - other.getLocal();
            if (Math.abs(l) > 0.2)
            {
                return Double.compare(getLocal(), other.getLocal());
            }
            return Double.compare(squaredDistanceFrom(getTargetEyePos()),
                    other.squaredDistanceFrom(other.getTargetEyePos()));
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
         * Returns <tt>true</tt> if the two {@link DamageData} damage sources are
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
        public float[] getRotationsTo(Vec3d src)
        {
            Vec3d rpos = getRotPos();
            if (rpos != null)
            {
                return RotationUtil.getRotationsTo(src, rpos);
            }
            return null;
        }

        /**
         * Returns the difference in yaw between this data' damage source
         * {@link #getRotationsTo(Vec3d)} and the specified data's damage
         * source rotations. Note that this value can be <b>negative</b>.
         *
         * @param other The data to be compared
         * @return The difference in yaw between the two damage sources
         *
         * @see #getRotationsTo(Vec3d)
         */
        public float getYawDiff(DamageData<?> other)
        {
            Vec3d eye = Managers.POSITION.getEyePos();
            float[] rots1 = getRotationsTo(eye);
            float[] rots2 = other.getRotationsTo(eye);
            if (rots1 != null && rots2 != null)
            {
                return rots1[0] - rots2[0];
            }
            return 0.0f;
        }

        /**
         *
         *
         * @return
         */
        public Vec3d getRotPos()
        {
            return (Vec3d) rotpos;
        }

        /**
         *
         *
         * @return
         */
        public Vec3d getPos()
        {
            return (Vec3d) pos;
        }

        /**
         *
         *
         * @return
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
         *
         *
         * @param src
         * @return
         */
        public double squaredDistanceFrom(Vec3d src)
        {
            return src.squaredDistanceTo(getPos());
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

        public boolean isAttackDamage()
        {
            return tags.contains("attackdmg");
        }

        /**
         *
         *
         * @return
         */
        public boolean isPlaceDamage()
        {
            return tags.contains("placedmg");
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