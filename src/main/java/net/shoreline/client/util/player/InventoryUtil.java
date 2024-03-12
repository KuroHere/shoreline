package net.shoreline.client.util.player;

import net.minecraft.item.EndCrystalItem;
import net.minecraft.util.Hand;
import net.shoreline.client.util.Globals;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InventoryUtil implements Globals
{
    /**
     *
     *
     * @return
     */
    public static boolean isHolding32k()
    {
        return isHolding32k(1000);
    }

    /**
     *
     *
     * @param lvl
     * @return
     */
    public static boolean isHolding32k(int lvl)
    {
        final ItemStack mainhand = mc.player.getMainHandStack();
        return EnchantmentHelper.getLevel(Enchantments.SHARPNESS, mainhand) >= lvl;
    }
    public static int getCrystalSlot()
    {
        int slot = -1;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof EndCrystalItem)
            {
                slot = i;
                break;
            }
        }
        return slot;
    }
    public static Hand getCrystalHand()
    {
        final ItemStack offhand = mc.player.getOffHandStack();
        final ItemStack mainhand = mc.player.getMainHandStack();
        if (offhand.getItem() instanceof EndCrystalItem)
        {
            return Hand.OFF_HAND;
        }
        else if (mainhand.getItem() instanceof EndCrystalItem)
        {
            return Hand.MAIN_HAND;
        }
        return null;
    }
}
