package com.caspian.client.api.render;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Interpolation
{
    /**
     * Gets the interpolated {@link Vec3d} position of an entity (i.e. position
     * based on render ticks)
     *
     * @param entity The entity to get the position for
     * @param tickDelta The render time
     * @return The interpolated vector of an entity
     */
    public static Vec3d getInterpolatedPosition(Entity entity, float tickDelta)
    {
        return new Vec3d(entity.getX() - MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()),
            entity.getY() - MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()),
            entity.getZ() - MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()));
    }
}
