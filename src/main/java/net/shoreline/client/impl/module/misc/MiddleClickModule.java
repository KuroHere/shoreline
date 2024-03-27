package net.shoreline.client.impl.module.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.MouseClickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;
import org.lwjgl.glfw.GLFW;

/**
 * @author linus
 * @since 1.0
 */
public class MiddleClickModule extends ToggleModule {

    //
    Config<MissAction> missActionConfig = new EnumConfig<>("Action", "Throws a pearl if looking at air", MissAction.PEARL, MissAction.values());
    Config<Action> actionConfig = new EnumConfig<>("Action-Player", "The action to perform when middle-clicking", Action.FRIEND, Action.values());

    /**
     *
     */
    public MiddleClickModule() {
        super("MiddleClick", "Adds an additional bind on the mouse middle button",
                ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onMouseClick(MouseClickEvent event) {
        if (mc.player == null || mc.interactionManager == null) {
            return;
        }
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE
                && event.getAction() == GLFW.GLFW_PRESS && mc.currentScreen == null) {
            if (mc.targetedEntity instanceof PlayerEntity target) {
                switch (actionConfig.getValue()) {
                    case FRIEND -> {
                        if (Managers.SOCIAL.isFriend(target.getUuid())) {
                            Managers.SOCIAL.remove(target.getUuid());
                        } else {
                            Managers.SOCIAL.addFriend(target.getUuid());
                        }
                    }
                    case DUEL -> {
                        ChatUtil.serverSendMessage("/duel " + target.getName());
                    }
                }
            } else if (mc.targetedEntity == null && missActionConfig.getValue() != MissAction.OFF) {
                int slot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.getInventory().getStack(i);
                    if (stack.getItem() == (missActionConfig.getValue() == MissAction.PEARL ? Items.ENDER_PEARL : Items.FIREWORK_ROCKET)) {
                        slot = i;
                        break;
                    }
                }
                if (slot != -1) {
                    int prev = mc.player.getInventory().selectedSlot;
                    Managers.INVENTORY.setClientSlot(slot);
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    Managers.INVENTORY.setClientSlot(prev);
                }
            }
        }
    }

    public enum Action {
        FRIEND,
        DUEL
    }

    public enum MissAction {
        PEARL,
        FIREWORK,
        OFF
    }
}
