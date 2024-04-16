package net.shoreline.client.impl.manager.client;

import net.shoreline.client.Shoreline;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.file.ConfigFile;
import net.shoreline.client.api.macro.Macro;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.keyboard.KeyboardInputEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.Globals;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author linus
 * @see Macro
 * @since 1.0
 */
public class MacroManager implements Globals {
    // For handling macros
    //
    private final Set<Macro> macros = new HashSet<>();

    /**
     *
     */
    public MacroManager() {
        Shoreline.EVENT_HANDLER.subscribe(this);
    }

    /**
     * @param event
     */
    @EventListener
    public void onKeyboardInput(KeyboardInputEvent event) {
        if (mc.player == null || mc.world == null
                || mc.currentScreen != null) {
            return;
        }
        // module keybind impl
        //
        if (macros.isEmpty()) {
            return;
        }
        for (Macro macro : macros) {
            if (event.getKeycode() != GLFW.GLFW_KEY_UNKNOWN
                    && event.getKeycode() == macro.getKeycode()) {
                macro.runMacro();
            }
        }
    }

    /**
     * Loads custom macros from the
     * {@link ConfigFile} system
     */
    public void postInit() {
        // TODO
    }

    public void setMacro(Macro macro, int keycode) {
        Macro macro1 = getMacro(m -> m.getId().equals(macro.getId()));
        if (macro1 != null) {
            getMacro(m -> m.getId().equals(macro.getId())).setKeycode(keycode);
        }
    }

    /**
     * @param macros
     */
    public void register(Macro... macros) {
        for (Macro macro : macros) {
            register(macro);
        }
    }

    /**
     * @param macro
     */
    public void register(Macro macro) {
        macros.add(macro);
    }

    public Macro getMacro(Predicate<? super Macro> predicate) {
        return macros.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * @return
     */
    public Collection<Macro> getMacros() {
        return macros;
    }
}
