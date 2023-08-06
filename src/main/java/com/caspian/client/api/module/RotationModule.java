package com.caspian.client.api.module;

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
