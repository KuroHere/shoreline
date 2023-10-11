package com.caspian.client.impl.event.network;

import com.caspian.client.api.event.Event;
import com.caspian.client.util.Globals;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 *
 * @author linus
 * @since 1.0
 *
 * @see com.caspian.client.mixin.network.MixinClientPlayerEntity
 */
public class SetCurrentHandEvent extends Event implements Globals
{
    //
    private final Hand hand;

    public SetCurrentHandEvent(Hand hand)
    {
        this.hand = hand;
    }

    public Hand getHand()
    {
        return hand;
    }

    public ItemStack getStackInHand()
    {
        return mc.player.getStackInHand(hand);
    }
}
