package net.shoreline.client.impl.manager.player.rotation;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.render.Interpolation;
import net.shoreline.client.impl.event.entity.JumpRotationEvent;
import net.shoreline.client.impl.event.entity.UpdateVelocityEvent;
import net.shoreline.client.impl.event.keyboard.KeyboardTickEvent;
import net.shoreline.client.impl.event.network.MovementPacketsEvent;
import net.shoreline.client.impl.event.network.PlayerUpdateEvent;
import net.shoreline.client.impl.event.render.entity.RenderPlayerEvent;
import net.shoreline.client.impl.imixin.IClientPlayerEntity;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.Globals;
import net.shoreline.client.util.chat.ChatUtil;

public final class RotationManager implements Globals
{
    private Rotation rotation;
    private int rotationTicks;
    private float[] server, lastServer;

    public RotationManager()
    {
        Shoreline.EVENT_HANDLER.subscribe(this);
        server = new float[2];
        lastServer = new float[2];
    }

    @EventListener
    public void onMovementPackets(final MovementPacketsEvent event)
    {
        if (rotation != null && rotationTicks-- > 0)
        {
            event.setYaw(rotation.yaw());
            event.setPitch(rotation.pitch());
            event.cancel();

            if (rotationTicks <= 0)
            {
                rotation = null;
            }
        }
    }

    @EventListener
    public void onPlayerUpdate(final PlayerUpdateEvent event)
    {
        if (event.getStage() == EventStage.POST)
        {
            if (mc.player.age < 5)
            {
                rotationTicks = 0;
                rotation = null;
                return;
            }

            final IClientPlayerEntity entity = (IClientPlayerEntity) mc.player;
            if (entity == null)
            {
                return;
            }
            lastServer[0] = server[0];
            lastServer[1] = server[1];
            server[0] = entity.getLastSpoofedYaw();
            server[1] = entity.getLastSpoofedPitch();
        }
    }

    @EventListener
    public void onKeyboardTick(final KeyboardTickEvent event)
    {
        if (rotation != null && mc.player != null && Modules.ROTATIONS.getMovementFix())
        {
            float forward = mc.player.input.movementForward;
            float sideways = mc.player.input.movementSideways;
            float delta = (mc.player.getYaw() - getServerYaw()) * MathHelper.RADIANS_PER_DEGREE;
            float cos = MathHelper.cos(delta);
            float sin = MathHelper.sin(delta);
            mc.player.input.movementSideways = Math.round(sideways * cos - forward * sin);
            mc.player.input.movementForward = Math.round(forward * cos + sideways * sin);
        }
    }

    @EventListener
    public void onUpdateVelocity(final UpdateVelocityEvent event)
    {
        if (rotation != null && Modules.ROTATIONS.getMovementFix())
        {
            event.cancel();
            event.setVelocity(movementInputToVelocity(getServerYaw(), event.getMovementInput(), event.getSpeed()));
        }
    }

    @EventListener
    public void onJumpRotation(final JumpRotationEvent event)
    {
        if (rotation != null && Modules.ROTATIONS.getMovementFix())
        {
            event.cancel();
            event.setYaw(getServerYaw());
        }
    }

    @EventListener
    public void onRenderPlayer(final RenderPlayerEvent event)
    {
        if (event.getEntity() == mc.player && rotation != null)
        {
            event.setYaw(Interpolation.interpolateFloat(lastServer[0], server[0], mc.getTickDelta()));
            event.setPitch(Interpolation.interpolateFloat(lastServer[1], server[1], mc.getTickDelta()));
            event.cancel();
        }
    }

    public void submit(final Rotation submission)
    {
        if ((rotation == null || submission.priority() >= rotation.priority()) && submission.time() >= 1)
        {
            rotation = submission;
            rotationTicks = submission.time();
        }
    }

    public void submitClient(final float yaw, final float pitch)
    {
        if (mc.player != null)
        {
            mc.player.setYaw(yaw);
            mc.player.setPitch(pitch);
        }
    }

    public void submitInstant(final float yaw, final float pitch, final boolean grim)
    {
        if (grim)
        {
            Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.Full(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ(), yaw, pitch, mc.player.isOnGround()));
            Managers.NETWORK.sendSequencedPacket((s) ->
                    new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, s));
        }
        else
        {
            Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, mc.player.isOnGround()));
        }
    }

    public boolean isRotationBlocked(final int priority)
    {
        return rotation != null && rotation.priority() < priority;
    }

    public float getServerYaw() {
        return server[0];
    }

    public float getLastServerYaw() {
        return lastServer[0];
    }

    public float getWrappedYaw() {
        return MathHelper.wrapDegrees(server[0]);
    }

    public float getServerPitch() {
        return server[1];
    }

    public float getLastServerPitch() {
        return lastServer[1];
    }

    public float[] getServer() {
        return server;
    }

    public boolean isRotating()
    {
        return rotation != null && rotationTicks > 0;
    }

    public boolean isRotated()
    {
        return rotation.yaw() == server[0] && rotation.pitch() == server[1];
    }

    private Vec3d movementInputToVelocity(float yaw, Vec3d movementInput, float speed) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
        float f = MathHelper.sin(yaw * MathHelper.RADIANS_PER_DEGREE);
        float g = MathHelper.cos(yaw * MathHelper.RADIANS_PER_DEGREE);
        return new Vec3d(vec3d.x * (double) g - vec3d.z * (double) f, vec3d.y, vec3d.z * (double) g + vec3d.x * (double) f);
    }
}