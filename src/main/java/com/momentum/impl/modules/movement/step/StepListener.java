package com.momentum.impl.modules.movement.step;

import com.momentum.api.event.FeatureListener;
import com.momentum.impl.events.vanilla.entity.StepEvent;
import com.momentum.impl.init.Modules;
import net.minecraft.network.play.client.CPacketPlayer;

/**
 * @author linus
 * @since 02/20/2023
 */
public class StepListener extends FeatureListener<StepModule, StepEvent> {

    /**
     * Default constructor
     *
     * @param feature The associated feature
     */
    protected StepListener(StepModule feature) {
        super(feature);
    }

    @Override
    public void invoke(StepEvent event) {

        // NCP bypass
        if (feature.modeOption.getVal() == StepMode.NORMAL) {

            // current step
            double step = event.getAxisAlignedBB().minY - mc.player.posY;

            // check if current step is valid
            if (step > 0 && step <= feature.heightOption.getVal()) {

                // one block jump
                double[] one = new double[]{
                        0.42,
                        step < 1.0 && step > 0.8 ? 0.753 : 0.75,
                        1.0,
                        1.16,
                        1.23,
                        1.2
                };

                // two block jump
                double[] two = new double[]{
                        0.42,
                        0.78,
                        0.63,
                        0.51,
                        0.9,
                        1.21,
                        1.45,
                        1.43
                };

                // use timer to slow down motion
                if (feature.useTimerOption.getVal()) {

                    // update timer
                    Modules.TIMER_MODULE.provide(step > 1.0 ? 0.15f : 0.35f);
                    feature.timer = true;
                }

                // send our NCP offsets
                for (double off : step < 2 ? one : two) {

                    // send position packet
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + off, mc.player.posZ, false));
                }
            }
        }
    }
}
