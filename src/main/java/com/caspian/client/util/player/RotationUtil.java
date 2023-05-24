package com.caspian.client.util.player;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 *
 */
public class RotationUtil
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
        return new float[]
                {
                        MathHelper.wrapDegrees(yaw),
                        MathHelper.wrapDegrees(pitch)
                };
    }
}
