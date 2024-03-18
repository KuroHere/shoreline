package net.shoreline.client.mixin.gui.screen.ingame;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.gui.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen
{
    /**
     *
     * @param matrices
     * @param stack
     * @param x
     * @param y
     * @param ci
     */
    @Inject(method = "renderTooltip", at = @At(value = "HEAD"), cancellable = true)
    private void hookRenderTooltip(MatrixStack matrices, ItemStack stack,
                                   int x, int y, CallbackInfo ci)
    {
        RenderTooltipEvent renderTooltipEvent =
                new RenderTooltipEvent(matrices, stack, x, y);
        Shoreline.EVENT_HANDLER.dispatch(renderTooltipEvent);
        if (renderTooltipEvent.isCanceled())
        {
            ci.cancel();
        }
    }
}
