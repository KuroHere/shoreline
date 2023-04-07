package com.momentum.asm.mixin.network;

import com.momentum.Momentum;
import com.momentum.impl.event.ChatMessageEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 *
 * @author linus
 * @since 04/04/2023
 *
 * @see ClientPlayNetworkHandler
 */
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler
{
    /**
     *
     * @param content
     * @param ci
     */
    @Inject(method = "sendChatMessage",
            at = @At(value = "HEAD"),
            cancellable = true)
    private void hookSendChatMessage(String content, CallbackInfo ci)
    {
        ChatMessageEvent chatMessageEvent = new ChatMessageEvent(content);
        Momentum.EVENT_HANDLER.dispatch(chatMessageEvent);

        // prevent chat message from sending
        if (chatMessageEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
