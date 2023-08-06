package com.caspian.client.api.manager;

import com.caspian.client.Caspian;
import com.caspian.client.api.macro.Macro;
import com.caspian.client.api.handler.MacroHandler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Macro
 */
public class MacroManager
{
    // The handler for handling macros
    private final MacroHandler handler;
    //
    private final Set<Macro> macros = new HashSet<>();

    /**
     *
     *
     */
    public MacroManager()
    {
        handler = new MacroHandler();
        Caspian.EVENT_HANDLER.subscribe(handler);
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
     * @param name
     * @param keycode
     */
    public void replaceKey(String name, int keycode)
    {
        Runnable temp = null;
        for (Macro m : macros)
        {
            final String id = m.getId();
            if (id.contains(name))
            {
                temp = m.macro();
                break;
            }
        }
        if (temp != null)
        {
            macros.removeIf(m -> m.getId().contains(name));
            macros.add(new Macro(name, keycode, temp));
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
