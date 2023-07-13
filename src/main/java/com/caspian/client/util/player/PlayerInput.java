package com.caspian.client.util.player;

import com.caspian.client.util.Globals;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PlayerInput implements Globals
{
    /**
     *
     *
     * @return
     */
    public static boolean isInputtingMovement()
    {
        return mc.player.input.pressingForward
                || mc.player.input.pressingBack
                || mc.player.input.pressingLeft
                || mc.player.input.pressingRight;
    }
}
