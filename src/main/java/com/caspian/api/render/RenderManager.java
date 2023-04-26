package com.caspian.api.render;

import com.caspian.util.Globals;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;

public class RenderManager implements Globals
{
    //
    public static final Tessellator TESSELLATOR = Tessellator.getInstance();
    public static final BufferBuilder BUFFER = TESSELLATOR.getBuffer();


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
}
