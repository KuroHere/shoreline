package net.shoreline.client.impl.module.movement;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.SprintCancelEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.player.MovementUtil;
import net.shoreline.client.util.string.EnumFormatter;
import net.minecraft.entity.effect.StatusEffects;
import net.shoreline.client.util.Globals;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SprintModule extends ToggleModule
{
    //
    Config<SprintMode> modeConfig = new EnumConfig<>("Mode",
            "Sprinting mode. Rage allows for multi-directional sprinting.",
            SprintMode.LEGIT, SprintMode.values());
    /**
     *
     */
    public SprintModule()
    {
        super("Sprint", "Automatically sprints", ModuleCategory.MOVEMENT);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getModuleData()
    {
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
        if (!Managers.POSITION.isSprinting()
                && !Managers.POSITION.isSneaking()
                && MovementUtil.isInputtingMovement()
                && !Globals.mc.player.isRiding()
                && !Globals.mc.player.isTouchingWater()
                && !Globals.mc.player.isInLava()
                && !Globals.mc.player.isHoldingOntoLadder()
                && !Globals.mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                && Globals.mc.player.getHungerManager().getFoodLevel() > 6.0F)
        {
            switch (modeConfig.getValue())
            {
                case LEGIT ->
                {
                    if (Globals.mc.player.input.hasForwardMovement()
                            && (!Globals.mc.player.horizontalCollision
                            || Globals.mc.player.collidedSoftly))
                    {
                        Globals.mc.player.setSprinting(true);
                    }
                }
                case RAGE -> Globals.mc.player.setSprinting(true);
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onSprintCancel(SprintCancelEvent event)
    {
        if (!Managers.POSITION.isSneaking()
                && MovementUtil.isInputtingMovement()
                && !Globals.mc.player.isRiding()
                && !Globals.mc.player.isTouchingWater()
                && !Globals.mc.player.isInLava()
                && !Globals.mc.player.isHoldingOntoLadder()
                && !Globals.mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                && Globals.mc.player.getHungerManager().getFoodLevel() > 6.0F
                && modeConfig.getValue() == SprintMode.RAGE)
        {
            event.cancel();
        }
    }

    public enum SprintMode
    {
        LEGIT,
        RAGE
    }
}
