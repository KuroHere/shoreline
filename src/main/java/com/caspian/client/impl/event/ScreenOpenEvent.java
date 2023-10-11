package com.caspian.client.impl.event;

import com.caspian.client.api.event.Event;
import net.minecraft.client.gui.screen.Screen;

/**
 *
 * @author linus
 * @since 1.0
 */
public class ScreenOpenEvent extends Event
{
    //
    private final Screen screen;

    public ScreenOpenEvent(Screen screen)
    {
        this.screen = screen;
    }

    public Screen getScreen()
    {
        return screen;
    }
}
