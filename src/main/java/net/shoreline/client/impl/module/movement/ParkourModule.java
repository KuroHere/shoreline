package net.shoreline.client.impl.module.movement;

import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.util.Globals;

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
        if (Globals.mc.player.isOnGround() && !Globals.mc.player.isSneaking()
                && Globals.mc.world.isSpaceEmpty(Globals.mc.player.getBoundingBox().offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)))
        {
            Globals.mc.player.jump();
        }
    }
}
