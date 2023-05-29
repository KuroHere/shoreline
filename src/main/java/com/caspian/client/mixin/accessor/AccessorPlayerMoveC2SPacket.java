package com.caspian.client.mixin.accessor;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface AccessorPlayerMoveC2SPacket
{
    /**
     *
     *
     * @param onGround
     */
    @Accessor("onGround")
    void hookSetOnGround(boolean onGround);
}
