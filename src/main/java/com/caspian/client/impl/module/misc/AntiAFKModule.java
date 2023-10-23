package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.setting.StringConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.chat.ChatUtil;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiAFKModule extends ToggleModule
{
    //
    Config<Boolean> messageConfig = new BooleanConfig("Message", "Messages in" +
            " chat to prevent AFK kick", true);
    Config<Boolean> tabCompleteConfig = new BooleanConfig("TabComplete",
            "Uses tab complete in chat to prevent AFK kick", true);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate",
            "Rotates the player to prevent AFK kick", true);
    Config<Boolean> autoReplyConfig = new BooleanConfig("AutoReply", "Replies" +
            " to players messaging you in chat", true);
    Config<String> replyConfig = new StringConfig("Reply", "The reply message" +
            " for AutoReply", "[Caspian] I am currently AFK.");
    Config<Float> delayConfig = new NumberConfig<>("Delay", "The delay " +
            "between actions", 5.0f, 60.0f, 270.0f);

    /**
     *
     */
    public AntiAFKModule()
    {
        super("AntiAFK", "Prevents the player from being kicked for AFK",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (event.getPacket() instanceof ChatMessageS2CPacket packet
                && autoReplyConfig.getValue())
        {
            String[] words = packet.body().content().split(" ");
            if (words[1].startsWith("whispers:"))
            {
                ChatUtil.serverSendMessage("/r " + replyConfig.getValue());
            }
        }
    }
}
