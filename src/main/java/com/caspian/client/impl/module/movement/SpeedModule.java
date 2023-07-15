package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.entity.player.PlayerMoveEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Modules;
import com.caspian.client.util.player.MovementUtil;
import com.google.common.collect.Lists;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;

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
            "slightly higher and slower jumps to bypass NCP", false);
    Config<Boolean> timerConfig = new BooleanConfig("UseTimer", "Uses " +
            "timer to increase acceleration", false);
    Config<Boolean> speedWaterConfig = new BooleanConfig("SpeedInWater",
            "Applies speed even in water and lava", false);
    //
    private int strafe = 4;
    private boolean accelerate;
    private int strictTicks;
    //
    private double speed;
    private double distance;

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
     */
    @Override
    public void onEnable()
    {
        clear();
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
        if (mc.player != null && MovementUtil.isInputtingMovement())
        {
            if (mc.player.isRiding()
                    || mc.player.isFallFlying()
                    || mc.player.isHoldingOntoLadder()
                    || mc.player.fallDistance > 2.0f
                    || (mc.player.isInLava() || mc.player.isTouchingWater())
                    && !speedWaterConfig.getValue())
            {
                clear();
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
            final double speedFactor = speedEffect / slowEffect;
            final double base = 0.2873f * speedFactor;
            if (speedModeConfig.getValue() == Speed.STRAFE)
            {
                if (timerConfig.getValue())
                {
                    Modules.TIMER.setTimer(1.088f);
                }
                if (strafe == 1)
                {
                    speed = 1.35f * base - 0.01f;
                }
                else if (strafe == 2)
                {
                    float jump = 0.3999999463558197f;
                    if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
                    {
                        double amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
                        jump += (amplifier + 1) * 0.1f;
                    }
                    event.setY(jump);
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
                    if (mc.world.isSpaceEmpty(mc.player, mc.player.getBoundingBox().offset(0,
                            mc.player.getVelocity().getY(), 0)) || mc.player.verticalCollision && strafe > 0)
                    {
                        strafe = MovementUtil.isInputtingMovement() ? 1 : 0;
                    }
                    speed = distance - (distance / 159);
                }
                speed = Math.max(speed, base);
                final Vec2f motion = handleStrafeMotion((float) speed);
                event.setX(motion.x);
                event.setZ(motion.y);
                ++strafe;
            }
            else if (speedModeConfig.getValue() == Speed.STRAFE_STRICT)
            {
                if (strafe == 1)
                {
                    speed = 1.35f * base - 0.01f;
                }
                else if (strafe == 2)
                {
                    float jump = strictJumpConfig.getValue() ?
                            0.41999998688697815f : 0.3999999463558197f;
                    if (mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST))
                    {
                        double amplifier = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier();
                        jump += (amplifier + 1) * 0.1f;
                    }
                    event.setY(jump);
                    speed *= 2.149;
                }
                else if (strafe == 3)
                {
                    double moveSpeed = 0.66 * (distance - base);
                    speed = distance - moveSpeed;
                }
                else
                {
                    if (mc.world.isSpaceEmpty(mc.player, mc.player.getBoundingBox().offset(0,
                            mc.player.getVelocity().getY(), 0)) || mc.player.verticalCollision && strafe > 0)
                    {
                        strafe = MovementUtil.isInputtingMovement() ? 1 : 0;
                    }
                    speed = distance - (distance / 159);
                }
                speed = Math.max(speed, base);
                //
                double baseMax = 0.465 * speedFactor;
                double baseMin = 0.44 * speedFactor;
                speed = Math.min(speed, strictTicks > 25 ? baseMax : baseMin);
                strictTicks++;
                if (strictTicks > 50)
                {
                    strictTicks = 0;
                }
                final Vec2f motion = handleStrafeMotion((float) speed);
                event.setX(motion.x);
                event.setZ(motion.y);
                ++strafe;
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
        if (!MovementUtil.isInputtingMovement())
        {
            return Vec2f.ZERO;
        }
        else if (forward != 0)
        {
            if (strafe > 0)
            {
                yaw += forward > 0 ? -45 : 45;
            }
            else if (strafe < 0)
            {
                yaw += forward > 0 ? 45 : -45;
            }
            strafe = 0;
            if (forward > 0)
            {
                forward = 1;
            }
            else if (forward < 0)
            {
                forward = -1;
            }
        }
        float cos = (float) Math.cos(Math.toRadians(yaw));
        float sin = (float) -Math.sin(Math.toRadians(yaw));
        return new Vec2f((forward * speed * sin) + (strafe * speed * cos),
                (forward * speed * cos) - (strafe * speed * sin));
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet)
            {
                clear();
            }
        }
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
