package net.shoreline.client.util.player;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.Globals;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationUtil implements Globals
{
    /**
     *
     *
     * @param src
     * @param dest
     * @return
     */
    public static float[] getRotationsTo(Vec3d src, Vec3d dest)
    {
        float yaw = (float) (Math.toDegrees(Math.atan2(dest.subtract(src).z,
                dest.subtract(src).x)) - 90);
        float pitch = (float) Math.toDegrees(-Math.atan2(dest.subtract(src).y,
                Math.hypot(dest.subtract(src).x, dest.subtract(src).z)));
        // fixes flags for aim % 360
        // int yawFixed = (int) (Managers.ROTATION.getYaw() / 360.0f);
        if (Modules.ROTATIONS.getMovementFix())
        {
            final double f = mc.options.getMouseSensitivity().getValue() * 0.6f + 0.2f;
            float gcd = (float) (f * f * f * 1.2f);
            float deltaYaw = yaw - Managers.ROTATION.getYaw();
            float deltaPitch = pitch - Managers.ROTATION.getPitch();
            float yaw1 = deltaYaw - (deltaYaw % gcd);
            float pitch1 = deltaPitch - (deltaPitch % gcd);
            yaw = Managers.ROTATION.getYaw() + yaw1;
            pitch = Managers.ROTATION.getPitch() + pitch1;
        }
        return new float[]
                {
                        yaw, MathHelper.wrapDegrees(pitch)
                };
    }
}
