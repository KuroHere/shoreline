package com.momentum.api.config.file;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Hook thread to runtime shutdown. All hooks will be called when runtime is
 * being shutdown.
 *
 * @author linus
 * @since 03/20/2023
 */
public class ShutdownHook extends Thread {

    // shutdown hooks
    private final List<Runnable> hooks =
            new CopyOnWriteArrayList<>();

    /**
     * Adds the ShutdownHook to the {@link Runtime} shutdown hooks
     */
    public ShutdownHook()
    {

        // add as runtime shutdown hook
        setName("Momentum-ShutdownHook");
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(this);
    }

    /**
     * Runs all hooks when runtime is being shutdown
     */
    @Override
    public void run() {

        // run all hooks
        for (Runnable h : hooks)
        {
            h.run();
        }
    }

    /**
     * Adds a runnable to the shutdown hooks
     *
     * @param r The runnable
     */
    public void hook(Runnable r) {

        // add to hooks
        hooks.add(r);
    }
}
