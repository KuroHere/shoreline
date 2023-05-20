package com.caspian.impl.module.world;

import com.caspian.api.config.Config;
import com.caspian.api.config.setting.BooleanConfig;
import com.caspian.api.config.setting.EnumConfig;
import com.caspian.api.config.setting.ListConfig;
import com.caspian.api.config.setting.NumberConfig;
import com.caspian.api.event.EventStage;
import com.caspian.api.event.listener.EventListener;
import com.caspian.api.module.ModuleCategory;
import com.caspian.api.module.ToggleModule;
import com.caspian.asm.accessor.AccessorMinecraftClient;
import com.caspian.impl.event.TickEvent;
import com.caspian.impl.event.network.PacketEvent;
import com.caspian.util.world.SneakBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * 
 */
public class FastPlaceModule extends ToggleModule
{
    //
    Config<Selection> selectionConfig = new EnumConfig<>("Selection", "",
            Selection.WHITELIST, Selection.values());
    Config<Integer> delayConfig = new NumberConfig<>("Delay", "Fast place " +
            "delay", 0, 0, 4);
    Config<Boolean> ghostFixConfig = new BooleanConfig("GhostFix", "Fixes " +
            "item ghosting issue on some servers", false);
    Config<List<Item>> whitelistConfig = new ListConfig<>("Whitelist",
            "Valid item whitelist", Items.EXPERIENCE_BOTTLE,
            Items.SNOWBALL, Items.EGG);
    Config<List<Item>> blacklistConfig = new ListConfig<>("Blacklist",
            "Valid item blacklist", Items.ENDER_PEARL, Items.ENDER_EYE);

    /**
     *
     */
    public FastPlaceModule()
    {
        super("FastPlace", "Place items and blocks faster",
                ModuleCategory.WORLD);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            if (placeCheck(mc.player.getMainHandStack()))
            {
                ((AccessorMinecraftClient) mc).setItemUseCooldown(delayConfig.getValue());
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet)
            {
                if (ghostFixConfig.getValue()
                        && placeCheck(mc.player.getStackInHand(packet.getHand())))
                {
                    BlockState state = mc.world.getBlockState(
                            packet.getBlockHitResult().getBlockPos());
                    if (!SneakBlocks.isSneakBlock(state))
                    {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param held
     * @return
     */
    private boolean placeCheck(ItemStack held)
    {
        return switch (selectionConfig.getValue())
                {
                    case WHITELIST -> ((ListConfig<Item>) whitelistConfig)
                            .contains(held.getItem());
                    case BLACKLIST -> ((ListConfig<Item>) blacklistConfig)
                            .contains(held.getItem());
                    case ALL -> true;
                };
    }

    public enum Selection
    {
        WHITELIST,
        BLACKLIST,
        ALL
    }
}
