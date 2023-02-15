package com.momentum.impl.modules.movement.speed;

import com.momentum.api.feature.Option;
import com.momentum.api.module.Module;
import com.momentum.api.module.ModuleCategory;
import com.momentum.api.util.render.Formatter;

/**
 * @author linus
 * @since 02/13/2023
 */
public class SpeedModule extends Module {

    // speed options
    public final Option<SpeedMode> mode =
            new Option<>("Mode", "Mode for speed", SpeedMode.STRAFE);
    public final Option<Boolean> useTimer =
            new Option<>("UseTimer", "Use Timer to speed", true);
    public final Option<Boolean> speedInWater =
            new Option<>("SpeedInWater", "Functional in liquids", false);

    // listeners
    public final UpdateListener updateListener =
            new UpdateListener(this);
    public final MoveListener moveListener =
            new MoveListener(this);

    // movement info
    public double speed;
    public double distance;
    public boolean accelerate;
    public int timeout;

    // stages
    public int strafeStage = 4;

    /**
     * Module containing all speed features
     */
    public SpeedModule() {
        super("Speed", "Move faster", ModuleCategory.MOVEMENT);

        // options
        associate(
                mode,
                useTimer,
                speedInWater,
                bind,
                drawn
        );

        // listeners
        associate(
                updateListener,
                moveListener
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // reset
        speed = 0;
        distance = 0;
        strafeStage = 4;
    }

    @Override
    public String getData() {

        // speed mode
        return Formatter.formatEnum(mode.getVal());
    }
}
