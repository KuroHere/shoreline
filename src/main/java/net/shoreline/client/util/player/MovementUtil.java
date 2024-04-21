package net.shoreline.client.util.player;

import net.minecraft.client.input.Input;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.util.Globals;

/**
 * @author linus
 * @since 1.0
 */
public class MovementUtil implements Globals {
    /**
     * @return
     */
    public static boolean isInputtingMovement() {
        return mc.player.input.pressingForward
                || mc.player.input.pressingBack
                || mc.player.input.pressingLeft
                || mc.player.input.pressingRight;
    }

    /**
     * @return
     */
    public static boolean isMovingInput() {
        return mc.player.input.movementForward != 0.0f
                || mc.player.input.movementSideways != 0.0f;
    }

    /**
     * @return
     */
    public static boolean isMoving() {
        double d = mc.player.getX() - mc.player.lastX;
        double e = mc.player.getY() - mc.player.lastBaseY;
        double f = mc.player.getZ() - mc.player.lastZ;
        return MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(2.0e-4);
    }

    public static float getYawOffset(Input input, float rotationYaw)
    {
        if (input.movementForward < 0.0f) rotationYaw += 180.0f;

        float forward = 1.0f;
        if (input.movementForward < 0.0f) {
            forward = -0.5f;
        } else if (input.movementForward > 0.0f) {
            forward = 0.5f;
        }

        float strafe = input.movementSideways;
        if (strafe > 0.0f) rotationYaw -= 90.0f * forward;
        if (strafe < 0.0f) rotationYaw += 90.0f * forward;
        return rotationYaw;
    }
}
