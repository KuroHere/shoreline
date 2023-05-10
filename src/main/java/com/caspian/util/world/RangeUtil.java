package com.caspian.util.world;

import com.caspian.util.Globals;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RangeUtil implements Globals
{
    /**
     *
     *
     * @param src
     * @param dest
     * @return
     */
    public static double distToCenter(Vec3d src, BlockPos dest)
    {
        return src.distanceTo(dest.toCenterPos());
    }
}
