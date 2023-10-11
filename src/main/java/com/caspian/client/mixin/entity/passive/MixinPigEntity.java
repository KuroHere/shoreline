package com.caspian.client.mixin.entity.passive;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.entity.passive.EntitySteerEvent;
import net.minecraft.entity.passive.PigEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntity.class)
public class MixinPigEntity
{
    /**
     *
     * @param cir
     */
    @Inject(method = "isSaddled", at = @At(value = "HEAD"), cancellable = true)
    private void hookIsSaddled(CallbackInfoReturnable<Boolean> cir)
    {
        EntitySteerEvent entitySteerEvent = new EntitySteerEvent();
        Caspian.EVENT_HANDLER.dispatch(entitySteerEvent);
        if (entitySteerEvent.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
