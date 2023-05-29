package com.caspian.client.asm.block;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.block.BlockSlipperinessEvent;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(Block.class)
public class MixinBlock
{
    /**
     *
     *
     * @param cir
     */
    @Inject(method = "getSlipperiness", at = @At(value = "RETURN"),
            cancellable = true)
    private void hookGetSlipperiness(CallbackInfoReturnable<Float> cir)
    {
        Block block = (Block) (Object) this;
        BlockSlipperinessEvent blockSlipperinessEvent =
                new BlockSlipperinessEvent(block, cir.getReturnValueF());
        Caspian.EVENT_HANDLER.dispatch(blockSlipperinessEvent);
        if (blockSlipperinessEvent.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(blockSlipperinessEvent.getSlipperiness());
        }
    }
}
