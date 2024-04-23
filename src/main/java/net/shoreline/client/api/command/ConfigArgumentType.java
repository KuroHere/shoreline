package net.shoreline.client.api.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.module.Module;

import java.util.concurrent.CompletableFuture;

public class ConfigArgumentType implements ArgumentType<Config<?>> {

    private final Module module;

    public ConfigArgumentType(Module module) {
        this.module = module;
    }

    public static Config<?> getConfig(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Config.class);
    }

    @Override
    public Config<?> parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readString();
        Config<?> config = null;
        for (Config<?> config1 : module.getConfigs()) {
            if (config1.getName().equalsIgnoreCase(string)) {
                config = config1;
                break;
            }
        }
        if (config == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, null);
        }
        return config;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context,
                                                              final SuggestionsBuilder builder) {
        for (Config<?> config : module.getConfigs()) {
            builder.suggest(config.getName().toLowerCase());
        }
        return builder.buildFuture();
    }
}
