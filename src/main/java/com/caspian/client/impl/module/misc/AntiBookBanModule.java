package com.caspian.client.impl.module.misc;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.DecodePacketEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiBookBanModule extends ToggleModule
{
    /**
     *
     */
    public AntiBookBanModule()
    {
        super("AntiBookBan", "Prevents getting book banned (Causes connection" +
                " issues, only use if actually book banned)", ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onDecodePacket(DecodePacketEvent event)
    {
        event.cancel();
    }
}
