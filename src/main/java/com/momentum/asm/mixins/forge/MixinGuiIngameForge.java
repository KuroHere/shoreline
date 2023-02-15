package com.momentum.asm.mixins.forge;

import com.momentum.Momentum;
import com.momentum.impl.events.forge.RenderTextOverlayEvent;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {

    /**
     * Called when the HUD text is rendered
     */
    @Inject(method = "renderHUDText", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", shift = At.Shift.AFTER), remap = false)
    private void onRenderHUDText(int width, int height, CallbackInfo ci) {

        // post the render text overlay event
        RenderTextOverlayEvent renderTextOverlayEvent = new RenderTextOverlayEvent();
        Momentum.EVENT_BUS.dispatch(renderTextOverlayEvent);
    }
}
