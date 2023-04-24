package com.caspian.impl.event.chat;

import com.caspian.api.event.Cancelable;
import com.caspian.api.event.Event;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Cancelable
public class ChatMessageEvent extends Event
{
    //
    private final String message;

    public ChatMessageEvent(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public static class Client extends ChatMessageEvent
    {
        public Client(String message)
        {
            super(message);
        }
    }

    public static class Server extends ChatMessageEvent
    {
        public Server(String message)
        {
            super(message);
        }
    }
}
