package net.shoreline.client.mixin.entity.passive;

import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.entity.passive.EntitySteerEvent;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public class MixinAbstractHorseEntity
{
    /**
     *
     * @param cir
     */
    @Inject(method = "isSaddled", at = @At(value = "HEAD"), cancellable = true)
    private void hookIsSaddled(CallbackInfoReturnable<Boolean> cir)
    {
        EntitySteerEvent entitySteerEvent = new EntitySteerEvent();
        Shoreline.EVENT_HANDLER.dispatch(entitySteerEvent);
        if (entitySteerEvent.isCanceled())
        {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
