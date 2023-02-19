package com.momentum.impl.modules.render.norender;

import com.momentum.api.event.FeatureListener;
import com.momentum.impl.events.vanilla.world.ExplosionEvent;

/**
 * @author linus
 * @since 02/15/2023
 */
public class ExplosionListener extends FeatureListener<NoRenderModule, ExplosionEvent> {

    /**
     * Default constructor
     *
     * @param feature The associated feature
     */
    protected ExplosionListener(NoRenderModule feature) {
        super(feature);
    }

    @Override
    public void invoke(ExplosionEvent event) {

        // cancel explosion rendering
        if (feature.explosionsOption.getVal()) {

            // remove particles
            event.setCanceled(true);
        }
    }
}
