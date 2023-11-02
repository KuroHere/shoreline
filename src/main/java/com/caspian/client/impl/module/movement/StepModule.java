package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.entity.EntityPositionEvent;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;
import com.caspian.client.util.string.EnumFormatter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.Arrays;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class StepModule extends ToggleModule
{
    //
    Config<StepMode> modeConfig = new EnumConfig<>("Mode", "Step mode",
            StepMode.NORMAL, StepMode.values());
    Config<Float> heightConfig = new NumberConfig<>("Height", "The maximum " +
            "height for stepping up blocks", 1.0f, 2.5f, 10.0f);
    Config<Boolean> useTimerConfig = new BooleanConfig("UseTimer", "Slows " +
            "down packets by applying timer when stepping", true);
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "Confirms the " +
            "step height for NCP servers", false, () -> heightConfig.getValue() <= 2.5f);
    Config<Boolean> entityStepConfig = new BooleanConfig("EntityStep",
            "Allows entities to step up blocks", false);
    //
    private final Timer stepTimer = new CacheTimer();
    private boolean cancelTimer;

    /**
     *
     */
    public StepModule()
    {
        super("Step", "Allows the player to step up blocks",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        if (mc.player == null)
        {
            return;
        }
        setStepHeight(isAbstractHorse(mc.player.getVehicle()) ? 1.0f : 0.6f);
        Managers.TICK.setClientTick(1.0f);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onMovementPackets(MovementPacketsEvent event)
    {
        if (modeConfig.getValue() == StepMode.NORMAL)
        {
            double height = mc.player.getY() - mc.player.prevY;
            if (height <= 0.5 || height > heightConfig.getValue())
            {
                return;
            }
            //
            final List<Double> offs = getStepOffsets(height);
            if (useTimerConfig.getValue())
            {
                Managers.TICK.setClientTick(height > 1.0 ? 0.15f : 0.35f);
                cancelTimer = true;
            }
            for (int i = 0; i < (height > 1.0 ? offs.size() : 2); i++)
            {
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(),
                        mc.player.getY() + offs.get(i), mc.player.getZ(), false));
            }
            stepTimer.reset();
        }
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
        if (mc.player.isTouchingWater()
                || mc.player.isInLava()
                || mc.player.isFallFlying())
        {
            Managers.TICK.setClientTick(1.0f);
            setStepHeight(isAbstractHorse(mc.player.getVehicle()) ? 1.0f : 0.6f);
            return;
        }
        if (cancelTimer && mc.player.isOnGround())
        {
            Managers.TICK.setClientTick(1.0f);
            cancelTimer = false;
        }
        if (mc.player.isOnGround() && stepTimer.passed(200))
        {
            setStepHeight(heightConfig.getValue());
        }
        else
        {
            setStepHeight(isAbstractHorse(mc.player.getVehicle()) ? 1.0f : 0.6f);
        }
    }

    /**
     *
     * @param stepHeight
     */
    private void setStepHeight(float stepHeight)
    {
        if (entityStepConfig.getValue() && mc.player.getVehicle() != null)
        {
            mc.player.getVehicle().setStepHeight(stepHeight);
        }
        else
        {
            mc.player.setStepHeight(stepHeight);
        }
    }

    /**
     *
     * @param height The step height
     * @return
     */
    private List<Double> getStepOffsets(double height)
    {
        List<Double> packets = Arrays.asList(0.42,
                height < 1.0 && height > 0.8 ? 0.753 : 0.75, 1.0, 1.16, 1.23, 1.2);
        if (height >= 2.0)
        {
            packets = Arrays.asList(0.42, 0.78, 0.63, 0.51, 0.9,
                    1.21, 1.45, 1.43);
        }
        if (strictConfig.getValue())
        {
            packets.add(height);
        }
        return packets;
    }

    /**
     *
     * @param e
     * @return
     */
    private boolean isAbstractHorse(Entity e)
    {
        return e instanceof HorseEntity || e instanceof LlamaEntity
                || e instanceof MuleEntity;
    }

    public enum StepMode
    {
        VANILLA,
        NORMAL
    }
}
