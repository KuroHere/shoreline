package com.caspian.client.api.manager.player;

import com.caspian.client.util.Globals;
import net.minecraft.util.math.Vec3d;

public class MovementManager implements Globals
{
    /**
     *
     *
     * @param y
     */
    public void setMotionY(double y)
    {
        Vec3d motion = mc.player.getVelocity();
        mc.player.setVelocity(motion.getX(), y, motion.getZ());
    }
}
