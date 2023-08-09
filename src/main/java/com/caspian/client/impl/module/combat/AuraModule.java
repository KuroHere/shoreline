package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.setting.NumberDisplay;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.handler.tick.TickSync;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.RotationModule;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.TickTimer;
import com.caspian.client.util.math.timer.Timer;
import com.caspian.client.util.player.InventoryUtil;
import com.caspian.client.util.player.PlayerUtil;
import com.caspian.client.util.player.RotationUtil;
import com.caspian.client.util.string.EnumFormatter;
import com.caspian.client.util.world.EntityUtil;
import com.caspian.client.util.world.FakePlayerEntity;
import com.caspian.client.util.world.VecUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.security.SecureRandom;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AuraModule extends RotationModule
{
    Config<Boolean> swingConfig = new BooleanConfig("Swing", "Swings the " +
            "hand after attacking", true);
    // RANGES
    Config<TargetMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "targeting entities to attack", TargetMode.SWITCH,
            TargetMode.values());
    Config<Priority> priorityConfig = new EnumConfig<>("Priority", "The " +
            "heuristic to prioritize when searching for targets",
            Priority.HEALTH, Priority.values());
    Config<Float> rangeConfig = new NumberConfig<>("Range", "Range to attack " +
            "entities", 1.0f, 4.5f, 5.0f);
    Config<Float> wallRangeConfig = new NumberConfig<>("WallRange", "Range to" +
            " attack entities through walls", 1.0f, 4.5f, 5.0f);
    Config<Float> fovConfig = new NumberConfig<>("FOV", "Field of view to " +
            "attack entities", 1.0f, 180.0f, 180.0f);
    Config<Boolean> latencyPositionConfig = new BooleanConfig(
            "LatencyPosition", "Targets the latency positions of enemies", false);
    Config<Integer> maxLatencyConfig = new NumberConfig<>("MaxLatency",
            "Maximum latency factor when calculating positions", 50, 250,
            1000, () -> latencyPositionConfig.getValue());
    //
    Config<Boolean> attackDelayConfig = new BooleanConfig("AttackDelay",
            "Delays attacks according to minecraft hit delays for maximum " +
                    "damage per attack", false);
    Config<Float> attackSpeedConfig = new NumberConfig<>("AttackSpeed",
            "Delay for attacks (Only functions if AttackDelay is off)", 1.0f,
            20.0f, 20.0f, () -> !attackDelayConfig.getValue());
    Config<Float> randomSpeedConfig = new NumberConfig<>("RandomSpeed",
            "Randomized delay for attacks (Only functions if AttackDelay is " +
                    "off)", 0.0f, 0.0f, 10.0f,
            () -> !attackDelayConfig.getValue());
    Config<Integer> packetsConfig = new NumberConfig<>("Packets", "Maximum " +
            "attack packets to send in a single tick", 0, 1, 20);
    Config<Float> swapDelayConfig = new NumberConfig<>("SwapPenalty", "Delay " +
            "for attacking after swapping items which prevents NCP flags", 0.0f,
            0.0f, 10.0f);
    Config<TickSync> tpsSyncConfig = new EnumConfig<>("TPS-Sync", "Syncs the " +
            "attacks with the server TPS", TickSync.NONE, TickSync.values());
    Config<Boolean> autoSwapConfig = new BooleanConfig("AutoSwap",
            "Automatically swaps to a weapon before attacking", true);
    Config<Boolean> swordCheckConfig = new BooleanConfig("Sword-Check",
            "Checks if a weapon is in the hand before attacking", true);
    // ROTATE
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotate" +
            "before attacking", false);
    Config<Vector> hitVectorConfig = new EnumConfig<>("HitVector", "The " +
            "vector to aim for when attacking entities", Vector.FEET,
            Vector.values());
    Config<Boolean> strictRotateConfig = new BooleanConfig("RotateStrict",
            "Rotates yaw over multiple ticks to prevent certain rotation  " +
                    "flags in NCP", false, () -> rotateConfig.getValue());
    Config<Integer> rotateLimitConfig = new NumberConfig<>(
            "RotateLimit", "Maximum yaw rotation in degrees for one tick",
            1, 180, 180, NumberDisplay.DEGREES,
            () -> rotateConfig.getValue() && strictRotateConfig.getValue());
    Config<Integer> yawTicksConfig = new NumberConfig<>("YawTicks",
            "Minimum ticks to rotate yaw", 1, 1, 5,
            () -> rotateConfig.getValue() && strictRotateConfig.getValue());
    Config<Integer> rotateTimeoutConfig = new NumberConfig<>(
            "RotateTimeout", "Minimum ticks to hold the rotation yaw after " +
            "reaching the rotation", 0, 0, 5, () -> rotateConfig.getValue());
    Config<Integer> ticksExistedConfig = new NumberConfig<>("TicksExisted",
            "The minimum age of the entity to be considered for attack", 0, 50, 200);
    Config<Boolean> stopSprintConfig = new BooleanConfig("StopSprint",
            "Stops sprinting before attacking to maintain vanilla behavior", false);
    Config<Boolean> stopShieldConfig = new BooleanConfig("StopShield",
            "Automatically handles shielding before attacking", false);
    //
    Config<Boolean> playersConfig = new BooleanConfig("Players",
            "Target players", true);
    Config<Boolean> monstersConfig = new BooleanConfig("Monsters",
            "Target monsters", false);
    Config<Boolean> neutralsConfig = new BooleanConfig("Neutrals",
            "Target neutrals", false);
    Config<Boolean> animalsConfig = new BooleanConfig("Animals",
            "Target animals", false);
    Config<Boolean> renderConfig = new BooleanConfig("Render",
            "Renders an indicator over the target", true);
    //
    private AuraTarget target;
    //
    private int packets;
    private final Timer attackTimer = new CacheTimer();
    private final Timer critTimer = new CacheTimer();
    //
    private final Timer autoSwapTimer = new CacheTimer();
    private final Timer switchTimer = new CacheTimer();
    //
    private boolean crit;
    private boolean sprinting;
    private boolean shielding;
    private boolean sneaking;
    // RANDOM
    private final Random random = new SecureRandom();
    private long randomTime = -1;
    // ROTATIONS
    private final Timer rotateTimer = new TickTimer();
    private int rotating;
    //
    private float[] yawLimits;
    private float yaw, pitch;

    /**
     *
     *
     */
    public AuraModule()
    {
        super("Aura", "Attacks nearby entities", ModuleCategory.COMBAT);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     *
     */
    @Override
    public void onDisable()
    {
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
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (renderConfig.getValue() && target != null
                && (!swordCheckConfig.getValue() || isHoldingSword()))
        {
            final Box box = target.getBoundingBox();
            RenderManager.renderBox(box, Modules.COLORS.getRGB(60));
            RenderManager.renderBoundingBox(box, 1.5f,
                    Modules.COLORS.getRGB(145));
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket)
        {
            if (!event.isCached())
            {
                autoSwapTimer.reset();
            }
            switchTimer.reset();
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
            if (Modules.AUTO_CRYSTAL.isAttacking()
                    || Modules.AUTO_CRYSTAL.isPlacing()
                    || Modules.AUTO_CRYSTAL.isRotating())
            {
                return;
            }
            if (isRotationBlocked())
            {
                return;
            }
            final Vec3d eyepos = Managers.POSITION.getEyePos();
            if (rotating > 0)
            {
                yaw = yawLimits[--rotating];
            }
            //
            if (!rotateTimer.passed(Modules.ROTATIONS.getPreserveTicks()))
            {
                setRotation(yaw, pitch);
                event.cancel();
            }
            if (rotating != 0)
            {
                return;
            }
            // Search delegated to main thread
            switch (modeConfig.getValue())
            {
                case SWITCH -> target = getAuraTarget(eyepos,
                        mc.world.getEntities());
                case SINGLE ->
                {
                    if (target == null || !target.isValid())
                    {
                        target = getAuraTarget(eyepos, mc.world.getEntities());
                    }
                }
            }
            if (target != null)
            {
                if (rotateConfig.getValue() && checkFacing(target.getBoundingBox()))
                {
                    float[] rots = RotationUtil.getRotationsTo(eyepos,
                            target.getHitVec());
                    rotating = setRotation(rots);
                    rotateTimer.reset();
                    return;
                }
                if (switchTimer.passed(swapDelayConfig.getValue()))
                {
                    crit = Modules.CRITICALS.isEnabled() && critTimer.passed(500);
                    float dist = mc.player.fallDistance;
                    if (attackDelayConfig.getValue())
                    {
                        float ticks = 20.0f - Managers.TICK.getTickSync(tpsSyncConfig.getValue());
                        float progress = mc.player.getAttackCooldownProgress(ticks);
                        if (progress >= 1.0f)
                        {
                            if (crit)
                            {
                                mc.player.fallDistance = 0.1f;
                                event.setOnGround(false);
                                event.cancel();
                            }
                            while (packets < packetsConfig.getValue())
                            {
                                attack(target.getEntity());
                                packets++;
                            }
                            critTimer.reset();
                            mc.player.resetLastAttackedTicks();
                            mc.player.fallDistance = dist;
                        }
                    }
                    else
                    {
                        if (randomTime < 0)
                        {
                            randomTime = random.nextLong((long)
                                    (randomSpeedConfig.getValue() * 10.0f + 1.0f));
                        }
                        float delay = attackSpeedConfig.getValue() * 50.0f + randomTime;
                        if (attackTimer.passed(delay))
                        {
                            if (crit)
                            {
                                mc.player.fallDistance = 0.1f;
                                event.setOnGround(false);
                                event.cancel();
                            }
                            while (packets < packetsConfig.getValue())
                            {
                                attack(target.getEntity());
                                packets++;
                            }
                            attackTimer.reset();
                            critTimer.reset();
                            randomTime = -1;
                            mc.player.fallDistance = dist;
                        }
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
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof EntityStatusS2CPacket packet)
            {
                if (packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING)
                {

                }
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isAttacking()
    {
        return target != null;
    }

    /**
     *
     *
     */
    public void clear()
    {
        target = null;
        rotating = 0;
        packets = 0;
        randomTime = -1;
        sprinting = false;
        shielding = false;
        sneaking = false;
        crit = false;
    }

    /**
     *
     *
     * @param entity
     * @return
     */
    private void attack(Entity entity)
    {
        if (autoSwapConfig.getValue() && !isHoldingSword())
        {
            int slot = getWeaponSlot();
            if (slot != -1 && preSwapCheck())
            {
                mc.player.getInventory().selectedSlot = slot;
                Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            }
        }
        if (swordCheckConfig.getValue() && !isHoldingSword())
        {
            return;
        }
        preAttack();
        Managers.NETWORK.sendPacket(PlayerInteractEntityC2SPacket.attack(entity,
                Managers.POSITION.isSneaking()));
        swingDirect(Hand.MAIN_HAND);
        postAttack();
    }

    /**
     *
     *
     * @return
     */
    private boolean preSwapCheck()
    {
        if (mc.options.useKey.isPressed() || mc.options.attackKey.isPressed())
        {
            autoSwapTimer.reset();
            return !autoSwapConfig.getValue();
        }
        if (autoSwapConfig.getValue())
        {
            return autoSwapTimer.passed(500);
        }
        return true;
    }

    /**
     *
     *
     * @return
     */
    public boolean isHoldingSword()
    {
        final ItemStack mainhand = mc.player.getMainHandStack();
        return mainhand.getItem() instanceof SwordItem
                || mainhand.getItem() instanceof MiningToolItem;
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

    /**
     *
     *
     */
    private void preAttack()
    {
        final ItemStack offhand = mc.player.getOffHandStack();
        // Shield state
        shielding = false;
        if (stopShieldConfig.getValue())
        {
            shielding = offhand.getItem() == Items.SHIELD
                    && mc.player.isBlocking();
            if (shielding)
            {
                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM,
                        Managers.POSITION.getBlockPos(), Direction.getFacing(mc.player.getX(),
                                mc.player.getY(), mc.player.getZ())));
            }
        }
        sneaking = false;
        sprinting = false;
        if (stopSprintConfig.getValue())
        {
            sneaking = Managers.POSITION.isSneaking();
            if (sneaking)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
            sprinting = Managers.POSITION.isSprinting();
            if (sprinting)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
        }
        if (Modules.CRITICALS.isEnabled())
        {
            if (!Managers.POSITION.isOnGround()
                    || mc.player.isRiding()
                    || mc.player.isSubmergedInWater()
                    || mc.player.isInLava()
                    || mc.player.isHoldingOntoLadder()
                    || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                    || mc.player.input.jumping)
            {
                return;
            }
            if (crit)
            {
                Modules.CRITICALS.hookPreAttack();
            }
        }
    }

    /**
     *
     *
     */
    private void postAttack()
    {
        if (shielding)
        {
            Managers.NETWORK.sendSequencedPacket(s ->
                    new PlayerInteractItemC2SPacket(Hand.OFF_HAND, s));
        }
        if (stopSprintConfig.getValue())
        {
            if (sneaking)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            }
            if (sprinting)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
        }
    }

    /**
     *
     *
     * @param pos
     * @param entities
     * @return
     */
    private AuraTarget getAuraTarget(final Vec3d pos,
                                     final Iterable<Entity> entities)
    {
        TreeSet<AuraTarget> targets = new TreeSet<>();
        for (Entity e : entities)
        {
            if (e != null && e.isAlive() && e != mc.player && !Managers.SOCIAL.isFriend(e.getUuid()))
            {
                if (e instanceof EndCrystalEntity
                        || e instanceof ItemEntity
                        || e instanceof ArrowEntity
                        || e instanceof ExperienceBottleEntity)
                {
                    continue;
                }
                if (mc.player.isRiding() && e == mc.player.getVehicle())
                {
                    continue;
                }
                if (e instanceof PlayerEntity player && player.isCreative())
                {
                    continue;
                }
                // Range check
                if (isEnemy(e))
                {
                    if (e.age < ticksExistedConfig.getValue())
                    {
                        continue;
                    }
                    Entity target = e;
                    if (e instanceof PlayerEntity player
                            && latencyPositionConfig.getValue())
                    {
                        FakePlayerEntity t = Managers.LATENCY.getTrackedPlayer(pos,
                                player, maxLatencyConfig.getValue());
                        if (t != null)
                        {
                            target = t;
                        }
                    }
                    //
                    final AuraTarget auraTarget = new AuraTarget(target);
                    if (auraTarget.isInRange(pos))
                    {
                        targets.add(auraTarget);
                    }
                }
            }
        }
        if (!targets.isEmpty())
        {
            return targets.last();
        }
        return null;
    }

    /**
     *
     *
     * @return
     */
    private int getWeaponSlot()
    {
        int sharp = -1;
        int slot = -1;
        // Maximize item attack damage
        for (int i = 0; i < 9; i++)
        {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof SwordItem
                    || stack.getItem() instanceof MiningToolItem)
            {
                int lvl = EnchantmentHelper.getLevel(Enchantments.SHARPNESS,
                        stack);
                if (lvl > sharp)
                {
                    sharp = lvl;
                    slot = i;
                }
            }
        }
        return slot;
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
     */
    private int setRotation(float[] dest)
    {
        int tick;
        if (strictRotateConfig.getValue())
        {
            float diff = dest[0] - Managers.ROTATION.getYaw(); // yaw diff
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

    public enum TargetMode
    {
        SWITCH,
        SINGLE
    }

    public enum Vector
    {
        EYES,
        TORSO,
        FEET
    }

    public enum Priority
    {
        HEALTH,
        DISTANCE,
        ARMOR
    }

    /**
     *
     *
     *
     */
    public class AuraTarget implements Comparable<Entity>
    {
        //
        private final Entity entity;

        /**
         *
         *
         * @param entity
         */
        public AuraTarget(Entity entity)
        {
            this.entity = entity;
        }

        /**
         *
         * @return
         */
        public boolean isValid()
        {
            return entity != null && entity.isAlive()
                    && isInRange(Managers.POSITION.getEyePos());
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compareTo(Entity o)
        {
            Priority prio = priorityConfig.getValue();
            if (InventoryUtil.isHolding32k())
            {
                prio = Priority.DISTANCE;
            }
            return switch (prio)
            {
                case HEALTH ->
                {
                    if (entity instanceof LivingEntity e
                            && o instanceof LivingEntity other)
                    {
                        yield Float.compare(e.getHealth() + e.getAbsorptionAmount(),
                                other.getHealth() + other.getAbsorptionAmount());
                    }
                    yield 0;
                }
                case DISTANCE ->
                {
                    Vec3d eyepos = Managers.POSITION.getEyePos();
                    yield Double.compare(eyepos.distanceTo(entity.getPos()),
                            eyepos.distanceTo(o.getPos()));
                }
                case ARMOR ->
                {
                    if (entity instanceof LivingEntity e
                            && o instanceof LivingEntity other)
                    {
                        float edmg = 0.0f;
                        float odmg = 0.0f;
                        //
                        float emax = 0.0f;
                        float omax = 0.0f;
                        for (ItemStack armor : e.getArmorItems())
                        {
                            if (armor != null && !armor.isEmpty())
                            {
                                edmg += armor.getDamage();
                                emax += armor.getMaxDamage();
                            }
                        }
                        for (ItemStack armor : other.getArmorItems())
                        {
                            if (armor != null && !armor.isEmpty())
                            {
                                odmg += armor.getDamage();
                                omax = armor.getMaxDamage();
                            }
                        }
                        float earmor = 100.0f - edmg / emax;
                        float oarmor = 100.0f - odmg / omax;
                        yield Float.compare(earmor, oarmor);
                    }
                    yield 0;
                }
            };
        }

        /**
         *
         *
         * @param pos
         * @return
         */
        public boolean isInRange(final Vec3d pos)
        {
            final Vec3d hitVec = getHitVec();
            //
            double dist = pos.distanceTo(hitVec);
            if (dist > rangeConfig.getValue())
            {
                return false;
            }
            BlockHitResult result = mc.world.raycast(new RaycastContext(
                    Managers.POSITION.getCameraPosVec(1.0f),
                    hitVec, RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE, mc.player));
            if (result != null && dist > wallRangeConfig.getValue())
            {
                return false;
            }
            if (fovConfig.getValue() != 180.0f)
            {
                float[] rots = RotationUtil.getRotationsTo(pos,
                        target.getHitVec());
                float diff = Managers.ROTATION.getWrappedYaw() - rots[0];
                float magnitude = Math.abs(diff);
                if (magnitude > fovConfig.getValue())
                {
                    return false;
                }
            }
            return true;
        }

        /**
         *
         *
         * @return
         */
        public Vec3d getHitVec()
        {
            return switch (hitVectorConfig.getValue())
                    {
                        case EYES -> VecUtil.toEyePos(entity);
                        case FEET -> entity.getPos();
                        case TORSO -> VecUtil.toTorsoPos(entity);
                    };
        }

        /**
         *
         *
         * @return
         */
        public Entity getEntity()
        {
            return entity;
        }

        /**
         *
         *
         * @return
         */
        public Box getBoundingBox()
        {
            return entity.getBoundingBox();
        }
    }
}
