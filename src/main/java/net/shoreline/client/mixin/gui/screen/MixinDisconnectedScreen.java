package net.shoreline.client.mixin.gui.screen;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.mixin.accessor.AccessorClickableWidget;
import net.shoreline.client.util.Globals;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author linus
 * @since 1.0
 */
@Mixin(DisconnectedScreen.class)
public abstract class MixinDisconnectedScreen extends MixinScreen implements Globals {
    @Shadow
    @Final
    private DirectionalLayoutWidget grid;
    //
    @Unique
    private long reconnectSeconds;

    /**
     * @param ci
     */
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/" +
            "widget/DirectionalLayoutWidget;add(Lnet/minecraft/client/gui/widget/Widget;)" +
            "Lnet/minecraft/client/gui/widget/Widget;", ordinal = 2))
    private void hookInit(CallbackInfo ci) {
        ButtonWidget.Builder reconnectButton = ButtonWidget.builder(Text.of("Reconnect"),
                (button) ->
                {
                    if (Managers.NETWORK.getAddress() != null && Managers.NETWORK.getInfo() != null) {
                        ConnectScreen.connect((DisconnectedScreen) (Object) this, mc,
                                Managers.NETWORK.getAddress(), Managers.NETWORK.getInfo(), false);
                    }
                });
        ButtonWidget.Builder autoReconnectButton = ButtonWidget.builder(
                Text.of("AutoReconnect"), (button) ->
                {
                    Modules.AUTO_RECONNECT.toggle();
                    if (Modules.AUTO_RECONNECT.isEnabled()) {
                        reconnectSeconds = Math.round(Modules.AUTO_RECONNECT.getDelay() * 20.0f);
                    }
                });
        grid.add(autoReconnectButton.build());
        grid.add(reconnectButton.build());
        // addDrawableChild(reconnectButton.dimensions(width / 2 - 100,
        //        height / 2 + mc.textRenderer.fontHeight + 24, 200, 20).build());
        // addDrawableChild(autoReconnectButton.dimensions(width / 2 - 100,
        //        height / 2 + mc.textRenderer.fontHeight + 48, 200, 20).build());
        if (Modules.AUTO_RECONNECT.isEnabled()) {
            reconnectSeconds = Math.round(Modules.AUTO_RECONNECT.getDelay() * 20.0f);
        }
    }

    /**
     *
     */
    @Override
    public void tick() {
        super.tick();
        if (getDrawables().size() > 1) {
            if (Modules.AUTO_RECONNECT.isEnabled()) {
                ((AccessorClickableWidget) getDrawables().get(3)).setMessage(
                        Text.of("AutoReconnect (" + (reconnectSeconds / 20 + 1) + ")"));
                if (reconnectSeconds > 0) {
                    --reconnectSeconds;
                } else if (Managers.NETWORK.getAddress() != null && Managers.NETWORK.getInfo() != null) {
                    ConnectScreen.connect((DisconnectedScreen) (Object) this, mc,
                            Managers.NETWORK.getAddress(), Managers.NETWORK.getInfo(), false);
                }
            } else {
                ((AccessorClickableWidget) getDrawables().get(3)).setMessage(Text.of("AutoReconnect"));
            }
        }
    }
}
