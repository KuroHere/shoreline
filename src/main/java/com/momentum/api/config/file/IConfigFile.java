package com.momentum.api.config.file;

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
public interface IConfigFile {

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
