package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.NumberDisplay;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.entity.player.PushEntityEvent;
import com.caspian.client.impl.event.entity.player.PushFluidsEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.network.PushOutOfBlocksEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.mixin.accessor.AccessorEntityVelocityUpdateS2CPacket;
import com.caspian.client.mixin.accessor.AccessorExplosionS2CPacket;
import com.caspian.client.util.math.timer.Timer;
import com.caspian.client.util.string.EnumFormatter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author Gavin
 * @since 1.0
 */
public class VelocityModule extends ToggleModule
{
    Config<VelocityMode> modeConfig = new EnumConfig<>("Mode",
            "The anti-cheat bypass for velocity", VelocityMode.NORMAL,
            VelocityMode.values());
    Config<Float> horizontalConfig = new NumberConfig<>("Horizontal",
            "How much horizontal knock-back to take", 0.0f, 0.0f, 100.0f,
            NumberDisplay.PERCENT, () -> modeConfig.getValue() == VelocityMode.NORMAL);
    Config<Float> verticalConfig = new NumberConfig<>("Vertical",
            "How much vertical knock-back to take", 0.0f, 0.0f, 100.0f,
            NumberDisplay.PERCENT, () -> modeConfig.getValue() == VelocityMode.NORMAL);
    Config<Boolean> pushEntitiesConfig = new BooleanConfig("NoPush-Entities",
            "Prevents being pushed away from entities", true);
    Config<Boolean> pushBlocksConfig = new BooleanConfig("NoPush-Blocks",
            "Prevents being pushed out of blocks", true);
    Config<Boolean> pushLiquidsConfig = new BooleanConfig("NoPush-Liquids",
            "Prevents being pushed by flowing liquids", true);
    Config<Boolean> pushFishhookConfig = new BooleanConfig("NoPush-Fishhook",
            "Prevents being pulled by fishing rod hooks", true);
    //
    private boolean cancelVelocity;

    /**
     *
     */
    public VelocityModule()
    {
        super("Velocity", "Reduces the amount of player knockback velocity",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        if (modeConfig.getValue() == VelocityMode.NORMAL)
        {
            DecimalFormat decimal = new DecimalFormat("0.0");
            return String.format("H:%s%%, V:%s%%",
                    decimal.format(horizontalConfig.getValue()),
                    decimal.format(verticalConfig.getValue()));
        }
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     *
     * @param event
     */
    // @EventListener
    // public void onPacketOutbound(PacketEvent.Outbound event)
    // {
    //
    // }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet)
            {
                if (packet.getId() != mc.player.getId())
                {
                    return;
                }
                switch (modeConfig.getValue())
                {
                    case NORMAL ->
                    {
                        if (horizontalConfig.getValue() == 0.0f
                                && verticalConfig.getValue() == 0.0f)
                        {
                            event.cancel();
                            return;
                        }
                        ((AccessorEntityVelocityUpdateS2CPacket) packet).setVelocityX((int) (packet.getVelocityX()
                                * (horizontalConfig.getValue() / 100.0f)));
                        ((AccessorEntityVelocityUpdateS2CPacket) packet).setVelocityY((int) (packet.getVelocityY()
                                * (verticalConfig.getValue() / 100.0f)));
                        ((AccessorEntityVelocityUpdateS2CPacket) packet).setVelocityZ((int) (packet.getVelocityZ()
                                * (horizontalConfig.getValue() / 100.0f)));
                    }
                    case STRICT ->
                    {
                        if (!Managers.NCP.passed(100))
                        {
                            return;
                        }
                        event.cancel();
                        cancelVelocity = true;
                    }
                }
            }
            else if (event.getPacket() instanceof ExplosionS2CPacket packet)
            {
                switch (modeConfig.getValue())
                {
                    case NORMAL ->
                    {
                        if (horizontalConfig.getValue() == 0.0f
                                && verticalConfig.getValue() == 0.0f)
                        {
                            event.cancel();
                            return;
                        }
                        ((AccessorExplosionS2CPacket) packet).setPlayerVelocityX(packet.getPlayerVelocityX()
                                * (horizontalConfig.getValue() / 100.0f));
                        ((AccessorExplosionS2CPacket) packet).setPlayerVelocityY(packet.getPlayerVelocityY()
                                * (verticalConfig.getValue() / 100.0f));
                        ((AccessorExplosionS2CPacket) packet).setPlayerVelocityZ(packet.getPlayerVelocityZ()
                                * (horizontalConfig.getValue() / 100.0f));
                    }
                    case STRICT ->
                    {
                        if (!Managers.NCP.passed(100))
                        {
                            return;
                        }
                        event.cancel();
                        cancelVelocity = true;
                    }
                }
            }
            else if (event.getPacket() instanceof EntityStatusS2CPacket packet)
            {
                if (pushFishhookConfig.getValue() && packet.getStatus() == EntityStatuses.PULL_HOOKED_ENTITY)
                {
                    Entity entity = packet.getEntity(mc.world);
                    if (entity instanceof FishingBobberEntity hook
                            && hook.getHookedEntity() == mc.player)
                    {
                        event.cancel();
                    }
                }
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE
                && modeConfig.getValue() == VelocityMode.STRICT)
        {
            if (cancelVelocity)
            {
                if (Managers.NCP.passed(100))
                {
                    Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(),
                            mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
                    Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                            BlockPos.ofFloored(mc.player.getPos()), Direction.DOWN));
                }
                cancelVelocity = false;
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPushEntity(PushEntityEvent event)
    {
        if (pushEntitiesConfig.getValue())
        {
            event.cancel();
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPushOutOfBlocks(PushOutOfBlocksEvent event)
    {
        if (pushBlocksConfig.getValue())
        {
            event.cancel();
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPushFluid(PushFluidsEvent event)
    {
        if (pushLiquidsConfig.getValue())
        {
            event.cancel();
        }
    }

    private enum VelocityMode
    {
        NORMAL,
        STRICT
    }
}
