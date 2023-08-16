package com.caspian.client.mixin.network;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(ClientConnection.class)
public class MixinClientConnection
{
    /**
     *
     *
     * @param packet
     * @param ci
     */
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void hookSend(Packet<?> packet, CallbackInfo ci)
    {
        PacketEvent.Outbound packetOutboundEvent =
                new PacketEvent.Outbound(packet);
        Caspian.EVENT_HANDLER.dispatch(packetOutboundEvent);
        // prevent client from sending packet to server
        if (packetOutboundEvent.isCanceled())
        {
            ci.cancel();
        }
    }

    /**
     *
     *
     * @param channelHandlerContext
     * @param packet
     * @param ci
     */
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;" +
            "Lnet/minecraft/network/packet/Packet;)V", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookChannelRead0(ChannelHandlerContext channelHandlerContext,
                                  Packet<?> packet, CallbackInfo ci)
    {
        PacketEvent.Inbound packetInboundEvent =
                new PacketEvent.Inbound(packet);
        Caspian.EVENT_HANDLER.dispatch(packetInboundEvent);
        // prevent client from receiving packet from server
        if (packetInboundEvent.isCanceled())
        {
            ci.cancel();
        }
    }

    /**
     *
     *
     * @param disconnectReason
     * @param ci
     */
    @Inject(method = "disconnect", at = @At(value = "HEAD"))
    private void hookDisconnect(Text disconnectReason, CallbackInfo ci)
    {
        DisconnectEvent disconnectEvent = new DisconnectEvent();
        Caspian.EVENT_HANDLER.dispatch(disconnectEvent);
    }
}
