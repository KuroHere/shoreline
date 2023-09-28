package com.caspian.client.mixin.render.entity;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.render.entity.RenderLivingEntityEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

/**
 * This is fucked
 *
 * @author linus
 * @since 1.0
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer extends EntityRenderer<LivingEntity>
{
    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx)
    {
        super(ctx);
    }

    @Shadow
    protected abstract boolean isVisible(LivingEntity entity);
    @Shadow
    protected abstract @Nullable RenderLayer getRenderLayer(LivingEntity entity,
                                                            boolean showBody,
                                                            boolean translucent,
                                                            boolean showOutline);

    @Shadow
    protected abstract float getAnimationProgress(LivingEntity entity,
                                                  float tickDelta);

    @Shadow
    public static int getOverlay(LivingEntity entity,
                                 float whiteOverlayProgress)
    {
        return 0;
    }

    @Shadow
    protected abstract void scale(LivingEntity entity,
                                  MatrixStack matrices,
                                  float amount);

    @Shadow
    protected abstract void setupTransforms(LivingEntity entity,
                                            MatrixStack matrices,
                                            float animationProgress,
                                            float bodyYaw,
                                            float tickDelta);
    @Shadow
    public static boolean shouldFlipUpsideDown(LivingEntity entity)
    {
        return false;
    }
    @Shadow
    protected abstract float getHandSwingProgress(LivingEntity entity,
                                                  float tickDelta);
    @Shadow
    protected EntityModel<LivingEntity> model;
    @Shadow
    @Final
    protected List<FeatureRenderer<LivingEntity, EntityModel<LivingEntity>>> features;

    /**
     *
     * @param livingEntity
     * @param f
     * @param g
     * @param matrixStack
     * @param vertexConsumerProvider
     * @param i
     * @param ci
     */
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/" +
            "minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/" +
            "render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"), cancellable = true)
    private void hookRender(LivingEntity livingEntity, float f, float g,
                            MatrixStack matrixStack,
                            VertexConsumerProvider vertexConsumerProvider,
                            int i, CallbackInfo ci)
    {
        ci.cancel();
        matrixStack.push();
        this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, g);
        this.model.riding = livingEntity.hasVehicle();
        this.model.child = livingEntity.isBaby();
        float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
        float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
        float k = j - h;
        float l;
        if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity)
        {
            LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
            h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            k = j - h;
            l = MathHelper.wrapDegrees(k);
            if (l < -85.0F)
            {
                l = -85.0F;
            }
            if (l >= 85.0F)
            {
                l = 85.0F;
            }
            h = j - l;
            if (l * l > 2500.0F)
            {
                h += l * 0.2F;
            }
            k = j - h;
        }
        float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
        if (shouldFlipUpsideDown(livingEntity))
        {
            m *= -1.0F;
            k *= -1.0F;
        }
        float n;
        if (livingEntity.isInPose(EntityPose.SLEEPING))
        {
            Direction direction = livingEntity.getSleepingDirection();
            if (direction != null)
            {
                n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
                matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
            }
        }
        l = this.getAnimationProgress(livingEntity, g);
        this.setupTransforms(livingEntity, matrixStack, l, h, g);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(livingEntity, matrixStack, g);
        matrixStack.translate(0.0F, -1.501F, 0.0F);
        n = 0.0F;
        float o = 0.0F;
        if (!livingEntity.hasVehicle() && livingEntity.isAlive())
        {
            n = livingEntity.limbAnimator.getSpeed(g);
            o = livingEntity.limbAnimator.getPos(g);
            if (livingEntity.isBaby())
            {
                o *= 3.0F;
            }
            if (n > 1.0F)
            {
                n = 1.0F;
            }
        }
        this.model.animateModel(livingEntity, o, n, g);
        this.model.setAngles(livingEntity, o, n, l, k, m);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = isVisible(livingEntity);
        boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraftClient.player);
        boolean bl3 = minecraftClient.hasOutline(livingEntity);
        RenderLayer renderLayer = getRenderLayer(livingEntity, bl, bl2, bl3);
        if (renderLayer != null)
        {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            int p = getOverlay(livingEntity, getAnimationProgress(livingEntity, g));
            RenderLivingEntityEvent renderLivingEntityEvent =
                    new RenderLivingEntityEvent(livingEntity, model, matrixStack,
                            vertexConsumer, i, p, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
            Caspian.EVENT_HANDLER.dispatch(renderLivingEntityEvent);
            if (!renderLivingEntityEvent.isCanceled())
            {
                this.model.render(matrixStack, vertexConsumer, i, p, 1.0F, 1.0F, 1.0F, bl2 ? 0.15F : 1.0F);
            }
        }
        if (!livingEntity.isSpectator())
        {
            Iterator var23 = features.iterator();
            while(var23.hasNext())
            {
                FeatureRenderer<LivingEntity, EntityModel<LivingEntity>> featureRenderer = (FeatureRenderer) var23.next();
                featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
            }
        }
        matrixStack.pop();
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
