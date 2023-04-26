package com.caspian.impl.event.render;

import com.caspian.api.event.Event;
import net.minecraft.client.util.math.MatrixStack;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see com.caspian.asm.gui.hud.MixinInGameHud
 */
public class RenderOverlayEvent extends Event
{
    //
    private final MatrixStack matrices;

    //
    private final float tickDelta;

    /**
     *
     *
     * @param matrices
     * @param tickDelta
     */
    public RenderOverlayEvent(MatrixStack matrices, float tickDelta)
    {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    /**
     *
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
