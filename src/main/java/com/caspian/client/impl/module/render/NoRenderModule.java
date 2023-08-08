package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.particle.ParticleEvent;
import net.minecraft.particle.ParticleTypes;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoRenderModule extends ToggleModule
{
    //
    Config<Boolean> explosionsConfig = new BooleanConfig("Explosions",
            "Prevents explosion particles from rendering", true);

    /**
     *
     */
    public NoRenderModule()
    {
        super("NoRender", "Prevents certain game elements from rendering",
                ModuleCategory.RENDER);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onParticle(ParticleEvent event)
    {
        if (explosionsConfig.getValue() && (event.getParticleType() == ParticleTypes.EXPLOSION
                || event.getParticleType() == ParticleTypes.EXPLOSION_EMITTER))
        {
            event.cancel();
        }
    }
}
