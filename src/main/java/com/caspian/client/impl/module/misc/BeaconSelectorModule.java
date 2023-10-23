package com.caspian.client.impl.module.misc;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.ScreenOpenEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.mixin.accessor.AccessorUpdateBeaconC2SPacket;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.text.Text;

import java.util.Optional;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BeaconSelectorModule extends ToggleModule
{
    //
    private StatusEffect primaryEffect;
    private StatusEffect secondaryEffect;

    /**
     *
     */
    public BeaconSelectorModule()
    {
        super("BeaconSelector", "Allows you to change beacon effects",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (event.getPacket() instanceof UpdateBeaconC2SPacket packet)
        {
            ((AccessorUpdateBeaconC2SPacket) packet).setPrimaryEffect(Optional.ofNullable(primaryEffect));
            ((AccessorUpdateBeaconC2SPacket) packet).setSecondaryEffect(Optional.ofNullable(secondaryEffect));
        }
    }

    //
    private boolean customBeacon;

    /**
     *
     * @param event
     */
    @EventListener
    public void onScreenOpen(ScreenOpenEvent event)
    {
        if (event.getScreen() instanceof BeaconScreen screen && !customBeacon)
        {
            event.cancel();
            customBeacon = true;
            mc.setScreen(new CustomBeaconScreen(screen.getScreenHandler(),
                    mc.player.getInventory(), screen.getTitle()));
            customBeacon = false;
        }
    }

    /**
     *
     */
    public class CustomBeaconScreen extends BeaconScreen
    {

        public CustomBeaconScreen(BeaconScreenHandler handler,
                                  PlayerInventory inventory,
                                  Text title)
        {
            super(handler, inventory, title);
        }

        /**
         *
         */
        @Override
        public void init()
        {
            super.init();
        }

        /**
         *
         */
        @Override
        public void handledScreenTick()
        {
            super.handledScreenTick();
        }

    }
}
