package net.shoreline.client.api.manager.player.rotation;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.RotationModule;
import net.shoreline.client.api.render.Interpolation;
import net.shoreline.client.impl.event.entity.UpdateVelocityEvent;
import net.shoreline.client.impl.event.entity.player.PlayerJumpEvent;
import net.shoreline.client.impl.event.keyboard.KeyboardTickEvent;
import net.shoreline.client.impl.event.network.MovementPacketsEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.impl.event.render.entity.RenderPlayerEvent;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.Globals;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linus, bon55
 * @since 1.0
 */
public class RotationManager implements Globals {
    private static final Map<String, Integer> ROTATE_PRIORITY = new HashMap<>();
    private final List<RotationRequest> requests = new ArrayList<>();
    //
    private float yaw, pitch, lastYaw, lastPitch, prevJumpYaw, prevYaw, prevPitch;
    boolean rotate;
    //
    private RotationRequest rotation;
    private RotationModule rotateModule;
    private int rotateTicks;

    /**
     *
     */
    public RotationManager() {
        Shoreline.EVENT_HANDLER.subscribe(this);
        //
        ROTATE_PRIORITY.put("Surround", 1000);
        ROTATE_PRIORITY.put("Speedmine", 950);
        ROTATE_PRIORITY.put("AutoCrystal", 900);
        ROTATE_PRIORITY.put("Aura", 800);
        // AntiAim should always have lowest prio?
        ROTATE_PRIORITY.put("AntiAim", 50);
    }

    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet && packet.changesLook()) {
            float packetYaw = packet.getYaw(0.0f);
            float packetPitch = packet.getPitch(0.0f);
            yaw = packetYaw;
            pitch = packetPitch;
        }
    }

    /*
        priority -1 not worked
        @EventHandler(priority = -1)
    */
    public void onUpdate() {
        if (requests.isEmpty()) {
            rotation = null;
            rotateModule = null;
            return;
        }
        RotationRequest request = getRotationRequest();
        if (request == null) {
            if (isDoneRotating()) {
                rotation = null;
                rotateModule = null;
                return;
            }
        } else {
            rotation = request;
            rotateModule = rotation.getModule();
        }
        // fixes flags for aim % 360
        // GCD implementation maybe?
        if (rotation == null) {
            return;
        }
        float serverYawChange = rotation.getYaw() - getWrappedYaw();
        float serverPitchChange = rotation.getPitch() - getPitch();
        float yaw1 = yaw + serverYawChange;
        float pitch1 = pitch + serverPitchChange;
        rotateTicks = 0;
        lastYaw = yaw1;
        lastPitch = pitch1;
        rotate = true;
    }

    @EventListener
    public void onMovementPackets(MovementPacketsEvent event) {
        if (rotate) {
            removeRotation(rotation.getModule());
            event.cancel();
            event.setYaw(lastYaw);
            event.setPitch(lastPitch);
            rotate = false;
        }
    }

    @Nullable
    private RotationRequest getRotationRequest() {
        if (requests.isEmpty()) {
            return null;
        }
        RotationRequest rotationRequest = null;
        int priority = 0;
        long time = 0;
        for (RotationRequest request : requests) {
            if (!request.getModule().isEnabled()) {
                continue;
            }
            if (request.getPriority() > priority ||
                    request.getPriority() == priority && request.getTime() > time) {
                rotationRequest = request;
                priority = request.getPriority();
                time = request.getTime();
            }
        }
        return rotationRequest;
    }

    @EventListener
    public void onKeyboardTick(KeyboardTickEvent event) {
        if (rotation != null && mc.player != null
                && Modules.ROTATIONS.getMovementFix()) {
            float forward = mc.player.input.movementForward;
            float sideways = mc.player.input.movementSideways;
            float delta = (mc.player.getYaw() - rotation.getYaw()) * MathHelper.RADIANS_PER_DEGREE;
            float cos = MathHelper.cos(delta);
            float sin = MathHelper.sin(delta);
            mc.player.input.movementSideways = Math.round(sideways * cos - forward * sin);
            mc.player.input.movementForward = Math.round(forward * cos + sideways * sin);
        }
    }

    @EventListener
    public void onUpdateVelocity(UpdateVelocityEvent event) {
        if (rotation != null && Modules.ROTATIONS.getMovementFix()) {
            event.cancel();
            event.setVelocity(movementInputToVelocity(rotation.getYaw(), event.getMovementInput(), event.getSpeed()));
        }
    }

    @EventListener
    public void onPlayerJump(PlayerJumpEvent event) {
        if (rotation != null && Modules.ROTATIONS.getMovementFix()) {
            if (event.getStage() == EventStage.PRE) {
                prevJumpYaw = mc.player.getYaw();
                mc.player.setYaw(rotation.getYaw());
            } else {
                mc.player.setYaw(prevJumpYaw);
            }
        }
    }

    @EventListener
    public void onRenderPlayer(RenderPlayerEvent event) {
        if (event.getEntity() == mc.player && rotation != null) {
            // Match packet server rotations
            event.setYaw(Interpolation.interpolateFloat(prevYaw, getYaw(), mc.getTickDelta()));
            event.setPitch(Interpolation.interpolateFloat(prevPitch, getPitch(), mc.getTickDelta()));
            prevYaw = event.getYaw();
            prevPitch = event.getPitch();
            event.cancel();
        }
    }

    /**
     * @param requester
     * @param yaw
     * @param pitch
     */
    public void setRotation(RotationModule requester, float yaw, float pitch) {
        for (RotationRequest r : requests) {
            if (requester == r.getModule()) {
                // r.setPriority();
                r.setTime(System.currentTimeMillis());
                r.setYaw(yaw);
                r.setPitch(pitch);
                return;
            }
        }
        requests.add(new RotationRequest(requester,
                ROTATE_PRIORITY.getOrDefault(requester.getName(), 100), yaw, pitch));
    }

    /**
     * @param request
     */
    public boolean removeRotation(RotationRequest request) {
        return requests.remove(request);
    }

    /**
     * @param requester
     */
    public void removeRotation(RotationModule requester) {
        requests.removeIf(r -> requester == r.getModule());
    }

    /**
     * @param yaw
     * @param pitch
     */
    public void setRotationClient(float yaw, float pitch) {
        if (mc.player == null) {
            return;
        }
        mc.player.setYaw(yaw);
        mc.player.setHeadYaw(yaw);
        mc.player.setBodyYaw(yaw);
        mc.player.setPitch(pitch);
    }

    /**
     * @return
     */
    public boolean isDoneRotating() {
        return rotateTicks > Modules.ROTATIONS.getPreserveTicks();
    }

    public boolean isRotating() {
        return rotation != null;
    }

    public float getRotationYaw() {
        return rotation.getYaw();
    }

    public float getRotationPitch() {
        return rotation.getPitch();
    }

    /**
     * @return
     */
    public RotationModule getRotatingModule() {
        return rotateModule;
    }

    /**
     * @return
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * @return
     */
    public float getWrappedYaw() {
        return MathHelper.wrapDegrees(yaw);
    }

    /**
     * @return
     */
    public float getPitch() {
        return pitch;
    }

    //
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
