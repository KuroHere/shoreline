package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.entity.player.PlayerMoveEvent;
import com.caspian.client.init.Managers;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class HighJumpModule extends ToggleModule
{
    //
    Config<Float> heightConfig = new NumberConfig<>("Height", "The height to " +
            "jump on the ground", 0.1f, 0.42f, 1.0f);
    Config<Boolean> airJumpConfig = new BooleanConfig("InAir", "Allows jumps " +
            "in the air (i.e. double jumps)", false);

    /**
     *
     */
    public HighJumpModule()
    {
        super("HighJump", "Allows player to jump higher", ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (mc.options.jumpKey.isPressed() && (mc.player.isOnGround()
                || airJumpConfig.getValue()))
        {
            Managers.MOVEMENT.setMotionY(heightConfig.getValue());
            event.cancel();
            event.setY(mc.player.getVelocity().y);
        }
    }
}
