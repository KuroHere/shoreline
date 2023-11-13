package com.caspian.client.impl.module.world;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoTunnelModule extends ToggleModule
{
    /**
     *
     */
    public AutoTunnelModule()
    {
        super("AutoTunnel", "Automatically mines a tunnel", ModuleCategory.WORLD);
    }
}
