package com.caspian.client.impl.module.movement;

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
public class ParkourModule extends ToggleModule
{
    /**
     *
     */
    public ParkourModule()
    {
        super("Parkour", "Automatically jumps at the edge of blocks",
                ModuleCategory.MOVEMENT);
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
        if (mc.player.isOnGround() && !mc.player.isSneaking()
                && mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)))
        {
            mc.player.jump();
        }
    }
}
