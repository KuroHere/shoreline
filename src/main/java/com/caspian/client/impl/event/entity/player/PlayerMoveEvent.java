package com.caspian.client.impl.event.entity.player;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 */
@Cancelable
public class PlayerMoveEvent extends Event
{
    //
    private final MovementType type;
    private double x, y, z;

    /**
     *
     *
     * @param type
     * @param movement
     */
    public PlayerMoveEvent(MovementType type, Vec3d movement)
    {
        this.type = type;
        this.x = movement.getX();
        this.y = movement.getY();
        this.z = movement.getZ();
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public MovementType getType()
    {
        return type;
    }

    public Vec3d getMovement()
    {
        return new Vec3d(x, y, z);
    }
}
