package net.shoreline.client.api.module;

import net.shoreline.client.init.Managers;

/**
 * @author linus
 * @see net.shoreline.client.impl.manager.player.rotation.RotationManager
 * @since 1.0
 */
public class RotationModule extends ToggleModule {
    /**
     * @param name     The module unique identifier
     * @param desc     The module description
     * @param category The module category
     */
    public RotationModule(String name, String desc, ModuleCategory category) {
        super(name, desc, category);
    }

    /**
     * @param yaw
     * @param pitch
     */
    protected void setRotation(float yaw, float pitch) {
        Managers.ROTATION.setRotation(this, yaw, pitch);
    }

    /**
     * Sets client look yaw and pitch
     * @param yaw
     * @param pitch
     */
    protected void setRotationClient(float yaw, float pitch) {
        Managers.ROTATION.setRotationClient(yaw, pitch);
    }

    /**
     * @return
     */
    public boolean isRotationBlocked() {
        RotationModule head = Managers.ROTATION.getRotatingModule();
        if (head != null) {
            return head != this;
        }
        return false;
    }
}
