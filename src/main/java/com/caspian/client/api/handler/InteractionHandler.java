package com.caspian.client.api.handler;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InteractionHandler implements Globals
{
    // TODO: usingItem impl
    private boolean breakingBlock, usingItem;

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerActionC2SPacket packet)
            {
                if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK)
                {
                    breakingBlock = true;
                }
                else if (packet.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
                        || packet.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK)
                {
                    breakingBlock = false;
                }
            }
            else if (event.getPacket() instanceof PlayerInteractItemC2SPacket)
            {
                usingItem = true;
            }
            else if (event.getPacket() instanceof PlayerInteractBlockC2SPacket)
            {
                usingItem = true;
            }
        }
    }

    /**
     *
     *
     * @return
     */
    public boolean isBreakingBlock()
    {
        return breakingBlock;
    }
}
