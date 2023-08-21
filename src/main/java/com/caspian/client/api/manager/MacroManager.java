package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.macro.Macro;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.keyboard.KeyboardInputEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.Globals;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Macro
 */
public class MacroManager implements Globals
{
    // For handling macros
    //
    private final Set<Macro> macros = new HashSet<>();

    /**
     *
     *
     */
    public MacroManager()
    {
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onKeyboardInput(KeyboardInputEvent event)
    {
        if (mc.player == null || mc.world == null
                || mc.currentScreen != null)
        {
            return;
        }
        // module keybind impl
        for (Module module : Managers.MODULE.getModules())
        {
            if (module instanceof ToggleModule toggle)
            {
                final Macro keybind = toggle.getKeybinding();
                if (event.getKeycode() != GLFW.GLFW_KEY_UNKNOWN
                        && event.getKeycode() == keybind.keycode())
                {
                    keybind.runMacro();
                }
            }
        }
        //
        if (macros.isEmpty())
        {
            return;
        }
        for (Macro macro : macros)
        {
            if (event.getKeycode() != GLFW.GLFW_KEY_UNKNOWN
                    && event.getKeycode() == macro.keycode())
            {
                macro.runMacro();
            }
        }
    }

    /**
     * Loads custom macros from the
     * {@link com.caspian.client.api.file.ConfigFile} system
     */
    public void postInit()
    {
        // TODO
    }

    /**
     *
     *
     * @param macros
     */
    public void register(Macro... macros)
    {
        for (Macro macro : macros)
        {
            register(macro);
        }
    }

    /**
     *
     *
     * @param macro
     */
    public void register(Macro macro)
    {
        macros.add(macro);
    }

    /**
     *
     *
     * @return
     */
    public Collection<Macro> getMacros()
    {
        return macros;
    }
}
