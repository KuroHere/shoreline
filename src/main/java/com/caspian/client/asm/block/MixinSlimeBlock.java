package com.caspian.client.asm.block;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.block.SteppedOnSlimeBlockEvent;
import com.caspian.client.util.Globals;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 * @author linus
 * @since 1.0
 *
 * @see SlimeBlock
 */
@Mixin(SlimeBlock.class)
public class MixinSlimeBlock implements Globals
{
    /**
     *
     *
     * @param world
     * @param pos
     * @param state
     * @param entity
     * @param ci
     */
    @Inject(method = "onSteppedOn", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookOnSteppedOn(World world, BlockPos pos, BlockState state,
                                 Entity entity, CallbackInfo ci)
    {
        SteppedOnSlimeBlockEvent steppedOnSlimeBlockEvent =
                new SteppedOnSlimeBlockEvent();
        Caspian.EVENT_HANDLER.dispatch(steppedOnSlimeBlockEvent);
        if (steppedOnSlimeBlockEvent.isCanceled() && entity == mc.player)
        {
            ci.cancel();
        }
    }
}
