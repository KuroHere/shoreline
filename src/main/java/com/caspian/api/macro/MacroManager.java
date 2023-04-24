package com.caspian.api.macro;

import com.caspian.Caspian;

import java.util.Collection;

public class MacroManager
{


    public MacroManager()
    {
        Caspian.EVENT_HANDLER.subscribe(MacroHandler.class);
    }

    public Collection<Macro> getMacros()
    {
        return null;
    }
}
