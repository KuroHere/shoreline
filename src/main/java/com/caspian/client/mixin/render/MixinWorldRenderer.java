package com.caspian.client.mixin.render;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
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
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer
{
    /**
     *
     *
     * @param matrices
     * @param tickDelta
     * @param limitTime
     * @param renderBlockOutline
     * @param camera
     * @param gameRenderer
     * @param lightmapTextureManager
     * @param positionMatrix
     * @param ci
     */
    @Inject(method = "render", at = @At(value = "HEAD"))
    private void hookRender(MatrixStack matrices, float tickDelta,
                            long limitTime, boolean renderBlockOutline,
                            Camera camera, GameRenderer gameRenderer,
                            LightmapTextureManager lightmapTextureManager,
                            Matrix4f positionMatrix, CallbackInfo ci)
    {
        RenderWorldEvent renderWorldEvent = new RenderWorldEvent(matrices, tickDelta);
        Caspian.EVENT_HANDLER.dispatch(renderWorldEvent);
    }
}
