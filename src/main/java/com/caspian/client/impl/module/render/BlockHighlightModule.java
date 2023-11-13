package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.BoxRender;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.render.RenderBlockOutlineEvent;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BlockHighlightModule extends ToggleModule
{
    //
    Config<BoxRender> boxModeConfig = new EnumConfig<>("BoxMode", "Box " +
            "rendering mode", BoxRender.OUTLINE, BoxRender.values());
    Config<Boolean> entitiesConfig = new BooleanConfig("Debug-Entities",
            "Highlights entity bounding boxes for debug purposes", false);
    //
    private double distance;

    /**
     *
     *
     */
    public BlockHighlightModule()
    {
        super("BlockHighlight", "Highlights the block the player is facing",
                ModuleCategory.RENDER);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        DecimalFormat decimal = new DecimalFormat("0.0");
        return decimal.format(distance);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        Box render = null;
        final HitResult result = mc.crosshairTarget;
        if (result != null)
        {
            final Vec3d pos = Managers.POSITION.getEyePos();
            if (entitiesConfig.getValue()
                    && result.getType() == HitResult.Type.ENTITY)
            {
                final EntityHitResult entityHit = (EntityHitResult) result;
                final Entity entity = entityHit.getEntity();
                render = entity.getBoundingBox();
                distance = pos.distanceTo(entity.getPos());
            }
            else if (result.getType() == HitResult.Type.BLOCK)
            {
                final BlockHitResult blockHit = (BlockHitResult) result;
                BlockPos hpos = blockHit.getBlockPos();
                render = mc.world.getBlockState(hpos)
                       .getOutlineShape(mc.world, hpos).getBoundingBox();
                // render = new Box(hpos);
                distance = pos.distanceTo(hpos.toCenterPos());
            }
        }
        if (render != null)
        {
            switch (boxModeConfig.getValue())
            {
                case FILL ->
                {
                    RenderManager.renderBox(event.getMatrices(), render,
                            Modules.COLORS.getRGB(60));
                    RenderManager.renderBoundingBox(event.getMatrices(),
                            render, 2.5f, Modules.COLORS.getRGB(145));
                }
                case OUTLINE -> RenderManager.renderBoundingBox(event.getMatrices(),
                        render, 2.5f, Modules.COLORS.getRGB(145));
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onRenderBlockOutline(RenderBlockOutlineEvent event)
    {
        event.cancel();
    }
}
