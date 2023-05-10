package com.caspian.impl.module.combat;

import com.caspian.Caspian;
import com.caspian.api.config.Config;
import com.caspian.api.config.setting.BooleanConfig;
import com.caspian.api.config.setting.EnumConfig;
import com.caspian.api.config.setting.NumberConfig;
import com.caspian.api.config.setting.NumberDisplay;
import com.caspian.api.event.EventStage;
import com.caspian.api.event.listener.EventListener;
import com.caspian.api.module.ModuleCategory;
import com.caspian.api.module.ToggleModule;
import com.caspian.asm.accessor.AccessorPlayerInteractEntityC2SPacket;
import com.caspian.impl.event.TickEvent;
import com.caspian.impl.event.network.MovementPacketsEvent;
import com.caspian.impl.event.network.PacketEvent;
import com.caspian.init.Managers;
import com.caspian.util.player.RotationUtil;
import com.caspian.util.time.Timer;
import com.caspian.util.world.EntityUtil;
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
import net.minecraft.item.ArmorItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

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
    final Config<Boolean> multitaskConfig = new BooleanConfig("Multitask", "",
            false);
    final Config<Float> targetRangeConfig = new NumberConfig<>("EnemyRange",
            "Range to search for potential enemies", 1.0f, 6.0f, 10.0f);
    final Config<Boolean> awaitConfig = new BooleanConfig("Await", "",
            false);
    // final Config<Boolean> raytraceConfig = new BooleanConfig("Raytrace", "",
    //        false);
    final Config<Sequential> sequentialConfig = new EnumConfig<>("Sequential",
            "", Sequential.NORMAL, Sequential.values());

    // ENEMY SETTINGS
    final Config<Boolean> playersConfig = new BooleanConfig("Players", "",
            true);
    final Config<Boolean> monstersConfig = new BooleanConfig("Monsters", "",
            false);
    final Config<Boolean> neutralsConfig = new BooleanConfig("Neutrals", "",
            false);
    final Config<Boolean> animalsConfig = new BooleanConfig("Animals", "",
            false);

    // PLACE SETTINGS
    final Config<Float> breakSpeedConfig = new NumberConfig<>("BreakSpeed",
            "Speed to break crystals", 1.0f, 20.0f, 20.0f);
    final Config<Integer> ticksExistedConfig = new NumberConfig<>("TicksExisted",
            "Minimum ticks alive to consider crystals for attack", 0, 0, 10);
    final Config<Float> breakRangeConfig = new NumberConfig<>("BreakRange",
            "Range to break crystals", 0.1f, 4.5f, 5.0f);
    final Config<Float> breakWallRangeConfig = new NumberConfig<>(
            "BreakWallRange", "Range to break crystals through walls", 0.1f,
            4.5f, 5.0f);
    final Config<Swap> antiWeaknessConfig = new EnumConfig<>("AntiWeakness",
            "Swap to tools before attacking crystals", Swap.OFF,
            Swap.values());
    final Config<Boolean> inhibitConfig = new BooleanConfig("Inhibit", "",
            true);
    final Config<Placements> placementsConfig = new EnumConfig<>("Placements",
            "", Placements.NATIVE, Placements.values());

    // PLACE SETTINGS
    final Config<Float> placeSpeedConfig = new NumberConfig<>("PlaceSpeed",
            "Speed to place crystals", 1.0f, 20.0f, 20.0f);
    final Config<Float> placeRangeConfig = new NumberConfig<>("PlaceRange",
            "Range to place crystals", 0.1f, 4.5f, 5.0f);
    final Config<Float> placeWallRangeConfig = new NumberConfig<>(
            "PlaceWallRange", "Range to place crystals through walls", 0.1f,
            4.0f, 5.0f);
    final Config<Boolean> placeRangeEyeConfig = new BooleanConfig(
            "PlaceRangeEye", "", false);
    final Config<Boolean> placeRangeCenterConfig = new BooleanConfig(
            "PlaceRangeCenter", "", true);
    final Config<Boolean> strictDirectionConfig = new BooleanConfig(
            "StrictDirection", "", false);

    // DAMAGE SETTINGS
    final Config<Float> minDamageConfig = new NumberConfig<>("MinDamage",
            "", 1.0f, 4.0f, 10.0f);
    final Config<Float> armorScaleConfig = new NumberConfig<>("ArmorScale",
            "", 0.0f, 5.0f, 20.0f, NumberDisplay.PERCENT);
    final Config<Float> lethalMultiplier = new NumberConfig<>(
            "LethalMultiplier", "", 0.0f, 0.5f, 4.0f);
    final Config<Boolean> safetyConfig = new BooleanConfig("Safety",  "",
            true);
    final Config<Float> safetyBalanceConfig = new NumberConfig<>(
            "SafetyBalance", "", 1.0f, 3.0f, 5.0f);
    final Config<Float> maxLocalDamageConfig = new NumberConfig<>(
            "MaxLocalDamage", "", 4.0f, 12.0f, 20.0f);



    //
    private DamageData<BlockPos> place;
    private DamageData<EndCrystalEntity> attack;

    // AutoCrystal dedicated thread service. Takes in the
    private int calls;
    final ExecutorCompletionService<DamageData<?>> service =
            new ExecutorCompletionService<>(executor);
    // Calculated placements and attacks will be added to their respective
    // stacks. When the main loop requires a placement/attack, simply pop the
    // last calculated from the stack.
    private final Deque<DamageData<BlockPos>> placementStack =
            new ArrayDeque<>();
    private final Deque<DamageData<EndCrystalEntity>> attackStack =
            new ArrayDeque<>();

    // Set of attempted placements and attacks
    private final Set<BlockPos> placements =
            Collections.synchronizedSet(new HashSet<>());
    private final Set<Integer> attacks =
            Collections.synchronizedSet(new HashSet<>());

    //
    private final Timer lastPlace = new Timer();
    private final Timer lastBreak = new Timer();

    // ROTATIONS
    //
    private Vec3d facing;

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
        calls = 0;
        attack = null;
        place = null;
        facing = null;
        lastBreak.reset();
        lastPlace.reset();
        attacks.clear();
        placements.clear();
        attackStack.clear();
        placementStack.clear();
        submit();
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
                // MAIN LOOP
                if (!placementStack.isEmpty())
                {
                    place = placementStack.pop();
                    try
                    {
                        while (isInvalidData(place))
                        {
                            place = placementStack.pop();
                        }
                    }
                    catch (NoSuchElementException e)
                    {
                        Caspian.error("No valid placements!");
                        // e.printStackTrace();
                        place = null;
                    }
                }
                if (!attackStack.isEmpty())
                {
                    attack = attackStack.pop();
                    try
                    {
                        while (isInvalidData(attack))
                        {
                            attack = attackStack.pop();
                        }
                    }
                    catch (NoSuchElementException e)
                    {
                        Caspian.error("No valid attacks!");
                        // e.printStackTrace();
                        attack = null;
                    }
                }
                if (attack != null)
                {
                    long delay = (long) ((((NumberConfig<Float>) breakSpeedConfig).getMax()
                            - breakSpeedConfig.getValue()) * 50);
                    if (lastBreak.passed(delay))
                    {
                        facing = attack.src().getEyePos();
                        setRotation(facing, () ->
                        {
                            if (attack(attack.src()))
                            {
                                lastBreak.reset();
                            }
                        });
                    }
                }
                if (place != null)
                {
                    long delay = (long) ((((NumberConfig<Float>) placeSpeedConfig).getMax()
                            - placeSpeedConfig.getValue()) * 50);
                    if (lastPlace.passed(delay))
                    {
                        facing = place.src().toCenterPos();
                        setRotation(facing, () ->
                        {
                            if (place(place.src()))
                            {
                                lastPlace.reset();
                            }
                        });
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
    public void onTickEvent(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            try
            {
                calc();
            }
            catch (InterruptedException | ExecutionException e)
            {
                Caspian.error("Failed calculation!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns <tt>true</tt> if the {@link DamageData} is no longer valid.
     * There are few reasons why data can be invalid, for example:
     * <p><ul>
     * <li> If the target no longer exists
     * <li> If the damage source no longer exists
     * </ul></p>
     *
     * @param d The data
     * @return Returns <tt>true</tt> if the {@link DamageData} is no longer
     * valid.
     */
    private boolean isInvalidData(DamageData<?> d)
    {
        Entity damaged = d.damaged();
        if (damaged != null && damaged.isAlive())
        {
            // if (d.src() instanceof BlockPos src)
            // {

            // }
            if (d.src() instanceof EndCrystalEntity src)
            {
                return !src.isAlive();
            }
        }
        return true;
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
    private void calc() throws InterruptedException, ExecutionException
    {
        int i;
        for (i = 0; i < calls; i++)
        {
            Future<DamageData<?>> result = service.take();
            if (result != null && result.get() != null)
            {
                DamageData<?> data = result.get();
                if (data.src() instanceof BlockPos)
                {
                    placementStack.add((DamageData<BlockPos>) data);
                }
                else if (data.src() instanceof EndCrystalEntity)
                {
                    attackStack.add((DamageData<EndCrystalEntity>) data);
                }
            }
        }
        calls -= i;
        submit();
    }

    /**
     *
     *
     * @see ExecutorCompletionService
     */
    private void submit()
    {
        service.submit(() -> getCrystal(getCrystalSphere(mc.player.getEyePos(),
                        breakRangeConfig.getValue() + 0.5)));
        service.submit(() -> getPlace(getSphere(placeRangeEyeConfig.getValue() ?
                                mc.player.getEyePos() : mc.player.getPos(),
                        placeRangeConfig.getValue() + 0.5)));
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
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.world != null && mc.player != null)
        {
            if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet)
            {

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
                            setRotation(facing, () ->
                            {
                                if (attack(packet.getId()))
                                {
                                    // Caspian.info("Attacked spawned crystal")
                                    lastBreak.reset();
                                }
                            });
                        }
                    }
                }
            }
            else if (event.getPacket() instanceof ExplosionS2CPacket packet)
            {
                for (Entity e : mc.world.getEntities())
                {
                    if (e instanceof EndCrystalEntity)
                    {
                        if (e.squaredDistanceTo(packet.getX(),
                                packet.getY(), packet.getZ()) < packet.getRadius() * packet.getRadius())
                        {
                            attacks.remove(e.getId());
                            e.kill();
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
                        if (e instanceof EndCrystalEntity)
                        {
                            if (e.squaredDistanceTo(packet.getX(),
                                    packet.getY(), packet.getZ()) < 144.0)
                            {
                                attacks.remove(e.getId());
                                e.kill();
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
     * @return
     */
    private DamageData<EndCrystalEntity> getCrystal(Iterable<EndCrystalEntity> src)
    {
        if (mc.world != null && mc.player != null)
        {
            TreeMap<Double, DamageData<EndCrystalEntity>> min = new TreeMap<>();
            for (Entity c : src)
            {
                if (c instanceof EndCrystalEntity)
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
                        }
                        if (isEnemy(e))
                        {
                            double pdist = mc.player.distanceTo(e);
                            if (pdist > targetRangeConfig.getValue())
                            {
                                continue;
                            }
                            double target = getDamage(e, c.getPos());
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
                            double lethal = lethalMultiplier.getValue() * target;
                            min.put(target, new DamageData<>(lethal + 0.5 > ehealth ||
                                    earmor < armorScaleConfig.getValue() ? 999.0 : target,
                                    local, e, (EndCrystalEntity) c));
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
                BlockHitResult result = mc.world.raycast(new RaycastContext(
                        mc.player.getEyePos(), new Vec3d(p.getX() + 0.5,
                        p.getY() + 2.70000004768372, p.getZ() + 0.5),
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE, mc.player));
                if (result != null && dist > placeWallRangeConfig.getValue())
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
                        if (isEnemy(e))
                        {
                            double pdist = mc.player.distanceTo(e);
                            // double edist = e.squaredDistanceTo(p.toCenterPos());
                            if (pdist > targetRangeConfig.getValue())
                            {
                                continue;
                            }
                            double target = getDamage(e, toSource(p));
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
                            double lethal = lethalMultiplier.getValue() * target;
                            min.put(target, new DamageData<>(lethal + 0.5 > ehealth ||
                                            earmor <armorScaleConfig.getValue() ? 999.0 : target,
                                            local, e, p));
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

    public List<EndCrystalEntity> getCrystalSphere(Vec3d o, double radius)
    {
        return null;
    }

    /**
     *
     *
     * @param o
     * @param radius
     * @return
     */
    public List<BlockPos> getSphere(Vec3d o, double radius)
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
     * @return
     */
    public boolean attack(EndCrystalEntity e)
    {
        if (antiWeaknessConfig.getValue() != Swap.OFF)
        {
            StatusEffectInstance weakness =
                    mc.player.getStatusEffect(StatusEffects.WEAKNESS);
            StatusEffectInstance strength =
                    mc.player.getStatusEffect(StatusEffects.STRENGTH);
            if (weakness != null && (strength == null
                    || weakness.getAmplifier() > strength.getAmplifier()))
            {
                // swap();
            }
        }
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
        if (multitaskConfig.getValue())
        {
            if (mc.player.isHolding(ItemStack::isFood) && mc.player.isUsingItem()
                    && getCrystalHand() != Hand.OFF_HAND)
            {
                return false;
            }
        }
        PlayerInteractEntityC2SPacket packet =
                PlayerInteractEntityC2SPacket.attack(mc.player, mc.player.isSneaking());
        ((AccessorPlayerInteractEntityC2SPacket) packet).hookSetEntityId(e);
        Managers.NETWORK.sendPacket(packet);
        Managers.NETWORK.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        attacks.add(e);
        return true;
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
                Managers.NETWORK.sendSequencedPacket(id ->
                        new PlayerInteractBlockC2SPacket(hand, result, id));
                Managers.NETWORK.sendPacket(new HandSwingC2SPacket(hand));
                placements.add(p);
            }
            return true; // success
        }
        return false;
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
                        d + 1.0, e + 2.0, f + 1.0));
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
     * @param stack
     */
    public void swap(ItemStack stack, Swap swap)
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
     * @param e
     * @param src
     * @return
     */
    public double getDamage(Entity e, Vec3d src)
    {
        return 0.0;
    }

    /**
     *
     *
     * @param to
     */
    private void setRotation(Vec3d to, Runnable callback)
    {
        float[] rots = RotationUtil.getRotationsTo(mc.player.getEyePos(), to);
        float diff = rots[0] - mc.player.getYaw(); // yaw diff
        if (Math.abs(diff) > 180)
        {
            diff += diff > 0 ? -360 : 360;
        }
        int dir = diff > 0 ? 1 : -1;


        // TODO: callbacks ...
        callback.run();
    }

    public enum Swap
    {
        NORMAL,
        SILENT,
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
    public record DamageData<T>(double target, double local,
                                Entity damaged, T src)
    {

    }
}