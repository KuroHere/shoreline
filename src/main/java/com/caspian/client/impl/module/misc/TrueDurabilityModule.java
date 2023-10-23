package com.caspian.client.impl.module.misc;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.item.DurabilityEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TrueDurabilityModule extends ToggleModule
{
    /**
     *
     */
    public TrueDurabilityModule()
    {
        super("TrueDurability", "Displays the true durability of unbreakable items",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onDurability(DurabilityEvent event)
    {
        // ??? Whats this
        int dura = event.getItemDamage();
        if (event.getDamage() < 0)
        {
            dura = event.getDamage();
        }
        event.cancel();
        event.setDamage(dura);
    }
}
