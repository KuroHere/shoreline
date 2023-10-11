package com.caspian.client.impl.module.misc;

import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.ScreenOpenEvent;
import com.caspian.client.impl.event.TickEvent;
import net.minecraft.client.gui.screen.DeathScreen;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoRespawnModule extends ToggleModule
{
    //
    private boolean respawn;

    /**
     *
     */
    public AutoRespawnModule()
    {
        super("AutoRespawn", "Respawns automatically after a death",
                ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE && mc.player.isDead() && respawn)
        {
            mc.player.requestRespawn();
            respawn = false;
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onScreenOpen(ScreenOpenEvent event)
    {
        if (event.getScreen() instanceof DeathScreen)
        {
            respawn = true;
        }
    }
}
