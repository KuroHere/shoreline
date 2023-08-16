package com.caspian.client.mixin.accessor;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(PlayerMoveC2SPacket.class)
public interface AccessorPlayerMoveC2SPacket
{
    /**
     *
     *
     * @param onGround
     */
    @Accessor("onGround")
    @Mutable
    void hookSetOnGround(boolean onGround);
}
