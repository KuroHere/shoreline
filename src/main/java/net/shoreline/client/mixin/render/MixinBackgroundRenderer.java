package net.shoreline.client.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.render.RenderFogEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author linus
 * @since 1.0
 */
@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {
    /**
     * @param camera
     * @param fogType
     * @param viewDistance
     * @param thickFog
     * @param tickDelta
     * @param ci
     */
    @Inject(method = "applyFog", at = @At(value = "TAIL"))
    private static void hookApplyFog(Camera camera, BackgroundRenderer.FogType fogType,
                                     float viewDistance, boolean thickFog,
                                     float tickDelta, CallbackInfo ci) {
        if (fogType != BackgroundRenderer.FogType.FOG_TERRAIN) {
            return;
        }
        RenderFogEvent renderFogEvent = new RenderFogEvent();
        Shoreline.EVENT_HANDLER.dispatch(renderFogEvent);
        if (renderFogEvent.isCanceled()) {
            RenderSystem.setShaderFogStart(viewDistance * 4.0f);
            RenderSystem.setShaderFogEnd(viewDistance * 4.25f);
        }
    }
}
