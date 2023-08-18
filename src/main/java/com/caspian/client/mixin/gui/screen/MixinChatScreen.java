package com.caspian.client.mixin.gui.screen;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.chat.ChatMessageEvent;
import com.caspian.client.mixin.accessor.AccessorChatScreen;
import com.caspian.client.mixin.accessor.AccessorTextFieldWidget;
import com.caspian.client.impl.event.chat.ChatInputEvent;
import com.caspian.client.impl.event.chat.ChatRenderEvent;
import com.caspian.client.util.Globals;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(ChatScreen.class)
public class MixinChatScreen implements Globals
{
    /**
     *
     *
     * @param keyCode
     * @param scanCode
     * @param modifiers
     * @param cir
     */
    @Inject(method = "keyPressed", at = @At(value = "TAIL"))
    private void hookKeyPressed(int keyCode, int scanCode, int modifiers,
                                CallbackInfoReturnable<Boolean> cir)
    {
        ChatScreen source = (ChatScreen) (Object) this;
        TextFieldWidget chatField = ((AccessorChatScreen) source).getChatField();
        ChatInputEvent chatKeyPressedEvent =
                new ChatInputEvent(keyCode, chatField.getText());
        Caspian.EVENT_HANDLER.dispatch(chatKeyPressedEvent);
    }

    /**
     *
     *
     * @param chatText
     * @param addToHistory
     * @param cir
     */
    @Inject(method = "sendMessage", at = @At(value = "HEAD"))
    private void hookSendMessage(String chatText, boolean addToHistory,
                                 CallbackInfoReturnable<Boolean> cir)
    {
        ChatMessageEvent.Client chatMessageEvent =
                new ChatMessageEvent.Client(chatText);
        Caspian.EVENT_HANDLER.dispatch(chatMessageEvent);
    }

    /**
     *
     *
     * @param matrices
     * @param mouseX
     * @param mouseY
     * @param delta
     * @param ci
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet" +
            "/minecraft/client/gui/screen/ChatScreen;fill(Lnet/minecraft/c" +
            "lient/util/math/MatrixStack;IIIII)V", shift = At.Shift.BEFORE))
    private void hookRender(MatrixStack matrices, int mouseX, int mouseY, float delta,
                            CallbackInfo ci)
    {
        ChatScreen source = (ChatScreen) (Object) this;
        TextFieldWidget chatField = ((AccessorChatScreen) source).getChatField();
        float x = ((AccessorTextFieldWidget) chatField).isDrawsBackground() ?
                chatField.getX() + 4 : chatField.getX();
        float y = ((AccessorTextFieldWidget) chatField).isDrawsBackground() ?
                chatField.getY() + (chatField.getHeight() - 8) / 2.0f :
                chatField.getY();
        ChatRenderEvent chatTextRenderEvent = new ChatRenderEvent(matrices,
                x ,y);
        Caspian.EVENT_HANDLER.dispatch(chatTextRenderEvent);
    }
}
