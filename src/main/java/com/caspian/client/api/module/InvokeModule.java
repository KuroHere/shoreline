package com.caspian.client.api.module;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InvokeModule extends ToggleModule
{
    //
    private boolean running;
    // The number of ticks that the module has been unused (not invoked)
    private final Timer idleTimer = new CacheTimer();

    /**
     *
     *
     * @param name     The module unique identifier
     * @param desc     The module description
     * @param category The module category
     */
    public InvokeModule(String name, String desc, ModuleCategory category)
    {
        super(name, desc, category);
        Caspian.EVENT_HANDLER.subscribe(this);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTickListener(TickEvent event)
    {
        if (isRunning() && idleTimer.passed(250))
        {
            running = false;
            disable();
        }
    }

    /**
     *
     */
    @Override
    public void enable()
    {
        enabledConfig.setValue(true);
        onEnable();
        setRunning(true);
    }

    /**
     *
     */
    @Override
    public void disable()
    {
        enabledConfig.setValue(false);
        onDisable();
        setRunning(false);
    }

    /**
     *
     *
     * @return
     */
    public void setRunning(boolean running)
    {
        this.running = running;
        idleTimer.reset();
        enabledConfig.setValue(running);
        if (running)
        {
            onEnable();
        }
        else
        {
            onDisable();
        }
    }

    /**
     *
     *
     * @return
     */
    public boolean isRunning()
    {
        return mc.player != null && mc.world != null && running;
    }
}
