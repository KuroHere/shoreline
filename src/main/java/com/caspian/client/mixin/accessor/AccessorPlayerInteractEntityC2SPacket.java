package com.caspian.client.mixin.accessor;

import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(PlayerInteractEntityC2SPacket.class)
public interface AccessorPlayerInteractEntityC2SPacket
{
    /**
     *
     *
     * @param entityId
     */
    @Accessor("entityId")
    void hookSetEntityId(int entityId);

    /**
     *
     *
     * @return
     */
    @Accessor("type")
    PlayerInteractEntityC2SPacket.InteractTypeHandler hookGetTypeHandler();
}
