package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.imixin.IMinecraftClient;
import com.caspian.client.init.Managers;
import com.caspian.client.mixin.accessor.AccessorMinecraftClient;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TriggerModule extends ToggleModule
{
    //
    Config<TriggerMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "activating the trigger bot", TriggerMode.MOUSE_BUTTON, TriggerMode.values());
    Config<Float> attackSpeedConfig = new NumberConfig<>("AttackSpeed", "The " +
            "speed to attack entities", 0.1f, 8.0f, 20.0f);
    Config<Float> randomSpeedConfig = new NumberConfig<>("RandomSpeed", "The " +
            "speed randomizer for attacks", 0.1f, 2.0f, 10.0f);
    //
    private final Timer triggerTimer = new CacheTimer();

    /**
     *
     */
    public TriggerModule()
    {
        super("Trigger", "Automatically attacks entities in the crosshair",
                ModuleCategory.COMBAT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
        boolean buttonDown = switch (modeConfig.getValue())
                {
                    case MOUSE_BUTTON -> mc.mouse.wasLeftButtonClicked();
                    case MOUSE_OVER ->
                    {
                        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY)
                        {
                            yield false;
                        }
                        EntityHitResult entityHit = (EntityHitResult) mc.crosshairTarget;
                        final Entity crosshairEntity = entityHit.getEntity();
                        if (mc.player.isTeammate(crosshairEntity)
                                || Managers.SOCIAL.isFriend(crosshairEntity.getUuid()))
                        {
                            yield false;
                        }
                        yield true;
                    }
                    case MOUSE_CLICK -> true;
                };
        double d = Math.random() * randomSpeedConfig.getValue() * 2.0 - randomSpeedConfig.getValue();
        if (buttonDown && triggerTimer.passed(Math.max(attackSpeedConfig.getValue() + d, 0.5) * 50))
        {
            ((IMinecraftClient) mc).leftClick();
            ((AccessorMinecraftClient) mc).hookSetAttackCooldown(0);
            triggerTimer.reset();
        }
    }

    public enum TriggerMode
    {
        MOUSE_BUTTON,
        MOUSE_OVER,
        MOUSE_CLICK
    }
}
