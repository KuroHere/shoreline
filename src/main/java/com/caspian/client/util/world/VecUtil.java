package com.caspian.client.util.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec2f;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Vec3d
 * @see Vec2f
 */
public class VecUtil
{
    /**
     *
     *
     * @param entity
     * @param pos
     * @return
     */
    public static Vec3d toEyePos(final Entity entity,
                                 final Vec3d pos)
    {
        return pos.add(0.0, entity.getStandingEyeHeight(), 0.0);
    }
}
