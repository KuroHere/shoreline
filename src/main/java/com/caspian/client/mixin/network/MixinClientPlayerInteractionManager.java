package com.caspian.client.mixin.network;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.network.AttackBlockEvent;
import com.caspian.client.impl.event.network.BreakBlockEvent;
import com.caspian.client.impl.event.network.InteractBlockEvent;
import com.caspian.client.impl.event.network.ReachEvent;
import com.caspian.client.util.Globals;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
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
public class MixinClientPlayerInteractionManager implements Globals
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
        BlockState state = mc.world.getBlockState(pos);
        final AttackBlockEvent attackBlockEvent = new AttackBlockEvent(
                pos, state, direction);
        Caspian.EVENT_HANDLER.dispatch(attackBlockEvent);
        if (attackBlockEvent.isCanceled())
        {
            cir.cancel();
            // cir.setReturnValue(false);
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
        final ReachEvent reachEvent = new ReachEvent();
        Caspian.EVENT_HANDLER.dispatch(reachEvent);
        if (reachEvent.isCanceled())
        {
            cir.cancel();
            float reach = gameMode.isCreative() ? 5.0f : 4.5f;;
            cir.setReturnValue(reach + reachEvent.getReach());
        }
    }

    /**
     *
     * @param player
     * @param hand
     * @param hitResult
     * @param cir
     */
    @Inject(method = "interactBlock", at = @At(value = "HEAD"), cancellable = true)
    private void hookInteractBlock(ClientPlayerEntity player, Hand hand,
                                   BlockHitResult hitResult,
                                   CallbackInfoReturnable<ActionResult> cir)
    {
        InteractBlockEvent interactBlockEvent = new InteractBlockEvent(
                player, hand, hitResult);
        Caspian.EVENT_HANDLER.dispatch(interactBlockEvent);
        if (interactBlockEvent.isCanceled())
        {
            cir.setReturnValue(ActionResult.SUCCESS);
            cir.cancel();
        }
    }

    /**
     *
     * @param pos
     * @param cir
     */
    @Inject(method = "breakBlock", at = @At(value = "HEAD"), cancellable = true)
    private void hookBreakBlock(BlockPos pos,
                                CallbackInfoReturnable<Boolean> cir)
    {
        BreakBlockEvent breakBlockEvent = new BreakBlockEvent(pos);
        Caspian.EVENT_HANDLER.dispatch(breakBlockEvent);
        if (breakBlockEvent.isCanceled())
        {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
