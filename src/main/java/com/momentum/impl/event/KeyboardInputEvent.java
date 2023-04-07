package com.momentum.impl.event;

import com.momentum.api.event.Event;
import com.momentum.asm.mixin.MixinKeyboard;

/**
 * Called during {@link net.minecraft.client.Keyboard#onKey(long, int, int, int, int)}
 *
 * @author linus
 * @since 1.0
 *
 * @see MixinKeyboard
 */
public class KeyboardInputEvent extends Event
{
    // input keycode
    private final int keycode;

    /**
     * Initializes a new KeyboardInputEvent with the inputted GLFW keycode
     *
     * @param keycode The input keycode
     */
    public KeyboardInputEvent(int keycode)
    {
        this.keycode = keycode;
    }

    /**
     * Gets this event's cancelable state
     *
     * @return The cancelable state
     */
    @Override
    public boolean isCancelable()
    {
        return true;
    }

    /**
     * Returns the inputted keycode
     *
     * @return The input keycode
     */
    public int getKeycode()
    {
        return keycode;
    }
}
