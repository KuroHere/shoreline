package net.shoreline.client.impl.module.combat;

import net.shoreline.client.api.module.ObsidianPlacerModule;
import net.shoreline.client.api.module.ModuleCategory;

/**
 * @author linus
 * @since 1.0
 */
public class AutoTrapModule extends ObsidianPlacerModule {

    /**
     *
     */
    public AutoTrapModule() {
        super("AutoTrap", "Automatically traps nearby players in blocks",
                ModuleCategory.COMBAT);
    }
}
