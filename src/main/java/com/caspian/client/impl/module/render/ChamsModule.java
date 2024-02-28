package com.caspian.client.impl.module.render;

import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.render.entity.RenderCrystalEvent;
import com.caspian.client.init.Modules;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import java.awt.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChamsModule extends ToggleModule
{
    /**
     *
     */
    public ChamsModule()
    {
        super("Chams", "Renders entity models through walls", ModuleCategory.RENDER);
    }

    private static final float SINE_45_DEGREES = (float) Math.sin(0.7853981633974483);

    /**
     *
     * @param event
     */
    @EventListener
    private void onRenderCrystal(RenderCrystalEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            event.cancel();
            return;
        }
        /*
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.disableCull();
        event.matrixStack.push();
        float h = EndCrystalEntityRenderer.getYOffset(event.endCrystalEntity, event.g);
        float j = ((float) event.endCrystalEntity.endCrystalAge + event.g) * 3.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexConsumer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        vertexConsumer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        event.matrixStack.push();
        Color color = Modules.COLORS.getColor();
        RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f,
                color.getBlue() / 255.0f, 60 / 255.0f);
        event.matrixStack.scale(2.0f, 2.0f, 2.0f);
        event.matrixStack.translate(0.0f, -0.5f, 0.0f);
        int k = OverlayTexture.DEFAULT_UV;
        event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        event.matrixStack.translate(0.0f, 1.5f + h / 2.0f, 0.0f);
        event.matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        event.frame.render(event.matrixStack, vertexConsumer, event.i, k);
        float l = 0.875f;
        event.matrixStack.scale(0.875f, 0.875f, 0.875f);
        event.matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        event.frame.render(event.matrixStack, vertexConsumer, event.i, k);
        event.matrixStack.scale(0.875f, 0.875f, 0.875f);
        event.matrixStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SINE_45_DEGREES, 0.0f, SINE_45_DEGREES));
        event.matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
        event.core.render(event.matrixStack, vertexConsumer, event.i, k);
        event.matrixStack.pop();
        event.matrixStack.pop();
        tessellator.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
         */
    }
}
