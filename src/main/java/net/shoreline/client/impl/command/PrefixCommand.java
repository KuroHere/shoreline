package net.shoreline.client.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import net.shoreline.client.api.command.Command;
import net.shoreline.client.api.macro.Macro;
import net.shoreline.client.api.module.Module;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.KeyboardUtil;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * @author linus
 * @since 1.0
 */
public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("Prefix", "Allows you to change the chat command prefix", literal("prefix"));
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("prefix", StringArgumentType.string()).executes(c -> {
            final String prefix = StringArgumentType.getString(c, "prefix");
            if (prefix.length() > 1) {
                ChatUtil.error("Prefix can only be one character!");
                return 0;
            }
            int keycode = KeyboardUtil.getKeyCode(prefix);
            for (Macro macro : Managers.MACRO.getMacros()) {
                if (macro.getKeycode() == keycode) {
                    ChatUtil.error("Macro already bound to " + prefix + "!");
                    return 0;
                }
            }
            for (Module module : Managers.MODULE.getModules()) {
                if (module instanceof ToggleModule toggle) {
                    Macro keybind = toggle.getKeybinding();
                    if (keybind.getKeycode() == keycode) {
                        ChatUtil.error(module.getName() + " already bound to " + prefix + "!");
                        return 0;
                    }
                }
            }
            Managers.COMMAND.setPrefix(prefix, keycode);
            ChatUtil.clientSendMessage("Client command prefix changed to " +
                    Formatting.DARK_BLUE + prefix + Formatting.RESET + "!");
            return 1;
        }));
    }
}
