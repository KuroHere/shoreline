package net.shoreline.client.mixin.render;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.world.SkyboxEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionEffects.class)
public class MixinDimensionEffects {

    /**
     * @param skyAngle
     * @param tickDelta
     * @param cir
     */
    @Inject(method = "getFogColorOverride", at = @At(value = "HEAD"), cancellable = true)
    private void hookGetFogColorOverride(float skyAngle, float tickDelta,
                                         CallbackInfoReturnable<float[]> cir) {
        SkyboxEvent.Fog skyboxEvent = new SkyboxEvent.Fog(tickDelta);
        Shoreline.EVENT_HANDLER.dispatch(skyboxEvent);
        float g = MathHelper.cos(skyAngle * ((float)Math.PI * 2)) - 0.0f;
        if (g >= -0.4f && g <= 0.4f && skyboxEvent.isCanceled()) {
            float i = (g - 0.0f) / 0.4f * 0.5f + 0.5f;
            float j = 1.0f - (1.0f - MathHelper.sin(i * (float)Math.PI)) * 0.99f;
            j *= j;
            Vec3d color = skyboxEvent.getColor();
            cir.cancel();
            cir.setReturnValue(new float[]
                    {
                            (float) color.x, (float) color.y,
                            (float) color.z, j
                    });
        }
    }
}
