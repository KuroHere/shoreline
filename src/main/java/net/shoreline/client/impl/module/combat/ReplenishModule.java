package net.shoreline.client.impl.module.combat;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.minecraft.item.ItemStack;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ReplenishModule extends ToggleModule
{
    //
    Config<Integer> percentConfig = new NumberConfig<>("Percent",
            "The minimum percent of total stack before replenishing", 1, 25, 80);

    /**
     *
     */
    public ReplenishModule()
    {
        super("Replenish", "Automatically replaces items in your hotbar",
                ModuleCategory.COMBAT);
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
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isStackable())
            {
                continue;
            }
            float total = ((float) stack.getCount() / stack.getMaxCount()) * 100.0f;
            if (total < percentConfig.getValue())
            {

            }
        }
    }

    /**
     *
     * @param item
     * @param slot
     */
    private void replenishStack(ItemStack item, int slot)
    {
        for (int i = 9; i < 36; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            // We cannot merge stacks if they do not have the same name
            if (!stack.getName().equals(item.getName()))
            {
                continue;
            }

        }
    }
}
