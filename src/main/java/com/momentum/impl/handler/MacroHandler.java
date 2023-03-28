package com.momentum.impl.handler;

import com.momentum.api.config.Macro;
import com.momentum.api.module.Module;
import com.momentum.api.module.modules.BindModule;
import com.momentum.init.Handlers;
import org.lwjgl.input.Keyboard;

/**
 * Handles {@link Macro} keybinding implementation
 *
 * @author linus
 * @since 03/25/2023
 */
public class MacroHandler
{

    @Deprecated
    public void runTickKeyboard()
    {
        // run on all module keybinding
        for (Module mod : Handlers.MODULE.getModules())
        {
            // check toggleable
            if (mod instanceof BindModule)
            {
                // check keybinding pressed
                Macro keybinding = ((BindModule) mod).getKeybinding();
                if (Keyboard.isKeyDown(keybinding.getKeycode())
                        && !Keyboard.isKeyDown(Keyboard.KEY_NONE))
                {
                    // toggle module
                    keybinding.invoke();
                }
            }
        }
    }
}
