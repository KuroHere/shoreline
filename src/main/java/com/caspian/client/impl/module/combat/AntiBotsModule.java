package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.ScreenOpenEvent;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.imixin.IPlayerInteractEntityC2SPacket;
import com.caspian.client.util.network.InteractType;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AntiBotsModule extends ToggleModule
{
    //
    Config<Boolean> pingConfig = new BooleanConfig("Ping", "Checks the ping " +
            "of the bot", true);
    Config<Boolean> invisibleConfig = new BooleanConfig("Invisibles", "Checks" +
            " if the bot is invisible", true);
    Config<Boolean> nameConfig = new BooleanConfig("Name", "Checks the " +
            "username of the bot", true);
    Config<Boolean> uuidConfig = new BooleanConfig("UUID", "Checks the UUID " +
            "of the bot", true);
    //
    private final Set<PlayerEntity> botPlayers = new HashSet<>();

    /**
     *
     */
    public AntiBotsModule()
    {
        super("AntiBots", "Prevents player from interacting with bots",
                ModuleCategory.COMBAT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() != EventStage.POST || mc.player.isDead()
                || mc.player.isSpectator())
        {
            return;
        }
        //
        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();
        players.remove(mc.player);
        if (uuidConfig.getValue())
        {
            for (int i = 0; i < players.size(); i++)
            {
                for (int j = i + 1; j < players.size(); j++)
                {
                    PlayerEntity player1 = players.get(i);
                    PlayerEntity player2 = players.get(j);
                    if (player1.getUuid() == player2.getUuid())
                    {
                        if (player1.getId() > player2.getId())
                        {
                            botPlayers.add(player1);
                            botPlayers.remove(player2);
                            break;
                        }
                        botPlayers.add(player2);
                        botPlayers.remove(player1);
                        break;
                    }
                }
            }
        }
        players.stream().filter(p -> checkInvisibility(p) || checkPing(p) || checkName(p))
                .forEach(p -> botPlayers.add(p));
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (event.getPacket() instanceof IPlayerInteractEntityC2SPacket packet
                && packet.getType() == InteractType.ATTACK
                && packet.getEntity() instanceof PlayerEntity attackPlayer
                && botPlayers.contains(attackPlayer))
        {
            event.cancel();
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onDisconnect(DisconnectEvent event)
    {
        botPlayers.clear();
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onScreenOpen(ScreenOpenEvent event)
    {
        if (event.getScreen() instanceof DownloadingTerrainScreen)
        {
            botPlayers.clear();
        }
    }

    /**
     *
     * @param player
     * @return
     */
    private boolean checkInvisibility(PlayerEntity player)
    {
        return invisibleConfig.getValue() && player.isInvisible()
                && !player.hasStatusEffect(StatusEffects.INVISIBILITY);
    }

    /**
     *
     * @param player
     * @return
     */
    private boolean checkPing(PlayerEntity player)
    {
        return pingConfig.getValue() && mc.getNetworkHandler() != null
                && mc.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null;
    }

    /**
     *
     * @param player
     * @return
     */
    private boolean checkName(PlayerEntity player)
    {
        return nameConfig.getValue() && player.getDisplayName().getString().equalsIgnoreCase(
                new StringBuilder().insert(0, player.getName()).append("§r").toString())
                && !mc.player.getDisplayName().getString().equalsIgnoreCase(
                        new StringBuilder().insert(0, mc.player.getName()).append("§r").toString());
    }
}
