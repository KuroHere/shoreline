package com.caspian.client.impl.event.gui.hud;

import com.caspian.client.api.event.Event;
import net.minecraft.text.Text;

public class ChatMessageEvent extends Event
{
    private final Text text;

    public ChatMessageEvent(Text text)
    {
        this.text = text;
    }

    public Text getText()
    {
        return text;
    }
}
