package com.momentum.asm.mixin.gui.screen;

import com.momentum.Momentum;
import com.momentum.api.util.Globals;
import com.momentum.asm.mixin.accessor.AccessorChatScreen;
import com.momentum.asm.mixin.accessor.AccessorTextFieldWidget;
import com.momentum.impl.event.ChatInputEvent;
import com.momentum.impl.event.ChatTextRenderEvent;
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
    @Inject(method = "keyPressed",
            at = @At(value = "TAIL"))
    private void hookKeyPressed(int keyCode, int scanCode, int modifiers,
                                CallbackInfoReturnable<Boolean> cir)
    {
        // chat field
        ChatScreen source = (ChatScreen) (Object) this;
        TextFieldWidget chatField = ((AccessorChatScreen) source).getChatField();

        // post chat key pressed event with chat field text
        ChatInputEvent chatKeyPressedEvent =
                new ChatInputEvent(keyCode, chatField.getText());
        Momentum.EVENT_HANDLER.dispatch(chatKeyPressedEvent);
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
        // chat field
        ChatScreen source = (ChatScreen) (Object) this;
        TextFieldWidget chatField = ((AccessorChatScreen) source).getChatField();

        ChatTextRenderEvent chatTextRenderEvent = new ChatTextRenderEvent();
        Momentum.EVENT_HANDLER.dispatch(chatTextRenderEvent);

        // render on top of chat field
        if (chatTextRenderEvent.isCanceled())
        {
            float x = ((AccessorTextFieldWidget) chatField).isDrawsBackground() ?
                            chatField.getX() + 4 : chatField.getX();
            float y = ((AccessorTextFieldWidget) chatField).isDrawsBackground() ?
                    chatField.getY() + (chatField.getHeight() - 8) / 2.0f :
                    chatField.getY();
            mc.textRenderer.drawWithShadow(matrices,
                    chatTextRenderEvent.getChatText(), x, y, 0xff808080);
        }
    }
}
