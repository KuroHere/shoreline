package com.caspian.client.mixin.entity.player;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.entity.player.PlayerJumpEvent;
import com.caspian.client.impl.event.entity.player.PushFluidsEvent;
import com.caspian.client.util.Globals;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements Globals
{
    /**
     *
     * @param cir
     */
    @Inject(method = "isPushedByFluids", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookIsPushedByFluids(CallbackInfoReturnable<Boolean> cir)
    {
        if ((Object) this != mc.player)
        {
            return;
        }
        PushFluidsEvent pushFluidsEvent = new PushFluidsEvent();
        Caspian.EVENT_HANDLER.dispatch(pushFluidsEvent);
        if (pushFluidsEvent.isCanceled())
        {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    /**
     *
     * @param ci
     */
    @Inject(method = "jump", at = @At(value = "HEAD"), cancellable = true)
    private void hookJump(CallbackInfo ci)
    {
        if ((Object) this != mc.player)
        {
            return;
        }
        PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent();
        Caspian.EVENT_HANDLER.dispatch(playerJumpEvent);
        if (playerJumpEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
