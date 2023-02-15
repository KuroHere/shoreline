package com.momentum.impl.modules.movement.sprint;

import com.momentum.api.event.FeatureListener;
import com.momentum.asm.mixins.vanilla.accessors.IEntity;
import com.momentum.impl.events.vanilla.entity.UpdateEvent;

/**
 * @author linus
 * @since 02/12/2023
 */
public class UpdateListener extends FeatureListener<SprintModule, UpdateEvent> {
    protected UpdateListener(SprintModule feature) {
        super(feature);
    }

    @Override
    public void invoke(UpdateEvent event) {

        // reset state
        // sprint state
        boolean sprint = false;

        // check if player is moving
        if (mc.player.moveForward != 0 || mc.player.moveStrafing != 0) {

            // check hunger level, cannot sprint if hunger level is too low
            if (mc.player.getFoodStats().getFoodLevel() > 6) {

                // directional
                if (feature.mode.getVal() == SprintMode.RAGE) {

                    // always sprint
                    sprint = true;
                }

                // forward
                else {

                    // make sure player is moving forward
                    sprint = !mc.player.collidedHorizontally && mc.gameSettings.keyBindForward.isKeyDown();
                }
            }
        }

        // update sprint state
        ((IEntity) mc.player).setFlag(3, sprint);
    }
}
