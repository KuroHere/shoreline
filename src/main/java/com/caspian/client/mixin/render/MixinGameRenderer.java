package com.caspian.client.mixin.render;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.render.*;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
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
 *
 * @see GameRenderer
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer
{

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render" +
            "/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD,
            ordinal = 0), method = "renderWorld")
    private void hookRenderWorld(float tickDelta, long limitTime,
                                 MatrixStack matrices, CallbackInfo ci)
    {
        final RenderWorldEvent renderWorldEvent =
                new RenderWorldEvent(matrices, tickDelta);
        Caspian.EVENT_HANDLER.dispatch(renderWorldEvent);
    }

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

    /**
     *
     * @param cir
     */
    @Inject(method = "shouldRenderBlockOutline", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir)
    {
        RenderBlockOutlineEvent renderBlockOutlineEvent =
                new RenderBlockOutlineEvent();
        Caspian.EVENT_HANDLER.dispatch(renderBlockOutlineEvent);
        if (renderBlockOutlineEvent.isCanceled())
        {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
