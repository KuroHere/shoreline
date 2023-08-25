package com.caspian.client.mixin.render;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.render.LightmapGammaEvent;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see LightmapTextureManager
 */
@Mixin(LightmapTextureManager.class)
public class MixinLightmapTextureManager
{
    /**
     *
     *
     * @param args
     */
    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet" +
            "/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void hookUpdate(Args args)
    {
        LightmapGammaEvent lightmapGammaEvent =
                new LightmapGammaEvent(args.get(2));
        Caspian.EVENT_HANDLER.dispatch(lightmapGammaEvent);
        if (lightmapGammaEvent.isCanceled())
        {
            args.set(2, lightmapGammaEvent.getGamma());
        }
    }
}
