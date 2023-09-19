package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.api.macro.Macro;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.init.Managers;
import com.caspian.client.util.KeyboardUtil;
import com.caspian.client.util.chat.ChatUtil;
import net.minecraft.util.Formatting;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PrefixCommand extends Command
{
    //
    Argument<String> prefixArgument = new StringArgument("Single-char Prefix",
            "The new chat command prefix");

    /**
     *
     */
    public PrefixCommand()
    {
        super("Prefix", "Allows you to change the chat command prefix");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        final String prefix = prefixArgument.parse();
        if (prefix != null)
        {
            if (prefix.length() > 1)
            {
                ChatUtil.error("Prefix can only be one character!");
                return;
            }
            int keycode = KeyboardUtil.getKeyCode(prefix);
            for (Macro macro : Managers.MACRO.getMacros())
            {
                if (macro.getKeycode() == keycode)
                {
                    ChatUtil.error("Macro already bound to " + prefix + "!");
                    return;
                }
            }
            for (Module module : Managers.MODULE.getModules())
            {
                if (module instanceof ToggleModule toggle)
                {
                    Macro keybind = toggle.getKeybinding();
                    if (keybind.getKeycode() == keycode)
                    {
                        ChatUtil.error(module.getName() + " already bound to "
                                + prefix + "!");
                        return;
                    }
                }
            }
            Managers.COMMAND.setPrefix(prefix, keycode);
            ChatUtil.clientSendMessage("Client command prefix changed to " +
                    Formatting.DARK_BLUE + prefix + Formatting.RESET + "!");
        }
    }
}
