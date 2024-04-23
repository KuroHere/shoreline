package net.shoreline.client.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.shoreline.client.api.command.Command;
import net.shoreline.client.api.command.ConfigArgumentType;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.macro.Macro;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.util.ClientIdentifier;
import net.shoreline.client.util.KeyboardUtil;
import net.shoreline.client.util.chat.ChatUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author linus
 * @since 1.0
 */
public class ModuleCommand extends Command {
    //
    private final Module module;

    /**
     * @param module
     */
    public ModuleCommand(Module module) {
        super(module.getName(), module.getDescription(), literal(module.getName().toLowerCase()));
        // DO NOT REMOVE THIS - linus
        this.module = module;
    }


    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("setting", new ConfigArgumentType(module)))
                .then(argument("value", StringArgumentType.string()).executes(c -> {
                    Config<?> config = ConfigArgumentType.getConfig(c, "setting");
                    if (config == null) {
                        if (module instanceof ToggleModule m) { // Can use the module command to toggle
                            m.toggle();
                        }
                        return 0;
                    }
                    String value = StringArgumentType.getString(c, "value");
                    if (value == null) {
                        return 0;
                    }
                    // parse value
                    try {
                        if (config.getValue() instanceof Integer) {
                            ((Config<Integer>) config).setValue(Integer.parseInt(value));
                        } else if (config.getValue() instanceof Float) {
                            ((Config<Float>) config).setValue(Float.parseFloat(value));
                        } else if (config.getValue() instanceof Double) {
                            ((Config<Double>) config).setValue(Double.parseDouble(value));
                        }
                    } catch (NumberFormatException e) {
                        ChatUtil.error("Not a number!");
                        // e.printStackTrace();
                    }
                    if (config.getValue() instanceof Boolean) {
                        ((Config<Boolean>) config).setValue(Boolean.parseBoolean(value));
                    } else if (config.getValue() instanceof Enum<?>) {
                        String[] values = Arrays.stream(((Enum<?>) config.getValue()).getClass()
                                .getEnumConstants()).map(Enum::name).toArray(String[]::new);
                        // TODO: FIX THIS!
                        int ix = -1;
                        for (int i = 0; i < values.length; i++) {
                            if (values[i].equalsIgnoreCase(value)) {
                                ix = i;
                                break;
                            }
                        }
                        if (ix == -1) {
                            ChatUtil.error("Not a valid mode!");
                            return 0;
                        }
                        Enum<?> val = Enum.valueOf(((Enum<?>) config.getValue()).getClass(), values[ix]);
                        ((Config<Enum<?>>) config).setValue(val);
                    } else if (config.getValue() instanceof List<?>) {
                        if (value.startsWith("item")) {
                            Item item = Registries.ITEM.get(ClientIdentifier.toId(value));
                            ((Config<List<Item>>) config).getValue().add(item);
                        } else if (value.startsWith("block")) {
                            Block block = Registries.BLOCK.get(ClientIdentifier.toId(value));
                            ((Config<List<Block>>) config).getValue().add(block);
                        }
                        ChatUtil.clientSendMessage("%s was added to §7%s§f!", value, config.getName());
                        return 0;
                    } else if (config.getValue() instanceof Macro macro) {
                        if (config.getName().equalsIgnoreCase("Keybind")) {
                            ChatUtil.error("Use the 'bind' command to keybind modules!");
                            return 0;
                        }
                        ((Config<Macro>) config).setValue(new Macro(config.getId(), KeyboardUtil.getKeyCode(value), macro.getRunnable()));
                    } else if (config.getValue() instanceof String) {
                        ((Config<String>) config).setValue(value);
                    }
                    ChatUtil.clientSendMessage("§7%s§f was set to %s!", config.getName(), value);
                    return 1;
                }));
    }
}
