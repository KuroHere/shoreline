package com.caspian.client.api.command.arg;

import com.caspian.client.api.command.Command;
import com.caspian.client.util.Globals;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PlayerArgument extends Argument<PlayerEntity> implements Globals
{
    /**
     *
     */
    public PlayerArgument()
    {

    }

    /**
     *
     *
     * @see Command#runCommand()
     */
    @Override
    public void buildArgument()
    {
        if (mc.world != null)
        {
            for (PlayerEntity player : mc.world.getPlayers())
            {
                if (player.getDisplayName().getString()
                        .equalsIgnoreCase(getLiteral()))
                {
                    setValue(player);
                    break;
                }
            }
        }
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String[] getSuggestions()
    {
        Collection<String> playerNames = new ArrayList<>();
        if (mc.world != null)
        {
            for (PlayerEntity player : mc.world.getPlayers())
            {
                playerNames.add(player.getDisplayName().getString());
            }
        }

        return (String[]) playerNames.toArray();
    }
}
