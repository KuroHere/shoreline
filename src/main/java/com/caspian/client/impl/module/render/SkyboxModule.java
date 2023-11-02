package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.ColorConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.config.ConfigUpdateEvent;
import com.caspian.client.impl.event.render.AmbientColorEvent;
import com.caspian.client.impl.event.world.SkyboxEvent;
import com.caspian.client.mixin.accessor.AccessorLightmapTextureManager;
import com.caspian.client.util.world.WorldRenderUtil;

import java.awt.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SkyboxModule extends ToggleModule
{
    //
    Config<Boolean> skyConfig = new BooleanConfig("Sky", "Changes the world " +
            "skybox color", true);
    Config<Color> skyColorConfig = new ColorConfig("SkyColor", "The color for" +
            " the world skybox", ColorConfig.GLOBAL_COLOR);
    Config<Boolean> cloudConfig = new BooleanConfig("Cloud", "Changes the " +
            "world cloud color", false);
    Config<Color> cloudColorConfig = new ColorConfig("CloudColor", "The color" +
            " for the world clouds", ColorConfig.GLOBAL_COLOR);
    Config<Boolean> fogConfig = new BooleanConfig("Fog", "Changes the world " +
            "fog color", false);
    Config<Color> fogColorConfig = new ColorConfig("FogColor", "The color for" +
            " the world fog", ColorConfig.GLOBAL_COLOR);
    Config<Boolean> ambientConfig = new BooleanConfig("Ambient", "Changes the" +
            " world ambient color overlay", true);
    Config<Color> ambientColorConfig = new ColorConfig("AmbientColor", "The " +
            "color for the game overlay", ColorConfig.GLOBAL_COLOR);

    /**
     *
     */
    public SkyboxModule()
    {
        super("Skybox", "Changes the rendering of the world skybox",
                ModuleCategory.RENDER);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        if (mc.player == null || mc.worldRenderer == null)
        {
            return;
        }
        if (ambientConfig.getValue())
        {
            ((AccessorLightmapTextureManager) mc.gameRenderer.getLightmapTextureManager()).setUpdateLightmap(true);
            WorldRenderUtil.reloadRenders(true);
        }
    }

    /**
     *
     */
    @Override
    public void onDisable()
    {
        if (mc.player == null || mc.worldRenderer == null)
        {
            return;
        }
        if (ambientConfig.getValue())
        {
            WorldRenderUtil.reloadRenders(true);
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onConfigUpdate(ConfigUpdateEvent event)
    {
        if (event.getStage() != EventStage.POST || mc.player == null
                || mc.worldRenderer == null)
        {
            return;
        }
        if (event.getConfig() == ambientConfig)
        {
            if (ambientConfig.getValue())
            {
                ((AccessorLightmapTextureManager) mc.gameRenderer.getLightmapTextureManager()).setUpdateLightmap(true);
                WorldRenderUtil.reloadRenders(true);
            }
            else
            {
                WorldRenderUtil.reloadRenders(true);
            }
        }
        else if (event.getConfig() == ambientColorConfig)
        {
            WorldRenderUtil.reloadRenders(true);
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onSkybox(SkyboxEvent.Sky event)
    {
        if (skyConfig.getValue())
        {
            event.cancel();
            event.setColor(skyColorConfig.getValue());
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onSkybox(SkyboxEvent.Cloud event)
    {
        if (cloudConfig.getValue())
        {
            event.cancel();
            event.setColor(cloudColorConfig.getValue());
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onAmbientColor(AmbientColorEvent event)
    {
        if (ambientConfig.getValue())
        {
            event.cancel();
            event.setColor(ambientColorConfig.getValue());
        }
    }
}
