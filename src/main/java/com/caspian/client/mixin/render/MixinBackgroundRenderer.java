package com.caspian.client.mixin.render;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.render.RenderFogEvent;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
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
 */
@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer
{
    /**
     *
     * @param camera
     * @param fogType
     * @param viewDistance
     * @param thickFog
     * @param tickDelta
     * @param ci
     */
    @Inject(method = "applyFog", at = @At(value = "HEAD"), cancellable = true)
    private static void hookApplyFog(Camera camera,
                                     BackgroundRenderer.FogType fogType,
                                     float viewDistance, boolean thickFog,
                                     float tickDelta, CallbackInfo ci)
    {
        RenderFogEvent renderFogEvent = new RenderFogEvent();
        Caspian.EVENT_HANDLER.dispatch(renderFogEvent);
        if (renderFogEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
