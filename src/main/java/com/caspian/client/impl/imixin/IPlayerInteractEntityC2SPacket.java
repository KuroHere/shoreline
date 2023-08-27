package com.caspian.client.impl.imixin;

import com.caspian.client.util.network.InteractType;
import net.minecraft.entity.Entity;

/**
 *
 *
 */
public interface IPlayerInteractEntityC2SPacket
{
    /**
     *
     * @return
     */
    Entity getEntity();

    /**
     *
     * @return
     */
    InteractType getType();
}
