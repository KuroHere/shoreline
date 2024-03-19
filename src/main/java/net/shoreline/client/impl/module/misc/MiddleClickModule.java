package net.shoreline.client.impl.module.misc;

import net.minecraft.item.Items;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.MouseClickEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class MiddleClickModule extends ToggleModule
{
    //
    Config<Action> actionConfig = new EnumConfig<>("Action", "The action to " +
            "perform when middle-clicking", Action.FRIEND, Action.values());
    Config<MissAction> missActionConfig = new EnumConfig<>("MissAction", "Throws a pearl " +
            "if looking at air", MissAction.PEARL, MissAction.values());

    /**
     *
     */
    public MiddleClickModule()
    {
        super("MiddleClick", "Adds an additional bind on the mouse middle button",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onMouseClick(MouseClickEvent event)
    {
        if (mc.player == null || mc.interactionManager == null)
        {
            return;
        }
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_MIDDLE
                && event.getAction() == GLFW.GLFW_PRESS && mc.currentScreen == null)
        {
            if (mc.targetedEntity instanceof PlayerEntity target)
            {
                switch (actionConfig.getValue())
                {
                    case FRIEND ->
                    {
                        if (Managers.SOCIAL.isFriend(target.getUuid()))
                        {
                            Managers.SOCIAL.remove(target.getUuid());
                        }
                        else
                        {
                            Managers.SOCIAL.addFriend(target.getUuid());
                        }
                    }
                    case DUEL ->
                    {
                        ChatUtil.serverSendMessage("/duel " + target.getName());
                    }
                }
            }
            else if (mc.targetedEntity == null)
            {
                int slot = -1;
                for (int i = 0; i < 9; i++)
                {
                    ItemStack stack = mc.player.getInventory().getStack(i);
                    if (stack.getItem() == (missActionConfig.getValue() == MissAction.PEARL ? Items.ENDER_PEARL : Items.FIREWORK_ROCKET))
                    {
                        slot = i;
                        break;
                    }
                }
                if (slot != -1)
                {
                    int prev = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = slot;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    mc.player.getInventory().selectedSlot = prev;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                }
            }
        }
    }

    public enum Action
    {
        FRIEND,
        DUEL
    }

    public enum MissAction
    {
        PEARL,
        FIREWORK,
        OFF
    }
}
