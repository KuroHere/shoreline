package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.config.ConfigUpdateEvent;
import com.caspian.client.impl.event.entity.player.PlayerMoveEvent;
import com.caspian.client.impl.event.gui.click.ToggleGuiEvent;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.player.MovementUtil;
import com.caspian.client.util.string.EnumFormatter;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec2f;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SpeedModule extends ToggleModule
{
    //
    Config<Speed> speedModeConfig = new EnumConfig<>("Mode", "Speed mode",
            Speed.STRAFE, Speed.values());
    Config<Boolean> strictJumpConfig = new BooleanConfig("StrictJump", "Use " +
            "slightly higher and slower jumps to bypass NCP", false,
            () -> speedModeConfig.getValue() == Speed.STRAFE_STRICT);
    Config<Boolean> timerConfig = new BooleanConfig("UseTimer", "Uses " +
            "timer to increase acceleration", false);
    Config<Boolean> strafeBoostConfig = new BooleanConfig("StrafeBoost",
            "Uses explosion velocity to boost Strafe", false);
    Config<Boolean> speedWaterConfig = new BooleanConfig("SpeedInWater",
            "Applies speed even in water and lava", false);
    //
    private int strafe = 4;
    private boolean accelerate;
    private int strictTicks;
    //
    private double speed;
    private double distance;
    //
    private boolean prevTimer;

    /**
     *
     */
    public SpeedModule()
    {
        super("Speed", "Move faster", ModuleCategory.MOVEMENT);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        return EnumFormatter.formatEnum(speedModeConfig.getValue());
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        prevTimer = Modules.TIMER.isEnabled();
        if (timerConfig.getValue() && !prevTimer)
        {
            Modules.TIMER.enable();
        }
    }

    /**
     *
     *
     */
    @Override
    public void onDisable()
    {
        clear();
        if (Modules.TIMER.isEnabled())
        {
            Modules.TIMER.resetTimer();
            if (!prevTimer)
            {
                Modules.TIMER.disable();
            }
        }
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
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            double dx = mc.player.getX() - mc.player.prevX;
            double dz = mc.player.getZ() - mc.player.prevZ;
            distance = Math.sqrt(dx * dx + dz * dz);
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (!MovementUtil.isInputtingMovement()
                    || Modules.FLIGHT.isEnabled()
                    || Modules.LONG_JUMP.isEnabled()
    //              || Modules.ELYTRA_FLY.isEnabled()
                    || mc.player.isRiding()
                    || mc.player.isFallFlying()
                    || mc.player.isHoldingOntoLadder()
                    || mc.player.fallDistance > 2.0f
                    || (mc.player.isInLava() || mc.player.isTouchingWater())
                    && !speedWaterConfig.getValue())
            {
                clear();
                Modules.TIMER.setTimer(1.0f);
                return;
            }
            event.cancel();
            //
            double speedEffect = 1.0;
            double slowEffect = 1.0;
            if (mc.player.hasStatusEffect(StatusEffects.SPEED))
            {
                double amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
                speedEffect = 1 + (0.2 * (amplifier + 1));
            }
            if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS))
            {
                double amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
                slowEffect = 1 + (0.2 * (amplifier + 1));
            }
            final double base = 0.2873f * speedEffect / slowEffect;
            // ~29 kmh
            if (speedModeConfig.getValue() == Speed.STRAFE)
            {
                if (timerConfig.getValue())
                {
                    Modules.TIMER.setTimer(1.0888f);
                }
                if (strafe == 1)
                {
                    speed = 1.35f * base - 0.01f;
                }
                else if (strafe == 2)
                {
                    if (mc.player.input.jumping || !mc.player.isOnGround())
                    {
                        return;
                    }
                    float jump = 0.3999999463558197f;
                    if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
                    {
                        double amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
                        jump += (amplifier + 1) * 0.1f;
                    }
                    event.setY(jump);
                    Managers.MOVEMENT.setMotionY(jump);
                    speed *= accelerate ? 1.6835 : 1.395;
                }
                else if (strafe == 3)
                {
                    double moveSpeed = 0.66 * (distance - base);
                    speed = distance - moveSpeed;
                    accelerate = !accelerate;
                }
                else
                {
                    if ((!mc.world.isSpaceEmpty(mc.player, mc.player.getBoundingBox().offset(0,
                            mc.player.getVelocity().getY(), 0)) || mc.player.verticalCollision) && strafe > 0)
                    {
                        strafe = MovementUtil.isInputtingMovement() ? 1 : 0;
                    }
                    speed = distance - distance / 159;
                }
                speed = Math.max(speed, base);
                final Vec2f motion = handleStrafeMotion((float) speed);
                event.setX(motion.x);
                event.setZ(motion.y);
                strafe++;
            }
            // ~26-27 kmh
            else if (speedModeConfig.getValue() == Speed.STRAFE_STRICT)
            {
                if (strafe == 1)
                {
                    speed = 1.35f * base - 0.01f;
                }
                else if (strafe == 2)
                {
                    if (mc.player.input.jumping || !mc.player.isOnGround())
                    {
                        return;
                    }
                    float jump = strictJumpConfig.getValue() ?
                            0.41999998688697815f : 0.3999999463558197f;
                    if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
                    {
                        double amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
                        jump += (amplifier + 1) * 0.1f;
                    }
                    event.setY(jump);
                    Managers.MOVEMENT.setMotionY(jump);
                    speed *= 2.149;
                }
                else if (strafe == 3)
                {
                    double moveSpeed = 0.66 * (distance - base);
                    speed = distance - moveSpeed;
                }
                else
                {
                    if ((!mc.world.isSpaceEmpty(mc.player, mc.player.getBoundingBox().offset(0,
                            mc.player.getVelocity().getY(), 0)) || mc.player.verticalCollision) && strafe > 0)
                    {
                        strafe = MovementUtil.isInputtingMovement() ? 1 : 0;
                    }
                    speed = distance - distance / 159;
                }
                strictTicks++;
                speed = Math.max(speed, base);
                //
                double baseMax = 0.465 * speedEffect / slowEffect;
                double baseMin = 0.44 * speedEffect / slowEffect;
                speed = Math.min(speed, strictTicks > 25 ? baseMax : baseMin);
                if (strictTicks > 50)
                {
                    strictTicks = 0;
                }
                final Vec2f motion = handleStrafeMotion((float) speed);
                event.setX(motion.x);
                event.setZ(motion.y);
                strafe++;
            }
        }
    }

    /**
     *
     *
     * @param speed
     * @return
     */
    private Vec2f handleStrafeMotion(final float speed)
    {
        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getTickDelta();
        if (forward == 0.0f && strafe == 0.0f)
        {
            return Vec2f.ZERO;
        }
        else if (forward != 0.0f)
        {
            if (strafe >= 1.0f)
            {
                yaw += forward > 0.0f ? -45 : 45;
                strafe = 0.0f;
            }
            else if (strafe <= -1.0f)
            {
                yaw += forward > 0.0f ? 45 : -45;
                strafe = 0.0f;
            }
            if (forward > 0.0f)
            {
                forward = 1.0f;
            }
            else if (forward < 0.0f)
            {
                forward = -1.0f;
            }
        }
        float rx = (float) Math.cos(Math.toRadians(yaw));
        float rz = (float) -Math.sin(Math.toRadians(yaw));
        return new Vec2f((forward * speed * rz) + (strafe * speed * rx),
                (forward * speed * rx) - (strafe * speed * rz));
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
            if (event.getPacket() instanceof ExplosionS2CPacket packet)
            {
                double x = packet.getX() / 8000.0f;
                double z = packet.getZ() / 8000.0f;
                double boost = Math.sqrt(x * x + z * z);
            }
            else if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet)
            {
                if (packet.getId() == mc.player.getId())
                {
                    double x = packet.getVelocityX();
                    double z = packet.getVelocityZ();
                    double boost = Math.sqrt(x * x + z * z);
                }
            }
            if (event.getPacket() instanceof PlayerPositionLookS2CPacket)
            {
                clear();
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onConfigUpdate(ConfigUpdateEvent event)
    {
        if (event.getConfig() == timerConfig && event.getStage() == EventStage.POST)
        {
            if (timerConfig.getValue())
            {
                prevTimer = Modules.TIMER.isEnabled();
                if (!prevTimer)
                {
                    Modules.TIMER.enable();
                    // Modules.TIMER.setTimer(1.0888f);
                }
            }
            else if (Modules.TIMER.isEnabled())
            {
                Modules.TIMER.resetTimer();
                if (!prevTimer)
                {
                    Modules.TIMER.disable();
                }
            }
        }
    }

    /**
     *
     */
    public void setPrevTimer()
    {
        prevTimer = !prevTimer;
    }

    /**
     *
     * @return
     */
    public boolean isUsingTimer()
    {
        return isEnabled() && timerConfig.getValue();
    }

    /**
     *
     *
     */
    public void clear()
    {
        strafe = 4;
        strictTicks = 0;
        speed = 0.0f;
        distance = 0.0;
        accelerate = false;
    }

    private enum Speed
    {
        STRAFE,
        STRAFE_STRICT
    }
}
