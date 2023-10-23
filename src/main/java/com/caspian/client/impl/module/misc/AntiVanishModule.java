package com.caspian.client.impl.module.misc;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.gui.hud.ChatMessageEvent;
import com.caspian.client.util.chat.ChatUtil;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiVanishModule extends ToggleModule
{
    //
    private final Timer vanishTimer = new CacheTimer();
    //
    private Map<UUID, String> playerCache = new HashMap<>();
    private final Set<String> messageCache = new HashSet<>();

    /**
     *
     */
    public AntiVanishModule()
    {
        super("AntiVanish", "Notifies user when a player uses /vanish",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        messageCache.clear();
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onChatMessage(ChatMessageEvent event)
    {
        // This only works if the server doesnt have a custom join/leave
        // message plugin
        String message = event.getText().getString();
        if (message.contains("left"))
        {
            messageCache.add(message);
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (!vanishTimer.passed(1000))
        {
            return;
        }
        final Map<UUID, String> players = playerCache;
        playerCache = mc.getNetworkHandler().getPlayerList().stream()
                .collect(Collectors.toMap(e -> e.getProfile().getId(),
                        e -> e.getProfile().getName()));
        for (UUID uuid : players.keySet())
        {
            if (playerCache.containsKey(uuid))
            {
                continue;
            }
            String name = players.get(uuid);
            if (messageCache.stream().noneMatch(s -> s.contains(name)))
            {
                ChatUtil.clientSendMessage("[AntiVanish] %s used /vanish!", name);
            }
        }
        messageCache.clear();
        vanishTimer.reset();
    }
}
