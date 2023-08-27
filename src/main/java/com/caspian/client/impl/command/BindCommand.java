package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.OptionalArg;
import com.caspian.client.api.command.arg.arguments.ModuleArgument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.util.KeyboardUtil;
import com.caspian.client.util.chat.ChatUtil;
import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BindCommand extends Command
{
    //
    Argument<Module> moduleArg = new ModuleArgument("Module", "The " +
            "param module to keybind");
    @OptionalArg
    Argument<String> keybind = new StringArgument("Keybind", "The new key to " +
            "bind the module");

    /**
     *
     */
    public BindCommand()
    {
        super("Bind", "Keybinds a module");
    }

    /**
     * Runs when the command is inputted in chat
     */
    @Override
    public void onCommandInput()
    {
        Module module = moduleArg.parse();
        if (module instanceof ToggleModule t)
        {
            final String key = keybind.parse();
            if (key == null || key.length() > 1)
            {
                ChatUtil.error("Invalid key!");
                return;
            }
            int keycode = KeyboardUtil.getKeyCode(key);
            if (keycode == GLFW.GLFW_KEY_UNKNOWN)
            {
                ChatUtil.error("Failed to parse key!");
                return;
            }
            t.keybind(keycode);
            ChatUtil.clientSendMessage("%s is now bound to %s!",
                    module.getName(), key.toUpperCase());
        }
    }
}
