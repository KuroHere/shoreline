package com.caspian.client.impl.module.client;

import com.caspian.client.CaspianMod;
import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.Module;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.render.RenderOverlayEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.Window;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

/**
 * @author linus
 * @since 1.0
 */
public class HudModule extends ToggleModule
{
    //
    // private static final HudScreen HUD_SCREEN = new HudScreen();
    //
    Config<Boolean> watermarkConfig = new BooleanConfig("Watermark",
                    "Displays client name and version watermark", true);
    Config<Boolean> directionConfig = new BooleanConfig("Direction",
            "Displays facing direction", true);
    Config<Boolean> armorConfig = new BooleanConfig("Armor",
            "Displays player equipped armor and durability", true);
    Config<PotionHud> potionHudConfig = new EnumConfig<>("PotionHud", "",
            PotionHud.HIDE, PotionHud.values());
    Config<Boolean> potionEffectsConfig = new BooleanConfig("PotionEffects",
            "Displays active potion effects", true);
    Config<Boolean> coordsConfig = new BooleanConfig("Coords",
            "Displays world coordinates", true);
    Config<Boolean> netherCoordsConfig = new BooleanConfig(
            "NetherCoords", "Displays nether coordinates", true);
    Config<Boolean> serverBrandConfig = new BooleanConfig("ServerBrand",
            "", true);
    Config<Boolean> speedConfig = new BooleanConfig("Speed",
            "", true);
    Config<Boolean> pingConfig = new BooleanConfig("Ping",
            "Display server response time in ms", true);
    Config<Boolean> tpsConfig = new BooleanConfig("TPS",
            "Displays server ticks per second", true);
    Config<Boolean> fpsConfig = new BooleanConfig("FPS",
            "Displays game FPS", true);
    Config<Boolean> arraylistConfig = new BooleanConfig("Arraylist",
            "Displays a list of all active modules", true);
    Config<Ordering> orderingConfig = new EnumConfig<>("Ordering", "",
            Ordering.LENGTH, Ordering.values());

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
     * Called directly after the screen overlay is rendered in
     * {@link net.minecraft.client.gui.hud.InGameHud}.
     *
     * @param event The render overlay event
     */
    @EventListener
    public void onRenderOverlay(RenderOverlayEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            Window res = mc.getWindow();
            // Render offsets for each corner of the screen.
            float topLeft = 2.0f;
            float topRight = topLeft;
            float bottomLeft = res.getScaledHeight() - 11.0f;
            float bottomRight = bottomLeft;
            // center = res.getScaledHeight() - 11 / 2.0f
            if (mc.currentScreen instanceof ChatScreen)
            {
                bottomLeft -= 14.0f;
                bottomRight -= 14.0f;
            }
            if (potionHudConfig.getValue() == PotionHud.MOVE)
            {
                if (!mc.player.getStatusEffects().isEmpty())
                {
                    topRight += 27.0f;
                }
            }
            if (watermarkConfig.getValue())
            {
                RenderManager.renderText(event.getMatrices(), String.format("%s %s.%s",
                                CaspianMod.MOD_NAME,
                                Formatting.WHITE + CaspianMod.MOD_VER,
                                CaspianMod.BUILD_NUMBER), 2.0f,
                        topLeft, Modules.COLORS.getRGB());
                // topLeft += 10.0f;
            }
            if (arraylistConfig.getValue())
            {
                List<Module> modules = (List<Module>) Managers.MODULE.getModules();
                modules = switch (orderingConfig.getValue())
                        {
                            case LENGTH -> modules.stream()
                                    .sorted(Comparator.comparing(m -> m.getName()))
                                    .toList();
                            case ALPHABETICAL -> modules.stream()
                                    .sorted(Comparator.comparing(m -> RenderManager.textWidth(String.format(
                                            "%s [%s]", m.getName(), m.getMetaData()))))
                                    .toList();
                        };
                for (Module m : modules)
                {
                    String text = String.format("%s §7[§f%s§7]", m.getName(),
                            m.getMetaData());
                    int width = RenderManager.textWidth(text);
                    RenderManager.renderText(event.getMatrices(), text,
                            res.getScaledWidth() - width - 1.0f, topRight,
                            Modules.COLORS.getRGB());
                    topRight += 10.0f;
                }
            }
            if (potionEffectsConfig.getValue())
            {
                for (StatusEffectInstance e : mc.player.getStatusEffects())
                {
                    StatusEffect effect = e.getEffectType();
                    String text = String.format("%s %s%s", effect.getName(),
                            e.getAmplifier() > 1 ? e.getAmplifier() + " ":
                                    "" + Formatting.WHITE,
                            StatusEffectUtil.durationToString(e, 1.0f));
                    int width = RenderManager.textWidth(text);
                    RenderManager.renderText(event.getMatrices(), text,
                            res.getScaledWidth() - width - 1.0f, bottomRight,
                            effect.getColor());
                    bottomRight -= 10.0f;
                }
            }
            if (serverBrandConfig.getValue())
            {
                String brand = mc.player.getServerBrand();
                int width = RenderManager.textWidth(brand);
                RenderManager.renderText(event.getMatrices(), brand,
                        res.getScaledWidth() - width - 1.0f, bottomRight,
                        Modules.COLORS.getRGB());
                bottomRight -= 10.0f;
            }
            if (speedConfig.getValue())
            {
                double x = mc.player.getX() - mc.player.prevX;
                // double y = mc.player.getY() - mc.player.prevY;
                double z = mc.player.getZ() - mc.player.prevZ;
                double dist = Math.sqrt(x * x + z * z) / 1000.0;
                double div = 0.05 / 3600.0;
                String speed = Double.toString(dist / div);
                String text = String.format("Speed §f%skm/h",
                        speed.substring(0, speed.indexOf(".") + 2));
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getMatrices(), text,
                        res.getScaledWidth() - width - 1.0f, bottomRight,
                        Modules.COLORS.getRGB());
                bottomRight -= 10.0f;
            }
            if (pingConfig.getValue())
            {
                int latency = 0;
                try
                {
                    if (mc.getNetworkHandler() != null)
                    {
                        for (PlayerListEntry e :
                                mc.getNetworkHandler().getPlayerList())
                        {
                            if (e.getProfile().getId() ==
                                    mc.player.getGameProfile().getId())
                            {
                                latency = e.getLatency();
                                break;
                            }
                        }
                    }
                }
                catch (Exception ignored)
                {

                }
                String text = String.format("Ping §f%dms", latency);
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getMatrices(), text,
                        res.getScaledWidth() - width - 1.0f, bottomRight,
                        Modules.COLORS.getRGB());
                bottomRight -= 10.0f;
            }
            if (tpsConfig.getValue())
            {
                String curr = Float.toString(Managers.TICK.getTpsCurrent());
                String avg = Float.toString(Managers.TICK.getTpsAverage());
                String text = String.format("TPS §f%s §7[§f%s§7]",
                        curr.substring(0, curr.indexOf(".") + 2),
                        avg.substring(0, avg.indexOf(".") + 2));
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getMatrices(), text,
                        res.getScaledWidth() - width - 1.0f, bottomRight,
                        Modules.COLORS.getRGB());
                bottomRight -= 10.0f;
            }
            if (fpsConfig.getValue())
            {
                String text = String.format("FPS §f%d", mc.getCurrentFps());
                int width = RenderManager.textWidth(text);
                RenderManager.renderText(event.getMatrices(), text,
                        res.getScaledWidth() - width - 1.0f, bottomRight,
                        Modules.COLORS.getRGB());
                bottomRight -= 10.0f;
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
                        bottomLeft, Modules.COLORS.getRGB());
                bottomLeft -= 10.0f;
            }
            if (directionConfig.getValue())
            {
                Direction direction = mc.player.getHorizontalFacing();
                Direction.AxisDirection axis = direction.getDirection();
                RenderManager.renderText(event.getMatrices(), String.format("%s [%s]",
                                direction.getName(), Formatting.WHITE + "" +
                                        direction.getAxis() +
                                        (axis == Direction.AxisDirection.POSITIVE ? "+" : "-")
                                        + Formatting.RESET),
                        2, bottomLeft, Modules.COLORS.getRGB());
                // bottomLeft -= 10.0f;
            }
            if (armorConfig.getValue())
            {
                int x = (res.getScaledWidth() / 2) - 78;
                int y = res.getScaledHeight() - 55;
                if (mc.player.isCreative())
                {
                    y -= 15;
                }
                else if (mc.player.isInsideWaterOrBubbleColumn())
                {
                    y -= 10;
                }
                for (int i = 3; i >= 0; --i)
                {
                    ItemStack armor = mc.player.getInventory().armor.get(i);
                    mc.getItemRenderer().renderInGui(event.getMatrices(),
                            armor, x, y);
                    mc.getItemRenderer().renderGuiItemOverlay(event.getMatrices(),
                            mc.textRenderer, armor, x, y);
                    x += 16;
                }
            }
        }
    }

    public enum PotionHud
    {
        MOVE,
        HIDE,
        KEEP
    }

    public enum Ordering
    {
        LENGTH,
        ALPHABETICAL
    }
}
