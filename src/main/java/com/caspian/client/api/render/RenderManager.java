package com.caspian.client.api.render;

import com.caspian.client.mixin.accessor.AccessorWorldRenderer;
import com.caspian.client.util.Globals;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.awt.*;

/**
 *
 * 
 * @author linus
 * @since 1.0
 */
public class RenderManager implements Globals
{
    //
    public static final Tessellator TESSELLATOR = RenderSystem.renderThreadTesselator();
    public static final BufferBuilder BUFFER = TESSELLATOR.getBuffer();
    
    /**
     *
     * @param matrices
     * @param p
     * @param color
     */
    public static void renderBox(MatrixStack matrices, BlockPos p, int color)
    {
        renderBox(matrices, new Box(p), color);
    }

    /**
     *
     *
     * @param matrices
     * @param box
     * @param color
     */
    public static void renderBox(MatrixStack matrices, Box box, int color)
    {
        if (!isFrustumVisible(box))
        {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        BUFFER.begin(VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_COLOR);
        Color c = new Color(color);
        drawBox(matrices, BUFFER, box, c.getRed(), c.getGreen(),
                c.getBlue(), c.getAlpha());
        TESSELLATOR.draw();
        RenderSystem.disableBlend();
    }

    /**
     *
     * @param matrices
     * @param vertexConsumer
     * @param box
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer,
                               Box box, float red, float green, float blue, float alpha)
    {
        drawBox(matrices, vertexConsumer, box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }
    

    /**
     * Draws a box spanning from [x1,y1,z1] to [x2,y2,z2].
     * The 3 axes centered at [x1,y1,z1] may be colored differently using
     * xAxisRed, yAxisGreen, and zAxisBlue.
     *
     * <p>Note the coordinates the box spans are relative to current
     * translation of the matrices.
     *
     * @param matrices
     * @param vertexConsumer
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumer,
                               double x1, double y1, double z1, double x2,
                               double y2, double z2, float red, float green,
                               float blue, float alpha)
    {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        float f = (float) x1;
        float g = (float) y1;
        float h = (float) z1;
        float i = (float) x2;
        float j = (float) y2;
        float k = (float) z2;
        vertexConsumer.vertex(matrix4f, f, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
    }

    /**
     *
     *
     * @param box
     * @param width
     * @param color
     */
    public static void renderBoundingBox(MatrixStack matrices, Box box,
                                         float width, int color)
    {
        if (!isFrustumVisible(box))
        {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(width);
        BUFFER.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP,
                VertexFormats.POSITION_COLOR);
        Color c = new Color(color);
        drawBoundingBox(matrices, BUFFER, box, c.getRed(), c.getGreen(),
                c.getBlue(), c.getAlpha());
        TESSELLATOR.draw();
        RenderSystem.disableBlend();
    }

    /**
     *
     *
     * @param p
     * @param width
     * @param color
     */
    public static void renderBoundingBox(MatrixStack matrices, BlockPos p,
                                         float width, int color)
    {
        renderBoundingBox(matrices, new Box(p), width, color);
    }

    /**
     *
     * @param matrices
     * @param vertexConsumer
     * @param box
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public static void drawBoundingBox(MatrixStack matrices, VertexConsumer vertexConsumer,
                                       Box box, float red, float green,
                                       float blue, float alpha)
    {
        drawBoundingBox(matrices, vertexConsumer, box.minX, box.minY,
                box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    /**
     * 
     * @param matrices
     * @param vertexConsumer
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public static void drawBoundingBox(MatrixStack matrices, VertexConsumer vertexConsumer,
                                       double x1, double y1, double z1, double x2,
                                       double y2, double z2, float red, float green,
                                       float blue, float alpha) 
    {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        float f = (float) x1;
        float g = (float) y1;
        float h = (float) z1;
        float i = (float) x2;
        float j = (float) y2;
        float k = (float) z2;
        vertexConsumer.vertex(matrix4f, f, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).next(); 
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next(); 
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, i, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, k).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, j, h).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, f, g, h).color(red, green, blue, alpha).next(); 
    }

    /**
     *
     *
     * @param box
     * @return
     */
    public static boolean isFrustumVisible(Box box)
    {
        return ((AccessorWorldRenderer) mc.worldRenderer).getFrustum().isVisible(box);
    }

    /**
     *
     *
     * @param matrices
     * @param text
     * @param x
     * @param y
     * @param color
     */
    public static void renderText(MatrixStack matrices, String text, float x,
                                  float y, int color)
    {
        mc.textRenderer.drawWithShadow(matrices, text, x, y, color);
    }

    /**
     *
     *
     * @param text
     * @return
     */
    public static int textWidth(String text)
    {
        return mc.textRenderer.getWidth(text);
    }
}
