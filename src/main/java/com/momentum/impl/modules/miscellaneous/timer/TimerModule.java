package com.momentum.impl.modules.miscellaneous.timer;

import com.momentum.api.feature.Option;
import com.momentum.api.feature.Service;
import com.momentum.api.module.Module;
import com.momentum.api.module.ModuleCategory;
import com.momentum.asm.mixins.vanilla.accessors.IMinecraft;
import com.momentum.asm.mixins.vanilla.accessors.ITimer;

import java.util.List;

/**
 * @author linus
 * @since 02/13/2023
 */
public class TimerModule extends Module implements Service<Float> {

    // tick speed option
    public final Option<Float> ticks =
            new Option<>("Ticks", "Tick speed", 0.1f, 2.0f, 50.0f);

    // listeners
    public final OptionUpdateListener optionUpdateListener =
            new OptionUpdateListener(this);

    public TimerModule() {
        super("Timer", "Changes client tick speed", ModuleCategory.MISCELLANEOUS);

        // options
        associate(
                ticks,
                bind,
                drawn
        );

        // listeners
        associate(
                optionUpdateListener
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // reset tick length
        ((ITimer) ((IMinecraft) mc).getTimer()).setTickLength(50.0f);
    }

    /**
     * Provides the service
     *
     * @param in The input
     */
    @Override
    public void provide(Float in) {

        // tick cannot be less than or equal 0
        if (in <= 0) {
            throw new IndexOutOfBoundsException();
        }

        // reset
        if (in == 1f) {
            disable();
            return;
        }

        // update tick length
        enable();
        ticks.setVal(in);
    }

    /**
     * Queues to preform at a later time
     *
     * @param in The input
     * @param q  The q to wait
     */
    @Override
    public void queue(Float in, List<Float> q) {

        // no impl
    }
}
