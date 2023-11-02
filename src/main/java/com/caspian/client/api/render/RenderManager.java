package com.caspian.client.api.render;

import com.caspian.client.mixin.accessor.AccessorWorldRenderer;
import com.caspian.client.util.Globals;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

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
     */
    private static void preRenderWorld()
    {
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    /**
     *
     */
    private static void postRenderWorld()
    {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

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
        preRenderWorld();
        matrices.push();
        Color c = new Color(color, true);
        RenderSystem.setShaderColor(c.getRed() / 255.0f, c.getGreen() / 255.0f,
                c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        BUFFER.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        drawBox(matrices, box);
        TESSELLATOR.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        postRenderWorld();
        matrices.pop();
    }

    /**
     *
     * @param matrices
     * @param box
     */
    public static void drawBox(MatrixStack matrices, Box box)
    {
        drawBox(matrices, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    /**
     * Draws a box spanning from [x1, y1, z1] to [x2, y2, z2].
     * The 3 axes centered at [x1, y1, z1] may be colored differently using
     * xAxisRed, yAxisGreen, and zAxisBlue.
     *
     * <p> Note the coordinates the box spans are relative to current
     * translation of the matrices.
     *
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public static void drawBox(MatrixStack matrices, double x1, double y1,
                               double z1, double x2, double y2, double z2)
    {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        float f = (float) x1;
        float g = (float) y1;
        float h = (float) z1;
        float i = (float) x2;
        float j = (float) y2;
        float k = (float) z2;
        BUFFER.vertex(matrix4f, f, g, h).next();
        BUFFER.vertex(matrix4f, i, g, h).next();
        BUFFER.vertex(matrix4f, i, g, k).next();
        BUFFER.vertex(matrix4f, f, g, k).next();
        BUFFER.vertex(matrix4f, f, j, h).next();
        BUFFER.vertex(matrix4f, f, j, k).next();
        BUFFER.vertex(matrix4f, i, j, k).next();
        BUFFER.vertex(matrix4f, i, j, h).next();
        BUFFER.vertex(matrix4f, f, g, h).next();
        BUFFER.vertex(matrix4f, f, j, h).next();
        BUFFER.vertex(matrix4f, i, j, h).next();
        BUFFER.vertex(matrix4f, i, g, h).next();
        BUFFER.vertex(matrix4f, i, g, h).next();
        BUFFER.vertex(matrix4f, i, j, h).next();
        BUFFER.vertex(matrix4f, i, j, k).next();
        BUFFER.vertex(matrix4f, i, g, k).next();
        BUFFER.vertex(matrix4f, f, g, k).next();
        BUFFER.vertex(matrix4f, i, g, k).next();
        BUFFER.vertex(matrix4f, i, j, k).next();
        BUFFER.vertex(matrix4f, f, j, k).next();
        BUFFER.vertex(matrix4f, f, g, h).next();
        BUFFER.vertex(matrix4f, f, g, k).next();
        BUFFER.vertex(matrix4f, f, j, k).next();
        BUFFER.vertex(matrix4f, f, j, h).next();
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
        preRenderWorld();
        matrices.push();
        Color c = new Color(color, true);
        RenderSystem.setShaderColor(c.getRed() / 255.0f, c.getGreen() / 255.0f,
                c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.lineWidth(width);
        BUFFER.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
        drawBoundingBox(matrices, box);
        TESSELLATOR.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        postRenderWorld();
        matrices.pop();
    }

    /**
     *
     * @param matrices
     * @param box
     */
    public static void drawBoundingBox(MatrixStack matrices, Box box)
    {
        drawBoundingBox(matrices, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    /**
     * 
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public static void drawBoundingBox(MatrixStack matrices, double x1, double y1, 
                                       double z1, double x2, double y2, double z2) 
    {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Matrix3f matrix3f = matrices.peek().getNormalMatrix();
        float f = (float) x1;
        float g = (float) y1;
        float h = (float) z1;
        float i = (float) x2;
        float j = (float) y2;
        float k = (float) z2;
        BUFFER.vertex(matrix4f, f, g, h).next();
        BUFFER.vertex(matrix4f, i, g, h).next();
        BUFFER.vertex(matrix4f, i, g, h).next();
        BUFFER.vertex(matrix4f, i, g, k).next();
        BUFFER.vertex(matrix4f, i, g, k).next();
        BUFFER.vertex(matrix4f, f, g, k).next();
        BUFFER.vertex(matrix4f, f, g, k).next();
        BUFFER.vertex(matrix4f, f, g, h).next();
        BUFFER.vertex(matrix4f, f, g, h).next();
        BUFFER.vertex(matrix4f, f, j, h).next();
        BUFFER.vertex(matrix4f, i, g, h).next();
        BUFFER.vertex(matrix4f, i, j, h).next();
        BUFFER.vertex(matrix4f, i, g, k).next();
        BUFFER.vertex(matrix4f, i, j, k).next();
        BUFFER.vertex(matrix4f, f, g, k).next();
        BUFFER.vertex(matrix4f, f, j, k).next();
        BUFFER.vertex(matrix4f, f, j, h).next();
        BUFFER.vertex(matrix4f, i, j, h).next();
        BUFFER.vertex(matrix4f, i, j, h).next();
        BUFFER.vertex(matrix4f, i, j, k).next();
        BUFFER.vertex(matrix4f, i, j, k).next();
        BUFFER.vertex(matrix4f, f, j, k).next();
        BUFFER.vertex(matrix4f, f, j, k).next();
        BUFFER.vertex(matrix4f, f, j, h).next();
    }

    /**
     *
     * @param matrices
     * @param s
     * @param d
     * @param width
     */
    public static void renderLine(MatrixStack matrices, Vec3d s,
                                  Vec3d d, float width, int color)
    {
        renderLine(matrices, s.x, s.y, s.z, d.x, d.y, d.z, width, color);
    }

    /**
     *
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @param width
     */
    public static void renderLine(MatrixStack matrices, double x1, double y1,
                                  double z1, double x2, double y2, double z2,
                                  float width, int color)
    {
        preRenderWorld();
        matrices.push();
        Color c = new Color(color, true);
        RenderSystem.setShaderColor(c.getRed() / 255.0f, c.getGreen() / 255.0f,
                c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.lineWidth(width);
        BUFFER.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION);
        drawLine(matrices, x1, y1, z1, x2, y2, z2);
        TESSELLATOR.draw();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        postRenderWorld();
        matrices.pop();
    }

    /**
     *
     * @param matrices
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     */
    public static void drawLine(MatrixStack matrices, double x1, double y1,
                                double z1, double x2, double y2, double z2)
    {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float f = (float) x1;
        float g = (float) y1;
        float h = (float) z1;
        float i = (float) x2;
        float j = (float) y2;
        float k = (float) z2;
        BUFFER.vertex(matrix4f, f, g, h).next();
        BUFFER.vertex(matrix4f, i, j, k).next();
    }

    /**
     *
     * @param matrices
     * @param text
     * @param pos
     */
    public static void renderSign(MatrixStack matrices, String text,
                                  Vec3d pos)
    {
        renderSign(matrices, text, pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     *
     * @param matrices
     * @param text
     * @param x1
     * @param x2
     * @param x3
     */
    public static void renderSign(MatrixStack matrices, String text,
                                  double x1, double x2, double x3)
    {

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
