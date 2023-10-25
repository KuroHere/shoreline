package com.caspian.client.impl.event.text;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

/**
 *
 *
 * @see com.caspian.client.mixin.text.MixinTextVisitFactory
 */
@Cancelable
public class TextVisitEvent extends Event
{
    //
    private String text;

    /**
     *
     * @param text
     */
    public TextVisitEvent(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
