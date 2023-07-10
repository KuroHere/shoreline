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
    private float gamma;

    /**
     *
     *
     * @param gamma
     */
    public LightmapGammaEvent(float gamma)
    {
        this.gamma = gamma;
    }

    public float getGamma()
    {
        return gamma;
    }

    public void setGamma(float gamma)
    {
        this.gamma = gamma;
    }
}
