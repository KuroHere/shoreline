package com.caspian.asm.gui.hud;

import com.caspian.Caspian;
import com.caspian.impl.event.render.RenderOverlayEvent;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see InGameHud
 */
@Mixin(InGameHud.class)
public class MixinInGameHud
{
    /**
     *
     *
     * @param matrices
     * @param tickDelta
     * @param ci
     */
    @Inject(method = "render", at = @At(value = "TAIL"))
    private void hookRender(MatrixStack matrices, float tickDelta,
                            CallbackInfo ci)
    {
        RenderOverlayEvent renderOverlayEvent =
                new RenderOverlayEvent(matrices, tickDelta);
        Caspian.EVENT_HANDLER.dispatch(renderOverlayEvent);
    }
}
