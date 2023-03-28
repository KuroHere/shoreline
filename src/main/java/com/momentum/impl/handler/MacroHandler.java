package com.momentum.impl.handler;

import com.momentum.Momentum;
import com.momentum.api.config.Macro;
import com.momentum.api.event.Event;
import com.momentum.api.event.Listener;
import com.momentum.api.module.Module;
import com.momentum.api.module.modules.BindModule;
import com.momentum.impl.event.TickKeyboardEvent;
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

    // TODO: This is temporary until I figure out how I want to do managers
    public MacroHandler()
    {
        Momentum.EVENT_HANDLER.subscribe(new Listener<TickKeyboardEvent>()
        {
            /**
             * Calls the listener for the {@link Event}
             * event
             *
             * @param event The event
             */
            @Override
            public void invoke(TickKeyboardEvent event)
            {
                runTickKeyboard();
            }
        });
    }

    /**
     * Runs on the {@link TickKeyboardEvent}
     */
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
