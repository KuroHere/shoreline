package com.caspian.impl.module.client;

import com.caspian.CaspianMod;
import com.caspian.api.config.Config;
import com.caspian.api.config.setting.BooleanConfig;
import com.caspian.api.event.listener.EventListener;
import com.caspian.api.module.ModuleCategory;
import com.caspian.api.module.ToggleModule;
import com.caspian.api.render.RenderManager;
import com.caspian.impl.event.render.RenderOverlayEvent;
import com.caspian.init.Modules;
import net.minecraft.client.util.Window;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * @author linus
 * @since 1.0
 */
public class HudModule extends ToggleModule
{
    //
    // private static final HudScreen HUD_SCREEN = new HudScreen();

    //
    final Config<Boolean> watermarkConfig = new BooleanConfig("Watermark",
                    "Displays client name and version watermark", true);
    final Config<Boolean> directionConfig = new BooleanConfig("Direction",
            "Displays facing direction", true);
    final Config<Boolean> coordsConfig = new BooleanConfig("Coords",
            "Displays world coordinates", true);
    final Config<Boolean> netherCoordsConfig = new BooleanConfig(
            "NetherCoords", "Displays nether coordinates", true);

    /**
     *
     *
     */
    public HudModule()
    {
        super("HUD", "Displays the HUD (heads up display) screen.",
                ModuleCategory.CLIENT);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onRenderOverlay(RenderOverlayEvent event)
    {
        Window res = mc.getWindow();
        // Render offsets for each corner of the screen.
        float topLeft = 2;
        float topRight = topLeft;
        float bottomLeft = res.getScaledHeight() - 11;
        float bottomRight = bottomLeft;
        // center = res.getScaledHeight() - 11 / 2.0f
        if (mc.world != null && mc.player != null)
        {
            if (watermarkConfig.getValue())
            {
                RenderManager.renderText(event.getMatrices(), String.format("%s %s",
                                CaspianMod.MOD_NAME,
                                Formatting.WHITE + CaspianMod.MOD_VER), 2,
                        topLeft, Modules.COLORS.getColorRGB());
                topLeft += 10;
            }

            if (coordsConfig.getValue())
            {
                String x = Double.toString(mc.player.getX());
                String y = Double.toString(mc.player.getY());
                String z = Double.toString(mc.player.getZ());
                boolean nether = mc.world.getRegistryKey() == World.NETHER;
                String nx = Double.toString(nether ? mc.player.getX() * 8 :
                        mc.player.getX() / 8);
                String nz = Double.toString(nether ? mc.player.getZ() * 8 :
                        mc.player.getZ() / 8);
                RenderManager.renderText(event.getMatrices(), String.format(
                        "XYZ %s, %s, %s" + (netherCoordsConfig.getValue() ?
                                                "[%s, %s]" : ""),
                                Formatting.WHITE + x.substring(0,
                                x.indexOf(".") + 2), y.substring(0,
                                        x.indexOf(".") + 2), z.substring(0,
                                        x.indexOf(".") + 2) + Formatting.RESET,
                                Formatting.WHITE + nx.substring(0,
                                        nx.indexOf("." + 2)), nz.substring(0,
                                        nz.indexOf("." + 2)) + Formatting.RESET), 2,
                        bottomLeft, Modules.COLORS.getColorRGB());
                bottomLeft -= 10;
            }

            if (directionConfig.getValue())
            {
                Direction direction = mc.player.getHorizontalFacing();
                Direction.AxisDirection axis = direction.getDirection();
                RenderManager.renderText(event.getMatrices(), String.format("%s [%s]",
                                direction.getName(),
                                direction.getAxis() + (axis == Direction.AxisDirection.POSITIVE ? "+" : "-")),
                        2, bottomLeft, Modules.COLORS.getColorRGB());
                bottomLeft -= 10;
            }
        }
    }
}
