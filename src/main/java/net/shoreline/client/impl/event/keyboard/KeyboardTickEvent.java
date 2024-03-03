package net.shoreline.client.impl.event.keyboard;

import net.minecraft.client.input.KeyboardInput;
import net.shoreline.client.api.event.Cancelable;
import net.shoreline.client.api.event.Event;

@Cancelable
public class KeyboardTickEvent extends Event
{
    public final KeyboardInput input;

    public KeyboardTickEvent(KeyboardInput input)
    {
        this.input = input;
    }
}
