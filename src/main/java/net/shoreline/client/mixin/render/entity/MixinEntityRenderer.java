package net.shoreline.client.mixin.render.entity;

import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.render.entity.RenderLabelEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
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
@Mixin(EntityRenderer.class)
public class MixinEntityRenderer
{
    /**
     *
     * @param entity
     * @param text
     * @param matrices
     * @param vertexConsumers
     * @param light
     * @param ci
     */
    @Inject(method = "renderLabelIfPresent", at = @At(value = "HEAD"),
            cancellable = true)
    public void hookRenderLabelIfPresent(Entity entity, Text text,
                                         MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers,
                                         int light, CallbackInfo ci)
    {
        RenderLabelEvent renderLabelEvent = new RenderLabelEvent(entity);
        Shoreline.EVENT_HANDLER.dispatch(renderLabelEvent);
        if (renderLabelEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
