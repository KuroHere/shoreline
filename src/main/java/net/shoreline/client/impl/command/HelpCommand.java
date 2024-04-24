package net.shoreline.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.shoreline.client.api.command.Command;
import net.shoreline.client.api.command.CommandArgumentType;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.chat.ChatUtil;

/**
 * @author linus
 * @since 1.0
 */
public class HelpCommand extends Command {

    /**
     *
     */
    public HelpCommand() {
        super("Help", "Displays command functionality", literal("help"));
    }

    /**
     * @param command
     * @return
     */
    private static String toHelpMessage(Command command) {
        if (command instanceof ModuleCommand) {
            return String.format("Module %s- %s", command.getUsage(),
                    command.getDescription());
        }
        return String.format("%s %s- %s", command.getName(),
                command.getUsage(), command.getDescription());
    }

    @Override
    public void buildCommand(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("command", CommandArgumentType.command()).executes(c -> {
            final Command command = CommandArgumentType.getCommand(c, "command");
            ChatUtil.clientSendMessage(toHelpMessage(command));
            return 1;
        })).executes(c -> {
            ChatUtil.clientSendMessageRaw("§7[§fCommands Help§7]");
            for (Command c1 : Managers.COMMAND.getCommands()) {
                if (c1 instanceof ModuleCommand) {
                    continue;
                }
                ChatUtil.clientSendMessageRaw(toHelpMessage(c1));
            }
            return 1;
        });
    }
}
