package com.momentum.impl.modules.client.hud;

import com.momentum.api.feature.Option;
import com.momentum.api.module.Module;
import com.momentum.api.module.ModuleCategory;

/**
 * @author linus
 * @since 02/08/2023
 */
public class HudModule extends Module {

    // hud options
    public final Option<Boolean> watermark =
            new Option<>("Watermark", "Displays watermark", true);
    public final Option<Boolean> direction =
            new Option<>("Direction", "Displays facing direction", true);
    public final Option<Boolean> armor =
            new Option<>("Armor", "Displays equipped armor", true);
    public final Option<Boolean> potionEffects =
            new Option<>("PotionEffects", "Displays active potion effects", true);
    public final Option<PotionHud> potionHud =
            new Option<>("PotionHud", "Displays active potion effects", PotionHud.HIDE);
    public final Option<Boolean> serverBrand =
            new Option<>("ServerBrand", "Displays server brand", true);
    public final Option<Boolean> tps =
            new Option<>("TPS", "Displays server ticks per second", true);
    public final Option<Boolean> fps =
            new Option<>("FPS", "Displays game FPS", true);
    public final Option<Boolean> speed =
            new Option<>("Speed", "Displays player move speed", true);
    public final Option<Boolean> ping =
            new Option<>("Ping", "Displays player ping in ms", true);
    public final Option<Ordering> ordering =
            new Option<>("Ordering", "Arraylist ordering type", Ordering.LENGTH);
    public final Option<Boolean> coordinates =
            new Option<>("Coordinates", "Displays overworld coordinates", true);
    public final Option<Boolean> netherCoordinates =
            new Option<>("NetherCoordinates", "Displays nether coordinates", true);
    public final Option<Boolean> durability =
            new Option<>("Durability", "Displays held item's durability", true);
    public final Option<Boolean> arraylist =
            new Option<>("Arraylist", "Displays list of enabled modules", true);
    public final Option<Rendering> rendering =
            new Option<>("Rendering", "Arraylist rendering position", Rendering.UP);

    // listeners
    RenderTextOverlayListener renderTextOverlayListener
            = new RenderTextOverlayListener(this);
    RenderPotionOverlayListener renderPotionOverlayListener
            = new RenderPotionOverlayListener(this);

    public HudModule() {
        super("Hud", "Heads up display", ModuleCategory.CLIENT);

        // options
        associate(
                watermark,
                direction,
                armor,
                potionEffects,
                potionHud,
                serverBrand,
                tps,
                fps,
                speed,
                ping,
                ordering,
                coordinates,
                netherCoordinates,
                durability,
                arraylist,
                rendering,
                bind,
                drawn
        );

        // listeners
        associate(
                renderTextOverlayListener,
                renderPotionOverlayListener
        );

        // default hidden
        draw(false);
    }
}
