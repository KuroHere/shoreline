package com.caspian.client.util.math.position;

import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

/**
 *
 */
public class TimedVec3d implements Position
{
    //
    private final Vec3d pos;
    //
    private final long time;

    /**
     *
     * @param pos
     * @param time
     */
    public TimedVec3d(Vec3d pos, long time)
    {
        this.pos = pos;
        this.time = time;
    }

    public Vec3d getPos()
    {
        return pos;
    }

    /**
     * Returns the X coordinate.
     */
    @Override
    public double getX()
    {
        return pos.getX();
    }

    /**
     * Returns the Y coordinate.
     */
    @Override
    public double getY()
    {
        return pos.getY();
    }

    /**
     * Returns the Z coordinate.
     */
    @Override
    public double getZ()
    {
        return pos.getZ();
    }

    public long getTime()
    {
        return time;
    }
}
