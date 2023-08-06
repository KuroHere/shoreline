package com.caspian.client.mixin.entity;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.entity.VelocityMultiplierEvent;
import com.caspian.client.util.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Entity
 */
@Mixin(Entity.class)
public abstract class MixinEntity implements Globals
{
    /**
     *
     *
     * @param instance
     * @return
     */
    @Redirect(method = "getVelocityMultiplier", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getBlock()" +
                    "Lnet/minecraft/ block/Block;"))
    private Block hookGetVelocityMultiplier(BlockState instance)
    {
        if ((Object) this != mc.player)
        {
            return instance.getBlock();
        }
        VelocityMultiplierEvent velocityMultiplierEvent =
                new VelocityMultiplierEvent(instance);
        Caspian.EVENT_HANDLER.dispatch(velocityMultiplierEvent);
        if (velocityMultiplierEvent.isCanceled())
        {
            return Blocks.DIRT;
        }
        return instance.getBlock();
    }
}
