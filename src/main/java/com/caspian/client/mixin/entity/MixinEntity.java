package com.caspian.client.mixin.entity;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.entity.EntityPositionEvent;
import com.caspian.client.impl.event.entity.VelocityMultiplierEvent;
import com.caspian.client.impl.event.entity.player.PushEntityEvent;
import com.caspian.client.util.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    //
    @Shadow
    public abstract Box getBoundingBox();

    /**
     * credit - @auto
     *
     * @param movementType
     * @param movement
     * @param ci
     */
    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet" +
            "/minecraft/util/profiler/Profiler;pop()V", ordinal = 0, shift =
            At.Shift.BEFORE))
    private void hookMove(MovementType movementType, Vec3d movement,
                          CallbackInfo ci)
    {
        if (((Entity) (Object) this).equals(mc.player))
        {
            EntityPositionEvent entityPositionEvent =
                    new EntityPositionEvent(getBoundingBox());
            Caspian.EVENT_HANDLER.dispatch(entityPositionEvent);
        }
    }

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

    /**
     *
     * @param entity
     * @param ci
     */
    @Inject(method = "pushAwayFrom", at = @At(value = "HEAD"), cancellable = true)
    private void hookPushAwayFrom(Entity entity, CallbackInfo ci)
    {
        PushEntityEvent pushEntityEvent = new PushEntityEvent();
        Caspian.EVENT_HANDLER.dispatch(pushEntityEvent);
        if (pushEntityEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
