package com.caspian.client.api.manager.player;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.Globals;
import com.caspian.client.util.world.VecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
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
    private double x, y, z;
    private BlockPos blockPos;
    //
    private boolean sneaking, sprinting;
    //
    private boolean onGround;

    /**
     *
     *
     */
    public PositionManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
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
        if (mc.player.isRiding())
        {
            mc.player.getVehicle().setPosition(x, y, z);
            return;
        }
        Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                x, y, z, isOnGround()));
        mc.player.setPosition(x, y, z);
    }

    /**
     *
     * @param x
     * @param z
     */
    public void setPositionXZ(double x, double z)
    {
        setPosition(x, y, z);
    }

    /**
     *
     * @param y
     */
    public void setPositionY(double y)
    {
        setPosition(x, y, z);
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

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerMoveC2SPacket packet)
            {
                onGround = packet.isOnGround();
                if (packet.changesPosition())
                {
                    x = packet.getX(x);
                    y = packet.getY(y);
                    z = packet.getZ(z);
                    int i = MathHelper.floor(x);
                    int j = MathHelper.floor(y);
                    int k = MathHelper.floor(z);
                    blockPos = new BlockPos(i, j, k);
                }
            }
            else if (event.getPacket() instanceof ClientCommandC2SPacket packet)
            {
                switch (packet.getMode())
                {
                    case START_SPRINTING -> sprinting = true;
                    case STOP_SPRINTING -> sprinting = false;
                    case PRESS_SHIFT_KEY -> sneaking = true;
                    case RELEASE_SHIFT_KEY -> sneaking = false;
                }
            }
        }
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    /**
     *
     *
     * @return
     */
    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    /**
     *
     *
     * @return
     */
    public boolean isSneaking()
    {
        return sneaking;
    }

    /**
     *
     *
     * @return
     */
    public boolean isSprinting()
    {
        return sprinting;
    }

    /**
     *
     * @return
     */
    public boolean isOnGround()
    {
        return onGround;
    }
}
