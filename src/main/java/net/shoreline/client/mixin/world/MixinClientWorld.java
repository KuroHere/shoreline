package net.shoreline.client.mixin.world;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.world.AddEntityEvent;
import net.shoreline.client.impl.event.world.SkyboxEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @author linus
 * @since 1.0
 */
@Mixin(ClientWorld.class)
public class MixinClientWorld {
    /**
     * @param entity
     * @param ci
     */
    @Inject(method = "addEntity", at = @At(value = "HEAD"))
    private void hookAddEntity(Entity entity, CallbackInfo ci) {
        AddEntityEvent addEntityEvent = new AddEntityEvent(entity);
        Shoreline.EVENT_HANDLER.dispatch(addEntityEvent);
    }

    /**
     *
     * @param cir
     */

    @Inject(method = "getEntities", at = @At(value = "HEAD"), cancellable = true)
    private void hookGetEntities(CallbackInfoReturnable<Iterable<Entity>> cir) {
        if (cir.getReturnValue() != null)
        {
            cir.cancel();
            List<Entity> entities = Lists.newArrayList(cir.getReturnValue());
            entities.removeIf(entity -> entity == null);
            cir.setReturnValue(Iterables.unmodifiableIterable(entities));
        }
    }

    /**
     * @param cameraPos
     * @param tickDelta
     * @param cir
     */
    @Inject(method = "getSkyColor", at = @At(value = "HEAD"), cancellable = true)
    private void hookGetSkyColor(Vec3d cameraPos, float tickDelta,
                                 CallbackInfoReturnable<Vec3d> cir) {
        SkyboxEvent.Sky skyboxEvent = new SkyboxEvent.Sky();
        Shoreline.EVENT_HANDLER.dispatch(skyboxEvent);
        if (skyboxEvent.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(skyboxEvent.getColor());
        }
    }

    /**
     * @param tickDelta
     * @param cir
     */
    @Inject(method = "getCloudsColor", at = @At(value = "HEAD"), cancellable = true)
    private void hookGetCloudsColor(float tickDelta,
                                    CallbackInfoReturnable<Vec3d> cir) {
        SkyboxEvent.Cloud skyboxEvent = new SkyboxEvent.Cloud();
        Shoreline.EVENT_HANDLER.dispatch(skyboxEvent);
        if (skyboxEvent.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(skyboxEvent.getColor());
        }
    }
}
