package net.shoreline.client.api.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.shoreline.client.util.Globals;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class PlayerArgumentType implements ArgumentType<PlayerEntity>, Globals {

    public static PlayerEntity getPlayer(final CommandContext<?> context, final String name) {
        return context.getArgument(name, PlayerEntity.class);
    }

    @Override
    public PlayerEntity parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        Collection<PlayerListEntry> playerListEntries = mc.player.networkHandler.getPlayerList();
        PlayerListEntry entry = playerListEntries.stream().filter(p -> p.getDisplayName() != null && p.getDisplayName().getString().equals(string)).findFirst().orElse(null);
        if (entry == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, null);
        }
        return mc.world.getPlayerByUuid(entry.getProfile().getId());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Collection<PlayerListEntry> playerListEntries = mc.player.networkHandler.getPlayerList();
        for (PlayerListEntry playerListEntry : playerListEntries) {
            if (playerListEntry.getDisplayName() == null) {
                continue;
            }
            builder.suggest(playerListEntry.getDisplayName().getString());
        }
        return builder.buildFuture();
    }
}
