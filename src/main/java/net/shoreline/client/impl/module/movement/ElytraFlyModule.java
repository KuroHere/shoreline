package net.shoreline.client.impl.module.movement;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.entity.player.TravelEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.mixin.accessor.AccessorPlayerMoveC2SPacket;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.util.Globals;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ElytraFlyModule extends ToggleModule
{
    //
    Config<FlyMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "elytra flight", FlyMode.CONTROL, FlyMode.values());
    Config<Float> speedConfig = new NumberConfig<>("Speed", "The horizontal " +
            "flight speed", 0.1f, 2.5f, 10.0f);
    Config<Float> vspeedConfig = new NumberConfig<>("VerticalSpeed", "The " +
            "vertical flight speed", 0.1f, 1.0f, 5.0f);
    Config<Boolean> instantFlyConfig = new BooleanConfig("InstantFly",
            "Automatically activates elytra from the ground", false);
    Config<Boolean> fireworkConfig = new BooleanConfig("Fireworks", "Uses " +
            "fireworks when flying", false, () -> modeConfig.getValue() != FlyMode.PACKET);
    //
    private float pitch;

    /**
     *
     */
    public ElytraFlyModule()
    {
        super("ElytraFly", "Allows you to fly freely using an elytra",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTravel(TravelEvent event)
    {
        if (event.getStage() != EventStage.PRE || mc.player == null
                || mc.world == null || !mc.player.isFallFlying())
        {
            return;
        }
        switch (modeConfig.getValue())
        {
            case CONTROL ->
            {
                event.cancel();
                float forward = mc.player.input.movementForward;
                float strafe = mc.player.input.movementSideways;
                float yaw = mc.player.getYaw();
                if (forward == 0.0f && strafe == 0.0f)
                {
                    Managers.MOVEMENT.setMotionXZ(0.0, 0.0);
                }
                else
                {
                    pitch = 12;
                    double rx = Math.cos(Math.toRadians(yaw + 90.0f));
                    double rz = Math.sin(Math.toRadians(yaw + 90.0f));
                    Managers.MOVEMENT.setMotionXZ(((forward * speedConfig.getValue() * rx)
                            + (strafe * speedConfig.getValue() * rz)), (forward * speedConfig.getValue() * rz)
                            - (strafe * speedConfig.getValue() * rx));
                }
                Managers.MOVEMENT.setMotionY(0.0);
                pitch = 0;
                if (mc.options.jumpKey.isPressed())
                {
                    pitch = -51;
                    Managers.MOVEMENT.setMotionY(vspeedConfig.getValue());
                }
                else if (mc.options.sneakKey.isPressed())
                {
                    Managers.MOVEMENT.setMotionY(-vspeedConfig.getValue());
                }
            }
            case BOOST ->
            {

            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player == null)
        {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet
                && packet.changesLook() && mc.player.isFallFlying())
        {
            ((AccessorPlayerMoveC2SPacket) packet).hookSetPitch(pitch);
        }
    }

    private void glideElytraVec(float pitch)
    {
        double d = 0.08;
        boolean bl = mc.player.getVelocity().y <= 0.0;
        if (bl && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING))
        {
            d = 0.01;
        }
        Vec3d vec3d4 = mc.player.getVelocity();
        Vec3d vec3d5 = getRotationVector(pitch, mc.player.getYaw());
        float f = pitch * 0.017453292f;
        double i = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
        double j = vec3d4.horizontalLength();
        double k = vec3d5.length();
        double l = Math.cos(f);
        l = l * l * Math.min(1.0, k / 0.4);
        vec3d4 = mc.player.getVelocity().add(0.0, d * (-1.0 + l * 0.75), 0.0);
        double m;
        // if (vec3d4.y < 0.0 && i > 0.0)
        // {
        //    m = vec3d4.y * -0.1 * l;
        //    vec3d4 = vec3d4.add(vec3d5.x * m / i, m, vec3d5.z * m / i);
        // }
        if (f < 0.0f && i > 0.0)
        {
            m = j * (double) (-MathHelper.sin(f)) * 0.04;
            vec3d4 = vec3d4.add(-vec3d5.x * m / i, m * 3.2, -vec3d5.z * m / i);
        }
        // if (i > 0.0)
        // {
        //     vec3d4 = vec3d4.add((vec3d5.x / i * j - vec3d4.x) * 0.1, 0.0, (vec3d5.z / i * j - vec3d4.z) * 0.1);
        // }
        mc.player.setVelocity(vec3d4.multiply(0.9900000095367432, 0.9800000190734863, 0.9900000095367432));
    }

    protected final Vec3d getRotationVector(float pitch, float yaw)
    {
        float f = pitch * 0.017453292f;
        float g = -yaw * 0.017453292f;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d((double) (i * j), (double) (-k), (double) (h * j));
    }

    public enum FlyMode
    {
        CONTROL,
        CONTROL_STRICT,
        ACCEL,
        BOOST,
        FACTORIZE,
        PACKET,
        BOUNCE
    }
}
