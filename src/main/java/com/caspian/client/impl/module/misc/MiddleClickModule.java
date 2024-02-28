package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.MouseClickEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.chat.ChatUtil;
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
    Config<Boolean> pearlConfig = new BooleanConfig("Pearl", "Throws a pearl " +
            "if looking at air", false);

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
                        ChatUtil.serverSendMessage("/duel " + target.getEntityName());
                    }
                }
            }
            else if (pearlConfig.getValue())
            {
                int slot = -1;
                for (int i = 0; i < 9; i++)
                {
                    ItemStack stack = mc.player.getInventory().getStack(i);
                    if (stack.getItem() instanceof EnderPearlItem)
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
                    mc.interactionManager.interactItem(mc.player,
                            Hand.MAIN_HAND);
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
}
