package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.handler.PositionHandler;
import com.caspian.client.init.Managers;
import com.caspian.client.util.Globals;
import com.caspian.client.util.world.VecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PositionManager implements Globals
{
    //
    private final PositionHandler handler;

    /**
     *
     *
     */
    public PositionManager()
    {
        handler = new PositionHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(double x, double y, double z)
    {
        Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x, y, z, isOnGround()));
        setPositionClient(x, y, z);
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     */
    public void setPositionClient(double x, double y, double z)
    {
        if (mc.player != null && mc.world != null)
        {
            mc.player.setPosition(x, y, z);
        }
    }

    /**
     *
     *
     * @return
     */
    public Vec3d getPos()
    {
        return new Vec3d(getX(), getY(), getZ());
    }

    /**
     *
     *
     * @return
     */
    public BlockPos getBlockPos()
    {
        return handler.getBlockPos();
    }

    /**
     *
     *
     * @return
     */
    public Vec3d getEyePos()
    {
        return VecUtil.toEyePos(mc.player, getPos());
    }

    /**
     *
     *
     * @param tickDelta
     * @return
     */
    public final Vec3d getCameraPosVec(float tickDelta)
    {
        double d = MathHelper.lerp(tickDelta, mc.player.prevX, getX());
        double e = MathHelper.lerp(tickDelta, mc.player.prevY, getY())
                + (double) mc.player.getStandingEyeHeight();
        double f = MathHelper.lerp(tickDelta, mc.player.prevZ, getZ());
        return new Vec3d(d, e, f);
    }

    /**
     *
     *
     * @param entity
     * @return
     */
    public double squaredDistanceTo(Entity entity)
    {
        float f = (float) (getX() - entity.getX());
        float g = (float) (getY() - entity.getY());
        float h = (float) (getZ() - entity.getZ());
        return MathHelper.squaredMagnitude(f, g, h);
    }

    /**
     *
     *
     * @param entity
     * @return
     */
    public double squaredReachDistanceTo(Entity entity)
    {
        Vec3d cam = getCameraPosVec(1.0f);
        float f = (float) (cam.getX() - entity.getX());
        float g = (float) (cam.getY() - entity.getY());
        float h = (float) (cam.getZ() - entity.getZ());
        return MathHelper.squaredMagnitude(f, g, h);
    }

    public double getX()
    {
        return handler.getX();
    }

    public double getY()
    {
        return handler.getY();
    }

    public double getZ()
    {
        return handler.getZ();
    }

    /**
     *
     *
     * @return
     */
    public boolean isSneaking()
    {
        return handler.isSneaking();
    }

    /**
     *
     *
     * @return
     */
    public boolean isSprinting()
    {
        return handler.isSprinting();
    }

    /**
     *
     * @return
     */
    public boolean isOnGround()
    {
        return handler.isOnGround();
    }
}
