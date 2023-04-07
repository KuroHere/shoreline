package com.momentum.impl.handler;

import com.momentum.api.config.Macro;
import com.momentum.api.module.Module;
import com.momentum.api.module.modules.BindModule;
import com.momentum.impl.event.KeyboardInputEvent;
import com.momentum.init.Handlers;
import org.lwjgl.glfw.GLFW;

/**
 * Handles {@link Macro} keybinding implementation.
 *
 * @author linus
 * @since 1.0
 *
 * @see Macro
 */
public class MacroHandler
{
    /**
     *
     *
     * @param event
     */
    @Deprecated
    public void onKeyboardInput(KeyboardInputEvent event)
    {
        // run on all module keybinding
        for (Module mod : Handlers.MODULE.getModules())
        {
            // check toggleable
            if (mod instanceof BindModule)
            {
                // check keybinding pressed
                Macro keybinding = ((BindModule) mod).getKeybinding();
                if (event.getKeycode() != GLFW.GLFW_KEY_UNKNOWN
                        && event.getKeycode() == keybinding.getKeycode())
                {
                    // toggle module
                    keybinding.invoke();
                }
            }
        }
    }
}
