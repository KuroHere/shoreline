package com.caspian.client.impl.command;

import com.caspian.client.api.command.Command;
import com.caspian.client.api.command.arg.Argument;
import com.caspian.client.api.command.arg.arguments.ConfigArgument;
import com.caspian.client.api.command.arg.arguments.StringArgument;
import com.caspian.client.api.config.Config;
import com.caspian.client.api.macro.Macro;
import com.caspian.client.api.module.Module;
import com.caspian.client.util.KeyboardUtil;
import com.caspian.client.util.chat.ChatUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ModuleCommand extends Command
{
    //
    Argument<Config<?>> configArg = new ConfigArgument("Config", "The setting" +
            " to configure");;
    Argument<String> valueArg = new StringArgument("Value", "The new value to" +
            " set the config");

    /**
     *
     * @param module
     */
    public ModuleCommand(Module module)
    {
        super(module.getName().toLowerCase(), "<setting> <value>",
                "Configures the module");
        configArg.setContainer(module);
    }

    /**
     * Runs when the command is inputted in chat
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onCommandInput()
    {
        Config<?> config = configArg.parse();
        if (config != null)
        {
            String value = valueArg.parse();
            if (value == null)
            {
                return;
            }
            try
            {
                if (config.getValue() instanceof Integer)
                {
                    ((Config<Integer>) config).setValue(Integer.parseInt(value));
                }
                else if (config.getValue() instanceof Float)
                {
                    ((Config<Float>) config).setValue(Float.parseFloat(value));
                }
                else if (config.getValue() instanceof Double)
                {
                    ((Config<Double>) config).setValue(Double.parseDouble(value));
                }
            }
            catch (NumberFormatException e)
            {
                ChatUtil.error("Not a number!");
                // e.printStackTrace();
            }
            if (config.getValue() instanceof Boolean)
            {
                ((Config<Boolean>) config).setValue(Boolean.parseBoolean(value));
            }
            else if (config.getValue() instanceof Enum<?>)
            {
                String[] values = Arrays.stream(((Enum<?>) config.getValue()).getClass()
                        .getEnumConstants()).map(Enum::name).toArray(String[]::new);
                // TODO: FIX THIS!
                int ix = -1;
                for (int i = 0; i < values.length; i++)
                {
                    if (values[i].equalsIgnoreCase(value))
                    {
                        ix = i;
                        break;
                    }
                }
                if (ix == -1)
                {
                    ChatUtil.error("Not a valid mode!");
                    return;
                }
                Enum<?> val = Enum.valueOf(((Enum<?>) config.getValue())
                        .getClass(), values[ix]);
                ((Config<Enum<?>>) config).setValue(val);
            }
            else if (config.getValue() instanceof Macro macro)
            {
                ((Config<Macro>) config).setValue(new Macro(config.getId(),
                        KeyboardUtil.getKeyCode(value), macro.macro()));
            }
            else if (config.getValue() instanceof String)
            {
                ((Config<String>) config).setValue(value);
            }
        }
    }
}
