package com.momentum.impl.configs;

import com.moandjiezana.toml.Toml;
import com.momentum.api.config.Config;
import com.momentum.api.feature.Option;
import com.momentum.api.module.ConcurrentModule;
import com.momentum.api.module.Module;
import com.momentum.Momentum;
import com.momentum.api.util.render.Formatter;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author linus
 * @since 02/03/2023
 */
public class UserConfig extends Config<Module> {

    /**
     * User configs
     */
    public UserConfig() {

        // add shutdown hook
        SHUTDOWN_HOOK.hook(() -> save());
    }

    @Override
    public void save() {

        // module
        for (Module module : Momentum.MODULE_REGISTRY.getModules()) {

            // path with module name
            Path f = modules.resolve(module.getName() + ".toml");

            // catches I0Exception
            try {

                // check if it already exists
                if (!Files.exists(f)) {

                    // create the new file
                    Files.createFile(f);
                }
            }

            // error when writing file
            catch (IOException e) {
                e.printStackTrace();
            }

            // module data
            String data = toData(module);

            // do not allow two streams to write to the same file at once
            if (!isDirty(f)) {

                // our file output stream
                OutputStream stream = null;

                // catches IOException
                try {

                    // add to our writing paths
                    markDirty(f);

                    // create our file output stream
                    stream = new FileOutputStream(f.toFile());

                    // write our bytes to the output stream
                    stream.write(data.getBytes(StandardCharsets.UTF_8), 0, data.length());
                }

                // error when writing file
                catch (IOException e) {
                    e.printStackTrace();
                }

                // close the stream
                finally {

                    // if the stream was created, we should close it
                    if (stream != null) {

                        // catches IOException
                        try {
                            stream.close();
                        }

                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // remove from our paths we are writing to
                clean(f);
            }
        }
    }

    @Override
    public void save(String in) {

        // update current option
        modules = main.resolve(in);

        // catches I0Exception
        try {

            // create directories
            Files.createDirectory(modules);
        }

        // error when writing file
        catch (IOException e) {
            e.printStackTrace();
        }

        // run save
        save();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load() {

        // module
        for (Module module : Momentum.MODULE_REGISTRY.getModules()) {

            // path with module name
            Path f = modules.resolve(module.getName() + ".toml");

            // toml reader
            Toml data = new Toml().read(f.toFile());

            // toml object
            if (data.contains(module.getName())) {

                // check all configs
                for (Option<?> option : module.getOptions()) {

                    // ignore enabled
                    if (module instanceof ConcurrentModule && option.getName().equalsIgnoreCase("Enabled")) {
                        continue;
                    }

                    // toml field
                    if (data.contains(module.getName() + option.getName())) {

                        // boolean option
                        if (option.getVal() instanceof Boolean) {

                            // value of the toml field
                            boolean val = data.getBoolean(module.getName() + option.getName());

                            // update option value
                            ((Option<Boolean>) option).setVal(val);
                        }

                        // num option
                        else if (option.getVal() instanceof Number) {

                            // bind option
                            if (option.getName().equalsIgnoreCase("Bind")) {

                                // value of the toml field
                                int val = Keyboard.getKeyIndex(data.getString(module.getName() + option.getName()));

                                // update option value
                                ((Option<Integer>) option).setVal(val);
                            }

                            // int option
                            if (option.getVal() instanceof Integer) {

                                // value of the ctoml field
                                int val = data.getDouble(module.getName() + option.getName()).intValue();

                                // update option value
                                ((Option<Integer>) option).setVal(val);
                            }

                            // int option
                            else if (option.getVal() instanceof Float) {

                                // value of the toml field
                                float val = data.getDouble(module.getName() + option.getName()).floatValue();

                                // update option value
                                ((Option<Float>) option).setVal(val);
                            }

                            // int option
                            if (option.getVal() instanceof Double) {

                                // value of the toml field
                                double val = data.getDouble(module.getName() + option.getName());

                                // update option value
                                ((Option<Double>) option).setVal(val);
                            }
                        }

                        // enum option
                        else if (option.getVal() instanceof Enum<?>) {

                            // value of the toml field
                            String sval = data.getString(module.getName() + option.getName()).toUpperCase();

                            // enum value
                            Enum<?> val = Enum.valueOf(((Enum<?>) option.getVal()).getClass(), sval);

                            // update option value
                            ((Option<Enum>) option).setVal(val);
                        }

                        // color option
                        else if (option.getVal() instanceof Color) {

                            // value of the toml field
                            String sval = data.getString(module.getName() + option.getName()).substring(1);

                            // list of rgb values compiled into a color obj
                            Color val = new Color(Integer.parseInt(sval));

                            // update option value
                            ((Option<Color>) option).setVal(val);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void load(String in) {

        // update current option
        modules = main.resolve(in);

        // catches I0Exception
        try {

            // create directories
            Files.createDirectory(modules);
        }

        // error when writing file
        catch (IOException e) {
            e.printStackTrace();
        }

        // run load
        load();
    }

    /**
     * Formats a module into a .toml data structure
     *
     * @param m The module
     * @return The formatted module data
     */
    @SuppressWarnings("unchecked")
    public String toData(Module m) {

        // module file data
        StringBuilder data = new StringBuilder();

        // create toml object
        data.append("[")
                .append(m.getName())
                .append("]")
                .append("\r\n");

        // write configs
        for (Option<?> option : m.getOptions()) {

            // ignore enabled
            if (m instanceof ConcurrentModule && option.getName().equalsIgnoreCase("Enabled")) {
                continue;
            }

            // option data
            data.append(option.getName())
                    .append(" = ");

            // bind option
            if (option.getName().equalsIgnoreCase("Bind")) {

                // key code
                int keycode = ((Option<Integer>) option).getVal();

                // key name
                String key = Keyboard.getKeyName(keycode);

                // add key to data
                data.append("'")
                        .append(key)
                        .append("'");
            }

            // number configurations
            else if (option.getVal() instanceof Number) {

                // double value
                // toml only supports doubles
                double dval = ((Number) option.getVal()).doubleValue();

                // add dval to data
                data.append(dval);
            }

            // enum option
            else if (option.getVal() instanceof Enum<?>) {

                // enum value as string
                String eval = Formatter.formatEnum((Enum<?>) option.getVal());

                // add string to data
                data.append("'")
                        .append(eval)
                        .append("'");
            }

            // color option
            else if (option.getVal() instanceof Color) {

                // color int
                int c = ((Option<Color>) option).getVal().getRGB();

                // color hex value
                String hexval = "#" + Integer.toHexString(c);

                // add rgba components to data
                data.append("'")
                        .append(hexval)
                        .append("'");
            }

            // all other configurations
            else {

                // attempt to add option value
                data.append(option.getVal());
            }

            // newline
            data.append("\r\n");
        }

        // return compiled data
        return data.toString();
    }
}
