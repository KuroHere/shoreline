package com.caspian.client.asm;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.impl.event.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;
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
@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    //
    @Shadow
    private Profiler profiler;
    @Shadow
    public ClientWorld world;
    @Shadow
    public ClientPlayerEntity player;

    /**
     *
     *
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void hookTickPre(CallbackInfo ci)
    {
        if (player != null && world != null)
        {
            profiler.push("caspian_pre_tick");
            TickEvent tickPreEvent = new TickEvent();
            tickPreEvent.setStage(EventStage.PRE);
            Caspian.EVENT_HANDLER.dispatch(tickPreEvent);
            profiler.pop();
        }
    }

    /**
     *
     *
     * @param ci
     */
    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void hookTickPost(CallbackInfo ci)
    {
        if (player != null && world != null)
        {
            profiler.push("caspian_post_tick");
            TickEvent tickPostEvent = new TickEvent();
            tickPostEvent.setStage(EventStage.POST);
            Caspian.EVENT_HANDLER.dispatch(tickPostEvent);
            profiler.pop();
        }
    }
}
