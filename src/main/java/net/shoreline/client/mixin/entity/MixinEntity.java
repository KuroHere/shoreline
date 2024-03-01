package net.shoreline.client.mixin.entity;

import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.entity.VelocityMultiplierEvent;
import net.shoreline.client.impl.event.entity.player.PushEntityEvent;
import net.shoreline.client.util.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        Shoreline.EVENT_HANDLER.dispatch(velocityMultiplierEvent);
        if (velocityMultiplierEvent.isCanceled())
        {
            return Blocks.DIRT;
        }
        return instance.getBlock();
    }

    /**
     *
     * @param entity
     * @param ci
     */
    @Inject(method = "pushAwayFrom", at = @At(value = "HEAD"), cancellable = true)
    private void hookPushAwayFrom(Entity entity, CallbackInfo ci)
    {
        PushEntityEvent pushEntityEvent = new PushEntityEvent();
        Shoreline.EVENT_HANDLER.dispatch(pushEntityEvent);
        if (pushEntityEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
