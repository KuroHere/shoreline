package net.shoreline.client.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.shoreline.client.api.command.Command;
import net.shoreline.client.api.command.ConfigArgumentType;
import net.shoreline.client.api.command.ItemArgumentType;
import net.shoreline.client.api.command.PlayerArgumentType;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.ItemListConfig;
import net.shoreline.client.api.macro.Macro;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.ToggleModule;
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
        this.module = module;
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("setting", ConfigArgumentType.config(module))
                .then(argument("value", StringArgumentType.string())
                        .executes(c -> {
                            Config<?> config = ConfigArgumentType.getConfig(c, "setting");
                            String value = StringArgumentType.getString(c, "value");
                            if (value.equalsIgnoreCase("list")) {
                                return listItems(config, value);
                            }
                            return updateValue(config, value);
                        }).then(argument("item", ItemArgumentType.item())
                                .executes(c -> {
                                    Config<?> config = ConfigArgumentType.getConfig(c, "setting");
                                    String action = StringArgumentType.getString(c, "value");
                                    Item value = ItemArgumentType.getItem(c, "item");
                                    return addDeleteItem(config, action, value);
                                }))).executes(c -> {
                                    ChatUtil.error("Must provide a value!");
                                    return 1;
                                })).executes(c -> {
                                    if (module instanceof ToggleModule m) { // Can use the module command to toggle
                                        m.toggle();
                                    }
                                    return 1;
                                });
    }

    private int addDeleteItem(Config<?> config, String action, Item value) {
        if (config instanceof ItemListConfig) {
            List<Item> list = ((List<Item>) config.getValue());
            if (action.equalsIgnoreCase("add")) {
                list.add(value);
                ChatUtil.clientSendMessage("Added " + value.getName().getString() + " to " + config.getName() + "!");
            } else if (action.equalsIgnoreCase("del") || action.equalsIgnoreCase("remove")) {
                list.remove(value);
                ChatUtil.clientSendMessage("Removed " + value.getName().getString() + " from " + config.getName() + "!");
            }
        }
        return 1;
    }

    private int listItems(Config<?> config, String action) {
        if (config instanceof ItemListConfig) {
            List<Item> list = ((List<Item>) config.getValue());
            if (action.equalsIgnoreCase("list")) {
                if (list.isEmpty()) {
                    ChatUtil.clientSendMessage("There are no items in the list!");
                    return 1;
                }
                StringBuilder listString = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    Item item = list.get(i);
                    listString.append(item.getName().getString());
                    if (i <= list.size() - 1) {
                        listString.append(", ");
                    }
                }
                ChatUtil.clientSendMessage(config.getName() + ": " + listString);
            }
        }
        return 1;
    }

    private int updateValue(Config<?> config, String value) {
        if (config == null || value == null) {
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
    }
}
