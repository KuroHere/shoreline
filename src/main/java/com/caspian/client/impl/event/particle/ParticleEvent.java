package com.caspian.client.impl.event.particle;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Cancelable
public class ParticleEvent extends Event
{
    //
    private final ParticleEffect particle;

    /**
     *
     *
     * @param particle
     */
    public ParticleEvent(ParticleEffect particle)
    {
        this.particle = particle;
    }

    /**
     *
     *
     * @return
     */
    public ParticleEffect getParticle()
    {
        return particle;
    }

    /**
     *
     *
     * @return
     */
    public ParticleType<?> getParticleType()
    {
        return particle.getType();
    }
}
