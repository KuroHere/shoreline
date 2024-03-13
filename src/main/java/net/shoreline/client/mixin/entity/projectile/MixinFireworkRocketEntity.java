package net.shoreline.client.mixin.entity.projectile;

import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.entity.projectile.RemoveFireworkEvent;
import net.shoreline.client.impl.event.render.entity.RenderFireworkRocketEvent;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(FireworkRocketEntity.class)
public class MixinFireworkRocketEntity
{
    //
    @Shadow
    private int lifeTime;

    /**
     *
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    private void hookTick(CallbackInfo ci)
    {
        RenderFireworkRocketEvent renderFireworkRocketEvent =
                new RenderFireworkRocketEvent();
        Shoreline.EVENT_HANDLER.dispatch(renderFireworkRocketEvent);
        if (renderFireworkRocketEvent.isCanceled())
        {
            ci.cancel();
        }
    }

    /**
     *
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/" +
            "projectile/FireworkRocketEntity;updateRotation()V"))
    private void hookTickPre(CallbackInfo ci)
    {
        int entityId = ((FireworkRocketEntity) (Object) this).getId();
        RemoveFireworkEvent removeFireworkEvent = new RemoveFireworkEvent(entityId);
        Shoreline.EVENT_HANDLER.dispatch(removeFireworkEvent);
        if (removeFireworkEvent.isCanceled())
        {
            lifeTime = Integer.MAX_VALUE;
        }
    }
}
