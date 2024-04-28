package net.shoreline.client.impl.manager.anticheat;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.impl.event.network.DisconnectEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.Globals;

import java.util.Arrays;

/**
 * @author xgraza
 * @since 1.0
 */
public final class GrimManager implements Globals
{
    private final int[] transactions = new int[4];
    private int index;
    private boolean isGrim;

    public GrimManager()
    {
        Shoreline.EVENT_HANDLER.subscribe(this);
        Arrays.fill(transactions, -1);
    }

    @EventListener
    public void onPacketInbound(final PacketEvent.Inbound event)
    {
        if (event.getPacket() instanceof CommonPingS2CPacket packet)
        {
            if (index > 3)
            {
                return;
            }
            final int uid = packet.getParameter();
            transactions[index] = uid;
            ++index;
            if (index == 4)
            {
                grimCheck();
            }
        }
    }

    @EventListener
    public void onDisconnect(final DisconnectEvent event)
    {
        Arrays.fill(transactions, -1);
        index = 0;
        isGrim = false;
    }

    private void grimCheck()
    {
        for (int i = 0; i < 4; ++i)
        {
            if (transactions[i] != -i)
            {
                break;
            }
        }

        isGrim = true;
        Shoreline.LOGGER.info("Server is running GrimAC.");
    }

    public boolean isGrim()
    {
        return isGrim;
    }

    public boolean isGrimCC() {
        ServerInfo info = Managers.NETWORK.getInfo();
        return info != null && info.address.equalsIgnoreCase("grim.crystalpvp.cc");
    }
}
