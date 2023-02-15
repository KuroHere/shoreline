package com.momentum.impl.modules.render.norender;

import com.momentum.api.feature.Option;
import com.momentum.api.module.Module;
import com.momentum.api.module.ModuleCategory;

/**
 * @author linus
 * @since 02/13/2023
 */
public class NoRenderModule extends Module {

    // overlay options
    public final Option<Boolean> fire =
            new Option<>("Fire", "Removes the fire overlay", true);
    public final Option<Boolean> hurtCamera =
            new Option<>("HurtCamera", new String[] {"NoHurtCam"}, "Removes the hurt camera effect", true);
    public final Option<Boolean> bossOverlay =
            new Option<>("BossOverlay", "Removes the boss bar overlay", true);
    public final Option<Boolean> pumpkinOverlay =
            new Option<>("PumpkinOverlay", "Removes the pumpkin overlay", true);
    public final Option<Boolean> blockOverlay =
            new Option<>("BlockOverlay", "Removes the block overlay", true);

    // render options
    public final Option<Boolean> blindness =
            new Option<>("Blindness", "Removes blindness effect", true);
    public final Option<Boolean> totemAnimation =
            new Option<>("TotemAnimation", "Removes the totem pop animation", false);
    public final Option<Boolean> signText =
            new Option<>("SignText", "Removes sign text rendering", false);
    public final Option<Boolean> armor =
            new Option<>("Armor", "Removes armor rendering", false);
    public final Option<Boolean> explosions =
            new Option<>("Explosions", "Removes the explosion particle effect", true);

    // listeners
    public final RenderHudOverlayListener renderHudOverlayListener =
            new RenderHudOverlayListener(this);
    public final RenderPumpkinOverlayListener renderPumpkinOverlayListener =
            new RenderPumpkinOverlayListener(this);
    public final RenderBossOverlayListener renderBossOverlayListener =
            new RenderBossOverlayListener(this);
    public final RenderHurtCameraListener renderHurtCameraListener =
            new RenderHurtCameraListener(this);
    public final RenderBlindnessListener renderBlindnessListener =
            new RenderBlindnessListener(this);
    public final RenderArmorModelListener renderArmorModelListener =
            new RenderArmorModelListener(this);
    public final RenderItemActivationListener renderItemActivationListener =
            new RenderItemActivationListener(this);

    public NoRenderModule() {
        super("NoRender", "Prevents certain elements from rendering", ModuleCategory.RENDER);

        // options
        associate(
                fire,
                hurtCamera,
                bossOverlay,
                pumpkinOverlay,
                blockOverlay,
                blindness,
                totemAnimation,
                signText,
                armor,
                explosions,
                bind,
                drawn
        );

        // listeners
        associate(
                renderHudOverlayListener,
                renderPumpkinOverlayListener,
                renderBossOverlayListener,
                renderHurtCameraListener,
                renderBlindnessListener,
                renderArmorModelListener,
                renderItemActivationListener
        );
    }
}
