package com.caspian.client.impl.event.render;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see com.caspian.client.mixin.render.MixinLightmapTextureManager
 */
@Cancelable
public class LightmapGammaEvent extends Event
{
    //
    private int gamma;

    /**
     *
     *
     * @param gamma
     */
    public LightmapGammaEvent(int gamma)
    {
        this.gamma = gamma;
    }

    public int getGamma()
    {
        return gamma;
    }

    public void setGamma(int gamma)
    {
        this.gamma = gamma;
    }
}
