package com.caspian.client.impl.module.combat;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.AttackCooldownEvent;

/**
 *
 *
 * @author Shoreline
 * @since 1.0
 */
public class NoHitDelayModule extends ToggleModule
{
    /**
     *
     */
    public NoHitDelayModule()
    {
        super("NoHitDelay", "Prevents hit delay when clicking fast", ModuleCategory.COMBAT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onAttackCooldown(AttackCooldownEvent event)
    {
        event.cancel();
    }
}
