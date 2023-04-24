package com.caspian;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ShutdownHook extends Thread
{
    /**
     *
     */
    public ShutdownHook()
    {
        setName("Caspian-ShutdownHook");
    }

    /**
     *
     */
    @Override
    public void run()
    {
        Caspian.CONFIG.saveClient();
    }
}
