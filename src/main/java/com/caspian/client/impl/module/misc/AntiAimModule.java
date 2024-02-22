package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.manager.player.rotation.RotationPriority;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.RotationModule;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PlayerUpdateEvent;
import com.caspian.client.impl.event.render.entity.RenderPlayerEvent;
import com.caspian.client.init.Managers;
import net.minecraft.util.math.Vec2f;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiAimModule extends RotationModule
{
    //
    Config<YawMode> yawModeConfig = new EnumConfig<>("Yaw", "The mode for the" +
            " rotation yaw spin ", YawMode.STATIC, YawMode.values());
    Config<PitchMode> pitchModeConfig = new EnumConfig<>("Pitch", "The mode " +
            "for the rotation pitch spin", PitchMode.DOWN, PitchMode.values());
    Config<Float> yawAddConfig = new NumberConfig<>("YawAdd", "The yaw to add" +
            " during each rotation", -180.0f, 0.0f, 180.0f);
    Config<Float> pitchAddConfig = new NumberConfig<>("PitchAdd", "The pitch " +
            "to add during each rotation", -90.0f, 0.0f, 90.0f);
    Config<Float> spinSpeedConfig = new NumberConfig<>("SpinSpeed", "The yaw " +
            "speed to rotate", 1.0f, 40.0f, 40.0f);
    Config<Integer> flipTicksConfig = new NumberConfig<>("FlipTicks", "The " +
            "number of ticks to wait between jitter", 2, 2, 20);
    //
    private float yaw;
    private float pitch;
    //
    private float prevYaw, prevPitch;

    /**
     *
     */
    public AntiAimModule()
    {
        super("AntiAim", "Makes it harder to accurately aim at the player",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        if (mc.player == null)
        {
            return;
        }
        prevYaw = mc.player.getYaw();
        prevPitch = mc.player.getPitch();
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerUpdate(PlayerUpdateEvent event)
    {
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
        if (mc.options.attackKey.isPressed() || mc.options.useKey.isPressed())
        {
            return;
        }
        if (isRotationBlocked())
        {
            return;
        }
        yaw = switch (yawModeConfig.getValue())
                {
                    case OFF -> mc.player.getYaw();
                    case STATIC -> mc.player.getYaw() + yawAddConfig.getValue();
                    case ZERO -> prevYaw;
                    case SPIN ->
                    {
                        float spin = yaw + spinSpeedConfig.getValue();
                        if (spin > 360.0f)
                        {
                            yield spin - 360.0f;
                        }
                        yield spin;
                    }
                    case JITTER -> mc.player.getYaw() + ((mc.player.age % flipTicksConfig.getValue() == 0) ?
                            yawAddConfig.getValue() : -yawAddConfig.getValue());
                };
        pitch = switch (pitchModeConfig.getValue())
                {
                    case OFF -> mc.player.getPitch();
                    case STATIC -> pitchAddConfig.getValue();
                    case ZERO -> prevPitch;
                    case UP ->  -90.0f;
                    case DOWN -> 90.0f;
                    case JITTER ->
                    {
                        float jitter = pitch + 30.0f;
                        if (jitter > 90.0f)
                        {
                            yield -90.0f;
                        }
                        if (jitter < -90.0f)
                        {
                            yield 90.0f;
                        }
                        yield jitter;
                    }
                };
        //
        Managers.ROTATION.setRotation(this, RotationPriority.LOW, yaw, pitch);
    }

    public enum YawMode
    {
        OFF, STATIC, ZERO, SPIN, JITTER
    }

    public enum PitchMode
    {
        OFF, STATIC, ZERO, UP, DOWN, JITTER
    }
}
