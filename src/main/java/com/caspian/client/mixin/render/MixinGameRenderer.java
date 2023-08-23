package com.caspian.client.mixin.render;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.render.HurtCamEvent;
import com.caspian.client.impl.event.render.RenderFloatingItemEvent;
import com.caspian.client.impl.event.render.RenderNauseaEvent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see GameRenderer
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer
{
    /**
     *
     * @param matrices
     * @param tickDelta
     * @param ci
     */
    @Inject(method = "tiltViewWhenHurt", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookTiltViewWhenHurt(MatrixStack matrices, float tickDelta,
                                      CallbackInfo ci)
    {
        HurtCamEvent hurtCamEvent = new HurtCamEvent();
        Caspian.EVENT_HANDLER.dispatch(hurtCamEvent);
        if (hurtCamEvent.isCanceled())
        {
            ci.cancel();
        }
    }

    /**
     *
     * @param floatingItem
     * @param ci
     */
    @Inject(method = "showFloatingItem", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookShowFloatingItem(ItemStack floatingItem, CallbackInfo ci)
    {
        RenderFloatingItemEvent renderFloatingItemEvent =
                new RenderFloatingItemEvent(floatingItem);
        Caspian.EVENT_HANDLER.dispatch(renderFloatingItemEvent);
        if (renderFloatingItemEvent.isCanceled())
        {
            ci.cancel();
        }
    }

    /**
     *
     * @param distortionStrength
     * @param ci
     */
    @Inject(method = "renderNausea", at = @At(value = "HEAD"),  cancellable = true)
    private void hookRenderNausea(float distortionStrength, CallbackInfo ci)
    {
        RenderNauseaEvent renderNauseaEvent = new RenderNauseaEvent();
        Caspian.EVENT_HANDLER.dispatch(renderNauseaEvent);
        if (renderNauseaEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
