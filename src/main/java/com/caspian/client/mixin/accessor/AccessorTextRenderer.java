package com.caspian.client.mixin.accessor;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 *
 *
 * @author Shoreline
 * @since 1.0
 */
@Mixin(TextRenderer.class)
public interface AccessorTextRenderer
{
    /**
     *
     * @param text
     * @param x
     * @param y
     * @param color
     * @param shadow
     * @param matrix
     * @param vertexConsumerProvider
     * @param layerType
     * @param underlineColor
     * @param light
     * @return
     */
    @Invoker("drawLayer")
    float hookDrawLayer(String text, float x, float y,
                        int color, boolean shadow, Matrix4f matrix,
                        VertexConsumerProvider vertexConsumerProvider,
                        TextRenderer.TextLayerType layerType, int underlineColor,
                        int light);
}
