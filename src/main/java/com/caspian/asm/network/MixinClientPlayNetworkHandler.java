package com.caspian.asm.network;

import com.caspian.Caspian;
import com.caspian.impl.event.chat.ChatInputEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler
{
    /**
     *
     *
     * @param content
     * @param ci
     */
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void hookSendChatMessage(String content, CallbackInfo ci)
    {
        ChatInputEvent chatInputEvent = new ChatInputEvent(content);
        Caspian.EVENT_HANDLER.dispatch(chatInputEvent);

        // prevent chat packet from sending
        if (chatInputEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
