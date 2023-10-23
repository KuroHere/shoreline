package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.imixin.IMinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoFishModule extends ToggleModule
{
    //
    Config<Boolean> openInventoryConfig = new BooleanConfig("OpenInventory",
            "Allows you to fish while in the inventory", true);
    Config<Integer> castDelayConfig = new NumberConfig<>("CastingDelay", "The " +
            "delay between fishing rod casts", 10, 15, 25);
    Config<Float> maxSoundDistConfig = new NumberConfig<>("MaxSoundDist",
            "The maximum distance from the splash sound", 0.0f, 2.0f, 5.0f);
    //
    private boolean autoReel;
    private int autoReelTicks;
    //
    private int autoCastTicks;

    /**
     *
     */
    public AutoFishModule()
    {
        super("AutoFish", "Automatically casts and reels fishing rods",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player == null)
        {
            return;
        }
        if (event.getPacket() instanceof PlaySoundS2CPacket packet
                && packet.getSound().value() == SoundEvents.ENTITY_FISHING_BOBBER_SPLASH
                && mc.player.getMainHandStack().getItem() == Items.FISHING_ROD)
        {
            FishingBobberEntity fishHook = mc.player.fishHook;
            if (fishHook == null || fishHook.getPlayerOwner() != mc.player)
            {
                return;
            }
            double dist = fishHook.squaredDistanceTo(packet.getX(),
                    packet.getY(), packet.getZ());
            if (dist <= maxSoundDistConfig.getValue())
            {
                autoReel = true;
                autoReelTicks = 4;
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
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen
                || openInventoryConfig.getValue())
        {
            FishingBobberEntity fishHook = mc.player.fishHook;
            if ((fishHook == null || fishHook.getHookedEntity() != null)
                    && autoCastTicks <= 0)
            {
                ((IMinecraftClient) mc).rightClick();
                autoCastTicks = castDelayConfig.getValue();
                return;
            }
            if (autoReel)
            {
                if (autoReelTicks <= 0)
                {
                    ((IMinecraftClient) mc).rightClick();
                    autoReel = false;
                    return;
                }
                autoReelTicks--;
            }
        }
        autoCastTicks--;
    }
}
