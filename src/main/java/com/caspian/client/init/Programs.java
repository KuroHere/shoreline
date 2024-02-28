package com.caspian.client.init;

import com.caspian.client.api.render.shader.shaders.GradientProgram;

public class Programs
{
    public static GradientProgram GRADIENT;

    public static void renderInit()
    {
        GRADIENT = new GradientProgram();
    }
}
