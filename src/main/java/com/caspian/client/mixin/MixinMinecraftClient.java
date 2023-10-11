package com.caspian.client.mixin;

import com.caspian.client.Caspian;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.impl.event.RunTickEvent;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.ScreenOpenEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
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
    public ClientWorld world;
    @Shadow
    public ClientPlayerEntity player;

    /**
     *
     *
     * @param ci
     */
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet" +
            "/minecraft/client/MinecraftClient;render(Z)V", shift =
            At.Shift.BEFORE))
    private void hookRun(CallbackInfo ci)
    {
        final RunTickEvent runTickEvent = new RunTickEvent();
        Caspian.EVENT_HANDLER.dispatch(runTickEvent);
    }

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
            TickEvent tickPreEvent = new TickEvent();
            tickPreEvent.setStage(EventStage.PRE);
            Caspian.EVENT_HANDLER.dispatch(tickPreEvent);
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
            TickEvent tickPostEvent = new TickEvent();
            tickPostEvent.setStage(EventStage.POST);
            Caspian.EVENT_HANDLER.dispatch(tickPostEvent);
        }
    }

    /**
     *
     * @param screen
     * @param ci
     */
    @Inject(method = "setScreen", at = @At(value = "TAIL"))
    private void hookSetScreen(Screen screen, CallbackInfo ci)
    {
        ScreenOpenEvent screenOpenEvent = new ScreenOpenEvent(screen);
        Caspian.EVENT_HANDLER.dispatch(screenOpenEvent);
    }
}
