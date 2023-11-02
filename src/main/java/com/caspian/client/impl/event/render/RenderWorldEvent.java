package com.caspian.client.impl.event.render;

import com.caspian.client.api.event.Event;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

/**
 *
 * @author linus
 * @since 1.0
 */
public class RenderWorldEvent extends Event
{
    //
    private final MatrixStack matrices;
    private final float tickDelta;

    /**
     *
     *
     * @param matrices
     */
    public RenderWorldEvent(MatrixStack matrices, float tickDelta)
    {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    /**
     *
     * @return
     */
    public MatrixStack getMatrices()
    {
        return matrices;
    }

    /**
     *
     * @return
     */
    public float getTickDelta()
    {
        return tickDelta;
    }
}
