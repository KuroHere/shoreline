package com.caspian.client.api.module;

import com.caspian.client.api.manager.player.rotation.RotationPriority;
import com.caspian.client.init.Managers;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class RotationModule extends ToggleModule
{
    /**
     * @param name     The module unique identifier
     * @param desc     The module description
     * @param category The module category
     */
    public RotationModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);
    }

    /**
     *
     *
     * @param yaw
     * @param pitch
     */
    protected void setRotation(float yaw, float pitch)
    {
        setRotation(RotationPriority.NORMAL, yaw, pitch);
    }

    /**
     *
     *
     * @param priority
     * @param yaw
     * @param pitch
     */
    protected void setRotation(RotationPriority priority, float yaw, float pitch)
    {
        Managers.ROTATION.setRotation(this, priority, yaw, pitch);
    }

    /**
     *
     *
     * @return
     */
    public boolean isRotationBlocked()
    {
        RotationModule head = Managers.ROTATION.getRotatingModule();
        if (head != null)
        {
            return head != this;
        }
        return false;
    }
}
