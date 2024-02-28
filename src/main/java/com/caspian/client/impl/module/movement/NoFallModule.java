package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.mixin.accessor.AccessorPlayerMoveC2SPacket;
import com.caspian.client.util.string.EnumFormatter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.world.World;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoFallModule extends ToggleModule
{
    //
    Config<NoFallMode> modeConfig = new EnumConfig<>("Mode", "The mode to " +
            "prevent fall damage", NoFallMode.ANTI, NoFallMode.values());

    /**
     *
     */
    public NoFallModule()
    {
        super("NoFall", "Prevents all fall damage", ModuleCategory.MOVEMENT);
    }

    /**
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
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE
                && modeConfig.getValue() == NoFallMode.LATENCY)
        {
            if (mc.player.fallDistance <= mc.player.getSafeFallDistance())
            {
                return;
            }
            if (mc.world.getRegistryKey() == World.NETHER)
            {
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), 0, mc.player.getZ(), true));
            }
            else
            {
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        0, 64, 0, true));
            }
            mc.player.fallDistance = 0.0f;
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player == null || mc.player.fallDistance <= mc.player.getSafeFallDistance())
        {
            return;
        }
        if (event.getPacket() instanceof PlayerMoveC2SPacket packet)
        {
            if (modeConfig.getValue() == NoFallMode.VANILLA)
            {
                ((AccessorPlayerMoveC2SPacket) packet).hookSetOnGround(true);
            }
            else if (modeConfig.getValue() == NoFallMode.ANTI)
            {
                double y = packet.getY(mc.player.getY());
                ((AccessorPlayerMoveC2SPacket) packet).hookSetY(y + 0.10000000149011612);
            }
        }
    }

    public enum NoFallMode
    {
        ANTI,
        LATENCY,
        VANILLA
    }
}
