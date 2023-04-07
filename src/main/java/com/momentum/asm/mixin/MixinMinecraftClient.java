package com.momentum.asm.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

/**
 *
 *
 * @author linus
 * @since 04/04/2023
 *
 * @see MinecraftClient
 */
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    /*
     *
    @Inject(method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/" +
                    "MinecraftClient;handleInputEvents()V", shift = At.Shift.AFTER))
    private void hookRunTickKeyboard(CallbackInfo ci)
    {
        TickKeyboardEvent tickKeyboardEvent = new TickKeyboardEvent();
        Momentum.EVENT_HANDLER.dispatch(tickKeyboardEvent);
    }
     */
}
