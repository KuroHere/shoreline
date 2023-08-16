package com.caspian.client.mixin.gui.hud;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.render.RenderOverlayEvent;
import com.caspian.client.util.Globals;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see InGameHud
 */
@Mixin(InGameHud.class)
public class MixinInGameHud implements Globals
{
    /**
     *
     *
     * @param matrices
     * @param tickDelta
     * @param ci
     */
    @Inject(method = "render", at = @At(value = "TAIL"))
    private void hookRender(MatrixStack matrices, float tickDelta,
                            CallbackInfo ci)
    {
        RenderOverlayEvent.Post renderOverlayEvent =
            new RenderOverlayEvent.Post(matrices, tickDelta);
        Caspian.EVENT_HANDLER.dispatch(renderOverlayEvent);
    }

    /**
     *
     *
     * @param matrices
     * @param ci
     */
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookRenderStatusEffectOverlay(MatrixStack matrices,
                            CallbackInfo ci)
    {
        RenderOverlayEvent.StatusEffect renderOverlayEvent =
                new RenderOverlayEvent.StatusEffect(matrices);
        Caspian.EVENT_HANDLER.dispatch(renderOverlayEvent);
        if (renderOverlayEvent.isCanceled())
        {
            ci.cancel();
        }
    }

    /**
     *
     *
     * @param instance
     * @param matrices
     * @param text
     * @param x
     * @param y
     * @param color
     * @return
     */
    @Redirect(method = "renderHeldItemTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow" +
                    "(Lnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/text/Text;FFI)I"))
    private int hookRenderHeldItemTooltip(TextRenderer instance,
                                          MatrixStack matrices, Text text,
                                          float x, float y, int color)
    {
        RenderOverlayEvent.ItemName renderOverlayEvent =
                new RenderOverlayEvent.ItemName(matrices);
        Caspian.EVENT_HANDLER.dispatch(renderOverlayEvent);
        if (renderOverlayEvent.isCanceled())
        {
            if (renderOverlayEvent.isUpdateXY())
            {
                return instance.drawWithShadow(matrices, text,
                        renderOverlayEvent.getX(), renderOverlayEvent.getY(), color);
            }
            return 0;
        }
        return instance.drawWithShadow(matrices, text, x, y, color);
    }
}
