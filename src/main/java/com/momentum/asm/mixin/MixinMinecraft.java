package com.momentum.asm.mixin;

import com.momentum.Momentum;
import com.momentum.impl.event.TickKeyboardEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    /**
     *
     */
    @Inject(method = "runTickKeyboard",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client" +
                    "/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void hookRunTickKeyboard(CallbackInfo ci)
    {
        TickKeyboardEvent tickKeyboardEvent = new TickKeyboardEvent();
        Momentum.EVENT_HANDLER.dispatch(tickKeyboardEvent);
    }
}
