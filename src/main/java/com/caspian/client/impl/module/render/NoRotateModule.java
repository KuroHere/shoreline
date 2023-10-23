package com.caspian.client.impl.module.render;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.mixin.accessor.AccessorPlayerMoveC2SPacket;
import com.caspian.client.mixin.accessor.AccessorPlayerPositionLookS2CPacket;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoRotateModule extends ToggleModule
{
    //
    private float yaw, pitch;
    private boolean cancelRotate;

    /**
     *
     */
    public NoRotateModule()
    {
        super("NoRotate", "Prevents server from forcing rotations",
                ModuleCategory.RENDER);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player == null || mc.currentScreen instanceof DownloadingTerrainScreen)
        {
            return;
        }
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket packet)
        {
            yaw = packet.getYaw();
            pitch = packet.getPitch();
            ((AccessorPlayerPositionLookS2CPacket) packet).setYaw(mc.player.getYaw());
            ((AccessorPlayerPositionLookS2CPacket) packet).setPitch(mc.player.getPitch());
            packet.getFlags().remove(PositionFlag.X_ROT);
            packet.getFlags().remove(PositionFlag.Y_ROT);
            cancelRotate = true;
            // Cancel packet and custom handle??
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (event.getPacket() instanceof PlayerMoveC2SPacket.Full packet && cancelRotate)
        {
            ((AccessorPlayerMoveC2SPacket) packet).hookSetYaw(yaw);
            ((AccessorPlayerMoveC2SPacket) packet).hookSetPitch(pitch);
            cancelRotate = false;
        }
    }
}
