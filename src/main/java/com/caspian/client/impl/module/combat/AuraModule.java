package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.setting.NumberDisplay;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.handler.tick.TickSync;
import com.caspian.client.api.module.ModuleCategory;
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
import com.caspian.client.util.player.PlayerUtil;
import com.caspian.client.util.player.RotationUtil;
import com.caspian.client.util.string.EnumFormatter;
import com.caspian.client.util.world.EntityUtil;
import com.caspian.client.util.world.VecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.security.SecureRandom;
import java.util.Random;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AuraModule extends ToggleModule
{
    Config<Boolean> swingConfig = new BooleanConfig("Swing", "Swings the " +
            "hand after attacking", true);
    // RANGES
    Config<TargetMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "targeting entities to attack", TargetMode.SWITCH,
            TargetMode.values());
    Config<Float> rangeConfig = new NumberConfig<>("Range", "Range to attack " +
            "entities", 1.0f, 4.5f, 5.0f);
    Config<Float> wallRangeConfig = new NumberConfig<>("WallRange", "Range to" +
            " attack entities through walls", 1.0f, 4.5f, 5.0f);
    //
    Config<Boolean> attackDelayConfig = new BooleanConfig("AttackDelay",
            "Delays attacks according to minecraft hit delays for maximum " +
                    "damage per attack", false);
    Config<Float> attackSpeedConfig = new NumberConfig<>("AttackSpeed",
            "Delay for attacks (Only functions if AttackDelay is off)", 1.0f,
            20.0f, 20.0f);
    Config<Float> randomSpeedConfig = new NumberConfig<>("RandomSpeed",
            "Randomized delay for attacks (Only functions if AttackDelay is " +
                    "off)", 0.0f, 0.0f, 10.0f);
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
    Config<Integer> rotateSuspendConfig = new NumberConfig<>(
            "RotateTimeout", "Minimum ticks to hold the rotation yaw after " +
            "reaching the rotation", 0, 0, 5, () -> rotateConfig.getValue());
    Config<Integer> ticksExistedConfig = new NumberConfig<>("TicksExisted",
            "The minimum age of the entity to be considered for attack", 0, 0, 200);
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
    private Entity target;
    private final Timer attackTimer = new CacheTimer();
    private final Timer autoSwapTimer = new CacheTimer();
    private final Timer switchTimer = new CacheTimer();
    //
    private boolean sprinting;
    private boolean shielding;
    // RANDOM
    private final Random random = new SecureRandom();
    private long randomTime = -1;
    // ROTATIONS
    private final Timer rotateTimer = new TickTimer();
    private int rotating;
    //
    private float yaw, pitch;
    private float[] yaws;

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
     */
    @Override
    public void onDisable()
    {
        clear();
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
        if (renderConfig.getValue() && target != null &&
                (!swordCheckConfig.getValue() || isHoldingSword()))
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
            if (rotating > 0)
            {
                yaw = yaws[--rotating];
                rotateTimer.reset();
            }
            if (!rotateTimer.passed(Modules.ROTATIONS.getPreserveTicks()))
            {
                Managers.ROTATION.setRotation(this, yaw, pitch);
                event.cancel();
            }
            if (rotating != 0)
            {
                return;
            }
            final Vec3d eyepos = Managers.POSITION.getEyePos();
            //
            target = getAuraTarget(eyepos);
            if (target != null)
            {
                if (rotateConfig.getValue() && checkFacing(target.getBoundingBox()))
                {
                    float[] rots = RotationUtil.getRotationsTo(eyepos,
                            getHitVec(target));
                    rotating = setRotation(rots);
                    rotateTimer.reset();
                    return;
                }
                if (switchTimer.passed(swapDelayConfig.getValue()))
                {
                    if (attackDelayConfig.getValue())
                    {
                        float ticks = 20.0f - getServerTicks();
                        float progress = mc.player.getAttackCooldownProgress(ticks);
                        if (progress >= 1)
                        {
                            attack(target);
                            mc.player.resetLastAttackedTicks();
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
                            attack(target);
                            attackTimer.reset();
                            randomTime = -1;
                        }
                    }
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
        randomTime = -1;
        sprinting = false;
        shielding = false;
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
        if (!swordCheckConfig.getValue() || isHoldingSword())
        {
            preAttack();
            Managers.NETWORK.sendPacket(PlayerInteractEntityC2SPacket.attack(entity,
                    Managers.POSITION.isSneaking()));
            swingDirect(Hand.MAIN_HAND);
            postAttack();
        }
    }

    /**
     *
     * @return
     */
    private boolean preSwapCheck()
    {
        if (mc.options.useKey.isPressed() || mc.options.attackKey.isPressed())
        {
            autoSwapTimer.reset();
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
                        mc.player.getBlockPos(), Direction.getFacing(mc.player.getX(),
                        mc.player.getY(), mc.player.getZ())));
            }
        }
        sprinting = false;
        if (stopSprintConfig.getValue())
        {
            sprinting = Managers.POSITION.isSprinting();
            if (sprinting)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.STOP_SPRINTING));
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
        if (stopSprintConfig.getValue() && sprinting)
        {
            Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
    }

    /**
     *
     *
     * @param pos
     * @return
     */
    private Entity getAuraTarget(final Vec3d pos)
    {
        Entity target = null;
        for (Entity e : mc.world.getEntities())
        {
            if (e != null && e.isAlive() && !Managers.SOCIAL.isFriend(e.getUuid()))
            {
                if (e instanceof EndCrystalEntity)
                {
                    continue;
                }
                if (e.age < ticksExistedConfig.getValue())
                {
                    continue;
                }
                if (isEnemy(e))
                {
                    final Vec3d hitVec = getHitVec(e);
                    //
                    double dist = pos.distanceTo(hitVec);
                    if (dist > rangeConfig.getValue())
                    {
                        continue;
                    }
                    BlockHitResult result = mc.world.raycast(new RaycastContext(
                            Managers.POSITION.getCameraPosVec(1.0f),
                            hitVec, RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE, mc.player));
                    if (result != null && dist > wallRangeConfig.getValue())
                    {
                        continue;
                    }

                    target = e;
                }
            }
        }
        return target;
    }

    /**
     *
     *
     * @return
     */
    private int getWeaponSlot()
    {
        float damage = 0.0f;
        int slot = -1;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            final Item item = stack.getItem();
            if (item instanceof SwordItem sword)
            {
                if (sword.getAttackDamage() > damage)
                {
                    damage = sword.getAttackDamage();
                    slot = i;
                }
            }
            else if (item instanceof MiningToolItem tool)
            {
                if (tool.getAttackDamage() > damage)
                {
                    damage = tool.getAttackDamage();
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
            tick += rotateSuspendConfig.getValue();
            yaws = new float[tick];
            int off = tick - 1;
            float yawTotal = 0.0f;
            for (int i = 0; i < tick; ++i)
            {
                if (i > yawCount)
                {
                    yaws[off - i] = 0.0f;
                    continue;
                }
                yawTotal += deltaYaw;
                yaws[off - i] = yawTotal;
            }
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
        }
        pitch = dest[1];
        return yaws.length;
    }

    /**
     *
     *
     * @return
     */
    private float getServerTicks()
    {
        return switch (tpsSyncConfig.getValue())
                {
                    case AVERAGE -> Managers.TICK.getTpsAverage();
                    case CURRENT -> Managers.TICK.getTpsCurrent();
                    case MINIMAL -> Managers.TICK.getTpsMin();
                    case NONE -> 20.0f;
                };
    }

    /**
     *
     *
     * @param entity
     * @return
     */
    public Vec3d getHitVec(Entity entity)
    {
        return switch (hitVectorConfig.getValue())
        {
            case EYES -> VecUtil.toEyePos(entity);
            case TORSO -> VecUtil.toTorsoPos(entity);
            case FEET -> entity.getPos();
        };
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
}
