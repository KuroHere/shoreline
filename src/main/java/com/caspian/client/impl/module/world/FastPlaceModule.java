package com.caspian.client.impl.module.world;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.ListConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.mixin.accessor.AccessorMinecraftClient;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.world.SneakBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class FastPlaceModule extends ToggleModule
{
    //
    Config<Selection> selectionConfig = new EnumConfig<>("Selection", "The " +
            "selection of items to apply fast placements", Selection.WHITELIST,
            Selection.values());
    Config<Integer> delayConfig = new NumberConfig<>("Delay", "Fast place " +
            "delay", 0, 1, 4);
    Config<Float> startDelayConfig = new NumberConfig<>("StartDelay", "Fast" +
            " place start delay", 0.0f, 0.0f, 1.0f);
    Config<Boolean> ghostFixConfig = new BooleanConfig("GhostFix", "Fixes " +
            "item ghosting issue on some servers", false);
    Config<List<Item>> whitelistConfig = new ListConfig<>("Whitelist",
            "Valid item whitelist", Items.EXPERIENCE_BOTTLE,
            Items.SNOWBALL, Items.EGG);
    Config<List<Item>> blacklistConfig = new ListConfig<>("Blacklist",
            "Valid item blacklist", Items.ENDER_PEARL, Items.ENDER_EYE);
    //
    private final CacheTimer startTimer = new CacheTimer();

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
            if (!mc.options.useKey.isPressed())
            {
                startTimer.reset();
            }
            else if (placeCheck(mc.player.getMainHandStack())
                    && startTimer.passed(startDelayConfig.getValue(), TimeUnit.SECONDS)
                    && ((AccessorMinecraftClient) mc).hookGetItemUseCooldown() > delayConfig.getValue())
            {
                if (ghostFixConfig.getValue())
                {
                    Managers.NETWORK.sendSequencedPacket(id ->
                            new PlayerInteractItemC2SPacket(mc.player.getActiveHand(), id));
                }
                ((AccessorMinecraftClient) mc).hookSetItemUseCooldown(delayConfig.getValue());
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
            if (event.getPacket() instanceof PlayerInteractBlockC2SPacket packet
                    && ghostFixConfig.getValue() && !event.isCached()
                    && placeCheck(mc.player.getStackInHand(packet.getHand())))
            {
                final BlockState state = mc.world.getBlockState(
                        packet.getBlockHitResult().getBlockPos());
                if (!SneakBlocks.isSneakBlock(state))
                {
                    event.cancel();
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
                    case WHITELIST -> ((ListConfig<?>) whitelistConfig)
                            .contains(held.getItem());
                    case BLACKLIST -> !((ListConfig<?>) blacklistConfig)
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
