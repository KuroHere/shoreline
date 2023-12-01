package com.caspian.client.impl.module.render;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.Interpolation;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.impl.event.render.entity.RenderLabelEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.world.FakePlayerEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NametagsModule extends ToggleModule
{
    //
    Config<Boolean> armorConfig = new BooleanConfig("Armor", "Displays " +
            "the player's armor", true);
    Config<Boolean> enchantmentsConfig = new BooleanConfig("Enchantments",
            "Displays a list of the item's enchantments", true);
    Config<Boolean> itemNameConfig = new BooleanConfig("ItemName", "Displays " +
            "the player's current held item name", false);
    Config<Boolean> entityIdConfig = new BooleanConfig("EntityId", "Displays " +
            "the player's entity id", false);
    Config<Boolean> gamemodeConfig = new BooleanConfig("Gamemode", "Displays " +
            "the player's gamemode", false);
    Config<Boolean> pingConfig = new BooleanConfig("Ping", "Displays " +
            "the player's server connection ping", true);
    Config<Boolean> healthConfig = new BooleanConfig("Health", "Displays " +
            "the player's current health", true);
    Config<Boolean> totemsConfig = new BooleanConfig("Totems", "Displays " +
            "the player's popped totem count", false);
    Config<Float> scalingConfig = new NumberConfig<>("Scaling", "The nametag " +
            "label scale", 0.001f, 0.003f, 0.01f);
    Config<Boolean> invisiblesConfig = new BooleanConfig("Invisibles",
            "Renders nametags on invisible players", true);
    Config<Boolean> borderedConfig = new BooleanConfig("TextBorder",
            "Renders a border behind the nametag", true);

    /**
     *
     */
    public NametagsModule()
    {
        super("Nametags", "Renders info on player nametags",
                ModuleCategory.RENDER);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (mc.gameRenderer != null && mc.getCameraEntity() != null)
        {
            Vec3d interpolate = Interpolation.getRenderPosition(
                    mc.getCameraEntity(), mc.getTickDelta());
            Camera camera = mc.gameRenderer.getCamera();
            for (Entity entity : mc.world.getEntities())
            {
                if (entity instanceof PlayerEntity player)
                {
                    if (!player.isAlive() || player == mc.player
                            || !invisiblesConfig.getValue() && player.isInvisible())
                    {
                        continue;
                    }
                    String info = getNametagInfo(player);
                    Vec3d pinterpolate = Interpolation.getRenderPosition(
                            player, mc.getTickDelta());
                    double rx = player.getX() - pinterpolate.getX();
                    double ry = player.getY() - pinterpolate.getY();
                    double rz = player.getZ() - pinterpolate.getZ();
                    int width = RenderManager.textWidth(info);
                    float hwidth = width / 2.0f;
                    //
                    double dx = (mc.player.getX() - interpolate.getX()) - rx;
                    double dy = (mc.player.getY() - interpolate.getY()) - ry;
                    double dz = (mc.player.getZ() - interpolate.getZ()) - rz;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist > 4096.0)
                    {
                        continue;
                    }
                    float sdist = (float) Math.max(dist - 8.0, 0.0);
                    float scaling = 0.0245f + scalingConfig.getValue() * sdist;
                    Vec3d render = new Vec3d(rx, ry, rz);
                    renderInfo(info, hwidth, player, render, camera, scaling);
                    renderItems(render, camera);
                }
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onRenderLabel(RenderLabelEvent event)
    {
        if (event.getEntity() instanceof PlayerEntity && event.getEntity() != mc.player)
        {
            event.cancel();
        }
    }

    /**
     *
     * @param info
     * @param width
     * @param entity
     * @param rv
     * @param camera
     * @param scaling
     */
    private void renderInfo(final String info,
                            final float width,
                            final PlayerEntity entity,
                            final Vec3d rv,
                            final Camera camera,
                            final float scaling)
    {
        final Vec3d pos = camera.getPos();
        MatrixStack matrices = new MatrixStack();
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0f));
        matrices.translate(rv.getX() - pos.getX(),
                rv.getY() + (double) entity.getHeight() + (entity.isSneaking() ? 0.4f : 0.43f) - pos.getY(),
                rv.getZ() - pos.getZ());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        matrices.scale(-scaling, -scaling, -1.0f);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        VertexConsumerProvider.Immediate vertexConsumers =
                mc.getBufferBuilders().getEntityVertexConsumers();
        mc.textRenderer.draw(info, -width, 0.0f, getNametagColor(entity),
                true, matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, 0xf000f0);
        // matrices.translate(1.0, 1.0, 0.0);
        // mc.textRenderer.draw(matrices, info, -width, 0.0f, 0x151515);
        vertexConsumers.draw();
        RenderSystem.disableBlend();
        matrices.pop();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }



    /**
     *
     * @param rv
     * @param camera
     */
    private void renderItems(final Vec3d rv,
                             final Camera camera)
    {

    }

    /**
     *
     * @param player
     * @return
     */
    private String getNametagInfo(PlayerEntity player)
    {
        final StringBuilder info = new StringBuilder(player.getEntityName());
        info.append(" ");
        if (entityIdConfig.getValue())
        {
            info.append("ID: ");
            info.append(player.getId());
            info.append(" ");
        }
        if (gamemodeConfig.getValue())
        {
            if (player.isCreative())
            {
                info.append("[C] ");
            }
            else if (player.isSpectator())
            {
                info.append("[I] ");
            }
            else
            {
                info.append("[S] ");
            }
        }
        if (pingConfig.getValue() && mc.getNetworkHandler() != null)
        {
            final PlayerListEntry playerEntry =
                    mc.getNetworkHandler().getPlayerListEntry(player.getGameProfile().getId());
            if (playerEntry != null)
            {
                info.append(playerEntry.getLatency());
                info.append("ms ");
            }
        }
        if (healthConfig.getValue())
        {
            double health = Math.ceil(player.getHealth() + player.getAbsorptionAmount());
            //
            Formatting hcolor;
            if (health > 18) 
            {
                hcolor = Formatting.GREEN;
            }
            else if (health > 16) 
            {
                hcolor = Formatting.DARK_GREEN;
            }
            else if (health > 12)
            {
                hcolor = Formatting.YELLOW;
            }
            else if (health > 8)
            {
                hcolor = Formatting.GOLD;
            }
            else if (health > 4)
            {
                hcolor = Formatting.RED;
            }
            else
            {
                hcolor = Formatting.DARK_RED;
            }
            int phealth = (int) health;
            info.append(hcolor);
            info.append(phealth);
            info.append(" ");
        }
        if (totemsConfig.getValue())
        {
            int totems = Managers.TOTEM.getTotems(player);
            if (totems > 0)
            {
                //
                Formatting pcolor = Formatting.GREEN;;
                if (totems > 1)
                {
                    pcolor = Formatting.DARK_GREEN;
                }
                if (totems > 2)
                {
                    pcolor = Formatting.YELLOW;
                }
                if (totems > 3)
                {
                    pcolor = Formatting.GOLD;
                }
                if (totems > 4)
                {
                    pcolor = Formatting.RED;
                }
                if (totems > 5)
                {
                    pcolor = Formatting.DARK_RED;
                }
                info.append(pcolor);
                info.append(-totems);
                info.append(" ");
            }
        }
        return info.toString().trim();
    }

    /**
     *
     * @param player
     * @return
     */
    private int getNametagColor(PlayerEntity player)
    {
        if (Managers.SOCIAL.isFriend(player.getUuid()))
        {
            return 0xff66ffff;
        }
        if (player.isInvisible())
        {
            return 0xffff2500;
        }
        // fakeplayer
        if (player instanceof FakePlayerEntity)
        {
            return 0xffef0147;
        }
        if (player.isSneaking())
        {
            return 0xffff9900;
        }
        return 0xffffffff;
    }
}
