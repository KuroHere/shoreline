package com.caspian.client.api.manager.player.rotation;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see RotationRequest
 * @see RotationManager
 */
public enum RotationPriority
{
    // MAINTAINS MODULE PRIORITY/COMPATIBILITY
    SURROUND(1000),
    SPEEDMINE(980),
    AUTO_CRYSTAL(970),
    AURA(950),
    //
    NORMAL(0),
    ANTI_AIM(-999);

    //
    private final int priority;

    /**
     *
     * @param priority
     */
    RotationPriority(int priority)
    {
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }
}
