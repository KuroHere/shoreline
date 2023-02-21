package com.momentum.impl.modules.movement.step;

import com.momentum.api.feature.Option;
import com.momentum.api.module.Module;
import com.momentum.api.module.ModuleCategory;
import com.momentum.api.util.render.Formatter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityPig;

/**
 * @author linus
 * @since 02/20/2023
 */
public class StepModule extends Module {

    // step options
    public final Option<StepMode> modeOption =
            new Option<>("Mode", "Bypass mode", StepMode.NORMAL);
    public final Option<Boolean> useTimerOption =
            new Option<>("UseTimer", "Uses timer to slow down packets", true);
    public final Option<Boolean> entityStepOption =
            new Option<>("EntityStep", "Applies step height to entities", false);
    public final Option<Float> heightOption =
            new Option<>("Height", "Maximum step height", 1.0f, 1.0f, 2.5f);

    // listeners
    public final StepListener stepListener =
            new StepListener(this);
    public final UpdateListener updateListener =
            new UpdateListener(this);

    // timer info
    protected boolean timer;

    public StepModule() {
        super("Step", "Allows you to step higher", ModuleCategory.MOVEMENT);

        // options
        associate(
                modeOption,
                useTimerOption,
                entityStepOption,
                heightOption,
                bind,
                drawn
        );

        // listeners
        associate(
                stepListener,
                updateListener
        );
    }

    /**
     * Checks if the given entity is an abstract horse
     *
     * @param entity The entity
     * @return Whether the given entity is an abstract horse
     */
    protected boolean isAbstractHorse(Entity entity) {
        return entity instanceof EntityHorse || entity instanceof EntityLlama || entity instanceof EntityMule || entity instanceof EntityPig && entity.isBeingRidden() && ((EntityPig) entity).canBeSteered();
    }

    @Override
    public String getData() {

        // formatted mode
        return Formatter.formatEnum(modeOption.getVal());
    }
}
