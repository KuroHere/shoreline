package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.ModuleArgument;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.Hideable;
import com.caspian.client.util.chat.ChatUtil;
import net.minecraft.util.Formatting;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Hideable
 */
public class DrawnCommand extends Command
{
    //
    Argument<Module> moduleArg = new ModuleArgument("Module", "The module to " +
            "toggle the drawn state");

    /**
     *
     */
    public DrawnCommand()
    {
        super("Drawn", "Toggles the drawn state of the module");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        Module module = moduleArg.parse();
        if (module instanceof ToggleModule toggle)
        {
            boolean hide = !toggle.isHidden();
            toggle.setHidden(hide);
            ChatUtil.clientSendMessage(module.getName() + " is " + Formatting.RED +
                    (hide ? "hidden" : "visible") + Formatting.RESET + " in the Hud!");
        }
    }
}
