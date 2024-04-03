package net.shoreline.client.mixin.option;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(SimpleOption.class)
public class MixinSimpleOption<T> {

    @Shadow
    T value;

    @Shadow
    @Final
    private Consumer<T> changeCallback;

    /**
     *
     * @param value
     * @param ci
     */
    @Inject(method = "setValue", at = @At(value = "HEAD"), cancellable = true)
    private void hookSetValue(T value, CallbackInfo ci) {
        ci.cancel();
        if (!MinecraftClient.getInstance().isRunning()) {
            this.value = value;
            return;
        }
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            this.changeCallback.accept(this.value);
        }
    }
}
