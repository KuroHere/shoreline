package com.caspian.client.util.player;

import com.caspian.client.mixin.accessor.AccessorClientPlayerEntity;
import com.caspian.client.util.Globals;
import net.minecraft.util.math.MathHelper;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class MovementUtil implements Globals
{
    /**
     *
     *
     * @return
     */
    public static boolean isInputtingMovement()
    {
        return mc.player.input.pressingForward
                || mc.player.input.pressingBack
                || mc.player.input.pressingLeft
                || mc.player.input.pressingRight;
    }

    /**
     *
     * @return
     */
    public static boolean isMovingInput()
    {
        return mc.player.input.movementForward != 0.0f
                || mc.player.input.movementSideways != 0.0f;
    }

    /**
     *
     *
     * @return
     */
    public static boolean isMoving()
    {
        double d = mc.player.getX() - ((AccessorClientPlayerEntity) mc.player).getLastX();
        double e = mc.player.getY() - ((AccessorClientPlayerEntity) mc.player).getLastBaseY();
        double f = mc.player.getZ() - ((AccessorClientPlayerEntity) mc.player).getLastZ();
        return MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0e-4);
    }
}
