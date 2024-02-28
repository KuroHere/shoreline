package com.caspian.client.init;

import com.caspian.client.impl.shaders.GradientProgram;

public class Programs
{
    public static GradientProgram GRADIENT;

    public static void renderInit()
    {
        GRADIENT = new GradientProgram();
    }
}
