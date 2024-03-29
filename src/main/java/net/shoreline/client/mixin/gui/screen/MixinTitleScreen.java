package net.shoreline.client.mixin.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.shoreline.client.BuildConfig;
import net.shoreline.client.ShorelineMod;
import net.shoreline.client.impl.gui.account.AccountSelectorScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author xgraza
 * @since 03/28/24
 */
@Mixin(TitleScreen.class)
public final class MixinTitleScreen extends Screen {

    public MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void hookRender(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo info) {

        context.drawTextWithShadow(client.textRenderer,
                "Shoreline " + ShorelineMod.MOD_VER
                        + " (" + ShorelineMod.MOD_BUILD_NUMBER
                        + "-" + BuildConfig.HASH + ")",
                2, height - (client.textRenderer.fontHeight * 2) - 2, -1);
    }

    @Redirect(method = "initWidgetsNormal", at = @At(
            target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
            value = "INVOKE",
            ordinal = 2))
    public Element hookInit(TitleScreen instance, Element element) {

        // parameters from when the method initWidgetsNormal is called
        final int y = this.height / 4 + 48;
        final int spacingY = 24;

        final ButtonWidget widget = ButtonWidget.builder(Text.of("Account Manager"), (action) -> client.setScreen(new AccountSelectorScreen((Screen) (Object) this)))
            .dimensions(this.width / 2 - 100, y + spacingY * 2, 200, 20)
            .tooltip(Tooltip.of(Text.of("Allows you to switch your in-game account to play Multiplayer")))
            .build();
        widget.active = true;

        return addDrawableChild(widget);
    }
}
