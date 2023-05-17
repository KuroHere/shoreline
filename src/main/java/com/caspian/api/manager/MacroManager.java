package com.caspian.api.manager;

import com.caspian.Caspian;
import com.caspian.api.macro.Macro;
import com.caspian.api.handler.MacroHandler;

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
public class MacroManager
{
    //
    private final Set<Macro> macros = new HashSet<>();

    /**
     *
     *
     */
    public MacroManager()
    {
        Caspian.EVENT_HANDLER.subscribe(MacroHandler.class);
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
