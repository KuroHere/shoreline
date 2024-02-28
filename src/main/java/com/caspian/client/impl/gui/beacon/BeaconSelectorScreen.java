package com.caspian.client.impl.gui.beacon;

import com.caspian.client.impl.module.misc.BeaconSelectorModule;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.text.Text;

/**
 *
 *
 * @author Shoreline
 * @since 1.0
 */
public class BeaconSelectorScreen extends BeaconScreen
{
    public BeaconSelectorScreen(BeaconScreenHandler handler,
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
