package net.shoreline.client.impl.event.entity;

import net.minecraft.entity.LivingEntity;
import net.shoreline.client.api.event.Event;

public final class JumpRotationEvent extends Event {
    private final LivingEntity entity;
    private float yaw;

    public JumpRotationEvent(LivingEntity entity, float yaw) {
        this.entity = entity;
        this.yaw = yaw;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
