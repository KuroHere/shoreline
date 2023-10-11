package com.caspian.client.mixin.network;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.network.AttackBlockEvent;
import com.caspian.client.impl.event.network.ReachEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see ClientPlayerInteractionManager
 */
@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager
{
    //
    @Shadow
    private GameMode gameMode;

    /**
     *
     *
     * @param pos
     * @param direction
     * @param cir
     */
    @Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
    private void hookAttackBlock(BlockPos pos, Direction direction,
                                 CallbackInfoReturnable<Boolean> cir)
    {
        AttackBlockEvent attackBlockEvent = new AttackBlockEvent(pos,
                direction);
        Caspian.EVENT_HANDLER.dispatch(attackBlockEvent);
        if (attackBlockEvent.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }

    /**
     *
     * @param cir
     */
    @Inject(method = "getReachDistance", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookGetReachDistance(CallbackInfoReturnable<Float> cir)
    {
        ReachEvent reachEvent = new ReachEvent();
        Caspian.EVENT_HANDLER.dispatch(reachEvent);
        if (reachEvent.isCanceled())
        {
            cir.cancel();
            float reach = gameMode.isCreative() ? 5.0f : 4.5f;;
            cir.setReturnValue(reach + reachEvent.getReach());
        }
    }
}
