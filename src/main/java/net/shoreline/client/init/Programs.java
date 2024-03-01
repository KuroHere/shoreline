package net.shoreline.client.init;

import net.shoreline.client.impl.shaders.GradientProgram;

public class Programs
{
    public static GradientProgram GRADIENT;

    public static void renderInit()
    {
        GRADIENT = new GradientProgram();
    }
}
