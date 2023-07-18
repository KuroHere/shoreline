package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.string.EnumFormatter;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class FastFallModule extends ToggleModule
{
    //
    Config<Float> heightConfig = new NumberConfig<>("Height", "The maximum " +
            "fall height", 1.0f, 2.0f, 10.0f);
    Config<FallMode> fallModeConfig = new EnumConfig<>("Mode", "The mode for " +
            "falling down blocks", FallMode.STEP, FallMode.values());
    Config<Integer> shiftTicksConfig = new NumberConfig<>("ShiftTicks",
            "Number of ticks to shift ahead", 0, 3, 5,
            () -> fallModeConfig.getValue() == FallMode.SHIFT);

    /**
     *
     */
    public FastFallModule()
    {
        super("FastFall", "Falls down blocks faster", ModuleCategory.MOVEMENT);
    }

    @Override
    public String getMetaData()
    {
        return EnumFormatter.formatEnum(fallModeConfig.getValue());
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
            if (fallModeConfig.getValue() == FallMode.STEP)
            {
                if (mc.player.isRiding()
                        || mc.player.isFallFlying()
                        || mc.player.isHoldingOntoLadder()
                        || mc.player.fallDistance > 0.5f
                        || mc.player.isInLava()
                        || mc.player.isTouchingWater()
                        || mc.player.input.jumping
                        || mc.player.input.sneaking)
                {
                    return;
                }
                if (!Managers.NCP.passed(1000)
                        || Modules.SPEED.isEnabled())
                {
                    return;
                }
                if (Managers.POSITION.isOnGround())
                {
                    if (getNearestBlockY() != -1)
                    {
                        final Vec3d motion = mc.player.getVelocity();
                        mc.player.setVelocity(motion.getX(), -3.0, motion.getZ());
                        // Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
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
    private int getNearestBlockY()
    {
        int y = mc.player.getBlockY();
        for (int i = y; i < heightConfig.getValue(); i++)
        {

        }
        return y;
    }

    public enum FallMode
    {
        STEP,
        SHIFT
    }
}
