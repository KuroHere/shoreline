package com.caspian.client.impl.module.world;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.ListConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.InteractBlockEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiInteractModule extends ToggleModule
{
    //
    Config<Boolean> packetsConfig = new BooleanConfig("Packets", "Prevents" +
            " player interact packets", false);
    Config<List<Block>> blocksConfig = new ListConfig<>("Blocks",
            "The blocks to prevent player interact", Blocks.ENDER_CHEST,
            Blocks.ANVIL);

    /**
     *
     */
    public AntiInteractModule()
    {
        super("AntiInteract", "Prevents player from " +
                "interacting with certain objects", ModuleCategory.WORLD);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onInteractBlock(InteractBlockEvent event)
    {
        BlockPos pos = event.getHitResult().getBlockPos();
        Block block = mc.world.getBlockState(pos).getBlock();
        if (((ListConfig<?>) blocksConfig).contains(block))
        {
            event.cancel();
            if (packetsConfig.getValue())
            {
                return;
            }
            // Managers.NETWORK.sendSequencedPacket(sequence -> new PlayerInteractBlockC2SPacket(
            //        event.getHand(), event.getHitResult(), sequence));
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player == null || mc.world == null)
        {
            return;
        }
        if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet
                && packetsConfig.getValue())
        {
            BlockPos pos = packet.getBlockHitResult().getBlockPos();
            Block block = mc.world.getBlockState(pos).getBlock();
            if (((ListConfig<?>) blocksConfig).contains(block))
            {
                event.cancel();
            }
        }
    }
}
