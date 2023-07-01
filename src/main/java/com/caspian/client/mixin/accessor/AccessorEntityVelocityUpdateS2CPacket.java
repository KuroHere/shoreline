package com.caspian.client.mixin.accessor;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author Gavin
 * @since 1.0
 */
@Mixin(EntityVelocityUpdateS2CPacket.class)
public interface AccessorEntityVelocityUpdateS2CPacket {

    @Accessor("velocityX") @Final
    void setVelocityX(int velocityX);

    @Accessor("velocityY") @Final
    void setVelocityY(int velocityY);

    @Accessor("velocityZ") @Final
    void setVelocityZ(int velocityZ);

}
