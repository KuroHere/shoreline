package com.momentum.api.feature;

import com.momentum.api.util.Wrapper;

/**
 * Simple client feature
 *
 * @author linus
 * @since 01/09/2023
 */
public class Feature implements Wrapper {

    // feature info
    protected final String name;
    protected final String[] aliases;
    protected final String description;

    /**
     * Feature with aliases and description
     *
     * @param name        The name of the feature
     * @param description The description of the feature
     * @param aliases     The aliases of the feature
     */
    public Feature(String name, String[] aliases, String description) {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
    }

    /**
     * Feature with no aliases
     *
     * @param name        The name of the feature
     * @param description The description of the feature
     */
    public Feature(String name, String description) {
        this(name, new String[] {}, description);
    }

    /**
     * Feature
     *
     * @param name The name of the feature
     */
    public Feature(String name) {
        this(name, new String[] {}, "No description.");
    }

    /**
     * Gets the name of the feature
     *
     * @return The name of the feature
     */
    public String getName() {
        return name;
    }

    /**
     * Gets a description of the feature
     *
     * @return A description of the feature
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the aliases of the feature
     *
     * @return The aliases of the feature
     */
    public String[] getAliases() {
        return aliases;
    }
}
