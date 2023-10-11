package com.caspian.client.mixin;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.MouseClickEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse
{
    /**
     *
     * @param window
     * @param button
     * @param action
     * @param mods
     * @param ci
     */
    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods,
                               CallbackInfo ci)
    {
        MouseClickEvent mouseClickEvent = new MouseClickEvent(button, action);
        Caspian.EVENT_HANDLER.dispatch(mouseClickEvent);
        if (mouseClickEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
