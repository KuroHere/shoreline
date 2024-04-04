package net.shoreline.client.impl.module.combat;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.init.Managers;

/**
 * @author linus
 * @since 1.0
 */
public class ReplenishModule extends ToggleModule {
    //
    Config<Integer> percentConfig = new NumberConfig<>("Percent", "The minimum percent of total stack before replenishing", 1, 25, 80);

    /**
     *
     */
    public ReplenishModule() {
        super("Replenish", "Automatically replaces items in your hotbar",
                ModuleCategory.COMBAT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (event.getStage() != EventStage.PRE) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isStackable()) {
                continue;
            }
            float total = ((float) stack.getCount() / stack.getMaxCount()) * 100.0f;
            if (total < percentConfig.getValue()) {
                replenishStack(stack, i);
                break;
            }
        }
    }

    private void replenishStack(ItemStack item, int hotbarSlot) {
        int replenishSlot = -1;
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            // We cannot merge stacks if they don't have the same name
            if (!stack.getName().equals(item.getName())) {
                continue;
            }
            if (stack.getItem() instanceof BlockItem blockItem && (!(item.getItem() instanceof BlockItem blockItem1) || blockItem.getBlock() != blockItem1.getBlock())) {
                continue;
            }
            if (stack.getItem() != item.getItem()) {
                continue;
            }
            float total = ((float) (item.getCount() + stack.getCount()) / item.getMaxCount()) * 100.0f;
            if (total >= percentConfig.getValue()) {
                replenishSlot = i;
            }
        }
        if (replenishSlot != -1) {
            mc.interactionManager.clickSlot(0, replenishSlot, hotbarSlot, SlotActionType.SWAP, mc.player);
        }
    }
}
