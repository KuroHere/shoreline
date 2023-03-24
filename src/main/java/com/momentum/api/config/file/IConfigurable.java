package com.momentum.api.config.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * Configurable object, all configurations in this object will be locally
 * saved in a <tt>.cfg</tt> file
 *
 * @author linus
 * @since 03/20/2023
 *
 * @param <T> The element type
 */
public interface IConfigurable<T extends JsonElement> {

    // json writer
    Gson gson = new GsonBuilder()
            .setLenient() // leniency to allow reading of .cfg files
            .setPrettyPrinting()
            .create();

    /**
     * Parses the values from a {@link JsonElement} and updates all
     * {@link com.momentum.api.config.Config} values in the objects
     *
     * @param e The Json element
     */
    void fromJson(T e);

    /**
     * Returns configs as a string which will be passed to the
     * {@link ConfigFile} writer and written to a <tt>.cfg</tt> file
     *
     * @return The configs as a parsable Json string
     */
    T toJson();
}
