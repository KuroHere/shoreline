package com.caspian.client.impl.event.network;

import com.caspian.client.api.event.Event;
import net.minecraft.network.packet.Packet;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PacketEvent extends Event
{
    //
    private final Packet<?> packet;

    /**
     *
     *
     * @param packet
     */
    public PacketEvent(Packet<?> packet)
    {
        this.packet = packet;
    }

    /**
     *
     *
     * @return
     */
    public Packet<?> getPacket()
    {
        return packet;
    }

    /**
     *
     */
    public static class Inbound extends PacketEvent
    {
        /**
         *
         *
         * @param packet
         */
        public Inbound(Packet<?> packet)
        {
            super(packet);
        }
    }

    /**
     *
     */
    public static class Outbound extends PacketEvent
    {
        /**
         *
         *
         * @param packet
         */
        public Outbound(Packet<?> packet)
        {
            super(packet);
        }
    }
}
