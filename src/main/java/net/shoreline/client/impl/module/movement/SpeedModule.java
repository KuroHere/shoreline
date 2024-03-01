package net.shoreline.client.impl.module.movement;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.config.ConfigUpdateEvent;
import net.shoreline.client.impl.event.entity.player.PlayerMoveEvent;
import net.shoreline.client.impl.event.network.DisconnectEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.player.MovementUtil;
import net.shoreline.client.util.string.EnumFormatter;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec2f;
import net.shoreline.client.util.Globals;

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
    public String getModuleData()
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
            double dx = Globals.mc.player.getX() - Globals.mc.player.prevX;
            double dz = Globals.mc.player.getZ() - Globals.mc.player.prevZ;
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
        if (Globals.mc.player != null && Globals.mc.world != null)
        {
            if (!MovementUtil.isInputtingMovement()
                    || Modules.FLIGHT.isEnabled()
                    || Modules.LONG_JUMP.isEnabled()
    //              || Modules.ELYTRA_FLY.isEnabled()
                    || Globals.mc.player.isRiding()
                    || Globals.mc.player.isFallFlying()
                    || Globals.mc.player.isHoldingOntoLadder()
                    || Globals.mc.player.fallDistance > 2.0f
                    || (Globals.mc.player.isInLava() || Globals.mc.player.isTouchingWater())
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
            if (Globals.mc.player.hasStatusEffect(StatusEffects.SPEED))
            {
                double amplifier = Globals.mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
                speedEffect = 1 + (0.2 * (amplifier + 1));
            }
            if (Globals.mc.player.hasStatusEffect(StatusEffects.SLOWNESS))
            {
                double amplifier = Globals.mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
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
                    if (Globals.mc.player.input.jumping || !Globals.mc.player.isOnGround())
                    {
                        return;
                    }
                    float jump = 0.3999999463558197f;
                    if (Globals.mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
                    {
                        double amplifier = Globals.mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
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
                    if ((!Globals.mc.world.isSpaceEmpty(Globals.mc.player, Globals.mc.player.getBoundingBox().offset(0,
                            Globals.mc.player.getVelocity().getY(), 0)) || Globals.mc.player.verticalCollision) && strafe > 0)
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
                    if (Globals.mc.player.input.jumping || !Globals.mc.player.isOnGround())
                    {
                        return;
                    }
                    float jump = strictJumpConfig.getValue() ?
                            0.41999998688697815f : 0.3999999463558197f;
                    if (Globals.mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
                    {
                        double amplifier = Globals.mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
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
                    if ((!Globals.mc.world.isSpaceEmpty(Globals.mc.player, Globals.mc.player.getBoundingBox().offset(0,
                            Globals.mc.player.getVelocity().getY(), 0)) || Globals.mc.player.verticalCollision) && strafe > 0)
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
    public Vec2f handleStrafeMotion(final float speed)
    {
        float forward = Globals.mc.player.input.movementForward;
        float strafe = Globals.mc.player.input.movementSideways;
        float yaw = Globals.mc.player.prevYaw + (Globals.mc.player.getYaw() - Globals.mc.player.prevYaw) * Globals.mc.getTickDelta();
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
        if (Globals.mc.player != null && Globals.mc.world != null)
        {
            if (event.getPacket() instanceof ExplosionS2CPacket packet)
            {
                double x = packet.getX() / 8000.0f;
                double z = packet.getZ() / 8000.0f;
                double boost = Math.sqrt(x * x + z * z);
            }
            else if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet)
            {
                if (packet.getId() == Globals.mc.player.getId())
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
