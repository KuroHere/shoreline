package com.caspian.client;

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
     *
     */
    public ShutdownHook()
    {
        setName("Caspian-ShutdownHook");
    }

    /**
     * This runs when the game is shutdown and saves the
     * {@link com.caspian.client.api.file.ClientConfiguration} files.
     *
     * @see com.caspian.client.api.file.ClientConfiguration#saveClient()
     */
    @Override
    public void run()
    {
        Caspian.info("Saving configurations and shutting down!");
        Caspian.CONFIG.saveClient();
    }
}
