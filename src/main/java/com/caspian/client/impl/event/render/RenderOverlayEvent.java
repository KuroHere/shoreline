package com.caspian.client.impl.event.render;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.client.util.math.MatrixStack;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see com.caspian.client.mixin.gui.hud.MixinInGameHud
 */
public class RenderOverlayEvent extends Event
{
    //
    private final MatrixStack matrices;


    /**
     *
     *
     * @param matrices
     */
    public RenderOverlayEvent(MatrixStack matrices)
    {
        this.matrices = matrices;
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

    public static class Post extends RenderOverlayEvent
    {
        //
        private final float tickDelta;

        /**
         *
         * @param matrices
         * @param tickDelta
         */
        public Post(MatrixStack matrices, float tickDelta)
        {
            super(matrices);
            this.tickDelta = tickDelta;
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

    @Cancelable
    public static class StatusEffect extends RenderOverlayEvent
    {
        /**
         * @param matrices
         */
        public StatusEffect(MatrixStack matrices)
        {
            super(matrices);
        }
    }

    @Cancelable
    public static class ItemName extends RenderOverlayEvent
    {
        //
        private int x, y;

        /**
         * @param matrices
         */
        public ItemName(MatrixStack matrices)
        {
            super(matrices);
        }

        /**
         *
         * @param x
         */
        public void setX(int x)
        {
            this.x = x;
        }

        /**
         *
         * @param y
         */
        public void setY(int y)
        {
            this.y = y;
        }

        public boolean isUpdateXY()
        {
            return x != 0 && y != 0;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }
    }
}
