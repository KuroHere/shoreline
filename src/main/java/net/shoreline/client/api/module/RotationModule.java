package net.shoreline.client.api.module;

import net.shoreline.client.impl.manager.player.rotation.Rotation;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;

/**
 * @author linus
 * @see net.shoreline.client.impl.manager.player.rotation.RotationManager
 * @since 1.0
 */
public class RotationModule extends ToggleModule {

    private int rotationPriority;
    private boolean hasSetPriority = false;

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
        setRotation(yaw, pitch, (int)Modules.ROTATIONS.getPreserveTicks());
    }

    protected void setRotation(float yaw, float pitch, int time)
    {
        Managers.ROTATION.submit(new Rotation(getRotationPriority(), time, yaw, pitch));
    }

    /**
     * Sets client look yaw and pitch
     * @param yaw
     * @param pitch
     */
    protected void setRotationClient(float yaw, float pitch) {
        Managers.ROTATION.submitClient(yaw, pitch);
    }

    protected void setRotationPriority(int rotationPriority) {
        this.rotationPriority = rotationPriority;
        hasSetPriority = true;
    }

    protected boolean isRotationBlocked() {
        return Managers.ROTATION.isRotationBlocked(getRotationPriority());
    }

    protected int getRotationPriority() {
        return hasSetPriority ? rotationPriority : 10;
    }
}
