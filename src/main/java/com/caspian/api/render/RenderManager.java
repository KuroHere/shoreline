package com.caspian.api.render;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;

public class RenderManager
{
    //
    public static final Tessellator TESSELLATOR = Tessellator.getInstance();
    public static final BufferBuilder BUFFER = TESSELLATOR.getBuffer();
}
