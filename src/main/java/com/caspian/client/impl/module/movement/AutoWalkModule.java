package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoWalkModule extends ToggleModule
{
    //
    Config<Boolean> lockConfig = new BooleanConfig("Lock", "Stops movement " +
            "when sneaking or jumping", false);

    /**
     *
     */
    public AutoWalkModule()
    {
        super("AutoWalk", "Automatically moves forward", ModuleCategory.MOVEMENT);
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        mc.options.forwardKey.setPressed(false);
    }

    /**
     *
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            mc.options.forwardKey.setPressed(!mc.options.sneakKey.isPressed()
                    && (!lockConfig.getValue() || (!mc.options.jumpKey.isPressed()
                    && mc.player.isOnGround())));
        }
    }
}
