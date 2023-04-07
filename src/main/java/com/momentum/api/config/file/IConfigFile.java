package com.momentum.api.config.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.nio.file.Path;

/**
 * Configuration <b>{@link com.google.gson.JsonElement}</b> file writer.
 * Writes value and name to an output <tt>.json</tt> file. Additionally, can be
 * used to manage a profile system where multiple configuration values may be
 * saved under different {@link File}
 *
 * @author linus
 * @since 03/20/2023
 */
public interface IConfigFile
{
    // client shutdown hooks
    ShutdownHook shutdown = new ShutdownHook();

    // json writer
    Gson gson = new GsonBuilder()
            .setLenient() // leniency to allow reading of .cfg files
            .setPrettyPrinting()
            .create();

    // json parser
    JsonParser parser = new JsonParser();

    /**
     * Saves all configurations to current file
     */
    void save();

    /**
     * Saves to a {@link Path}
     *
     * @param p The path
     */
    void save(Path p);

    /**
     * Loads all configurations from the current file
     */
    void load();

    /**
     * Loads a {@link Path}
     *
     * @param p The path
     */
    void load(Path p);
}
