package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.impl.event.network.PlayerUpdateEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.world.EntityUtil;
import com.caspian.client.util.world.FakePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BowAimModule extends ToggleModule
{
    //
    Config<Boolean> playersConfig = new BooleanConfig("Players",
            "Aims bow at players", true);
    Config<Boolean> monstersConfig = new BooleanConfig("Monsters",
            "Aims bow at monsters", false);
    Config<Boolean> neutralsConfig = new BooleanConfig("Neutrals",
            "Aims bow at neutrals", false);
    Config<Boolean> animalsConfig = new BooleanConfig("Animals",
            "Aims bow at animals", false);
    Config<Boolean> invisiblesConfig = new BooleanConfig("Invisibles",
            "Aims bow at invisible entities", false);

    /**
     *
     */
    public BowAimModule()
    {
        super("BowAim", "Automatically aims charged bow at nearby entities",
                ModuleCategory.COMBAT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPlayerUpdate(PlayerUpdateEvent event)
    {
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
        if (mc.player.getMainHandStack().getItem() instanceof BowItem
                && mc.player.getItemUseTime() >= 3)
        {
            double minDist = Double.MAX_VALUE;
            Entity aimTarget = null;
            for (Entity entity : mc.world.getEntities())
            {
                if (entity == null || entity == mc.player || !entity.isAlive()
                        || !isValidAimTarget(entity)
                        || Managers.SOCIAL.isFriend(entity.getUuid())
                        || entity instanceof FakePlayerEntity
                        || entity instanceof PlayerEntity player && Modules.ANTI_BOTS.contains(player))
                {
                    continue;
                }
                double dist = mc.player.distanceTo(entity);
                if (dist < minDist)
                {
                    minDist = dist;
                    aimTarget = entity;
                }
            }
            if (aimTarget instanceof LivingEntity target)
            {
                float[] rots = getBowRotationsTo(target);
                mc.player.setYaw(rots[0]);
                mc.player.setHeadYaw(rots[0]);
                mc.player.setPitch(rots[1]);
            }
        }
    }

    /**
     *
     * @param target
     * @return
     */
    private float[] getBowRotationsTo(LivingEntity target)
    {
        double iX = target.getX() - target.prevX;
        double iZ = target.getZ() - target.prevZ;
        double d = mc.player.distanceTo(target);
        d -= d % 2.0;
        iX = d / 2.0 * iX * (mc.player.isSprinting() ? 1.3 : 1.1);
        iZ = d / 2.0 * iZ * (mc.player.isSprinting() ? 1.3 : 1.1);
        float yaw = (float) Math.toDegrees(Math.atan2(target.getZ() + iZ - mc.player.getY(),
                target.getX() + iX - mc.player.getX())) - 90.0f;
        float bowHeldTime = (float) (mc.player.getActiveItem().getMaxUseTime()
                - mc.player.getItemUseTime()) / 20.0f;
        bowHeldTime = (bowHeldTime * bowHeldTime + bowHeldTime * 2.0f) / 3.0f;
        if (bowHeldTime >= 1.0f)
        {
            bowHeldTime = 1.0f;
        }
        double duration = bowHeldTime * 3.0f;
        double coeff = 0.05000000074505806;
        float pitch = (float) (-Math.toDegrees(getArc(target, duration, coeff)));
        return new float[]
                {
                        yaw, pitch
                };
    }

    /**
     *
     * @param target
     * @param duration
     * @param coeff
     * @return
     */
    private float getArc(LivingEntity target, double duration, double coeff)
    {
        double arc = target.getY() + (double) (target.getStandingEyeHeight() / 2.0f)
                        - (mc.player.getY() + (double) mc.player.getStandingEyeHeight());
        double dX = target.getX() - mc.player.getX();
        double dZ = target.getZ() - mc.player.getZ();
        double dir = Math.sqrt(dX * dX + dZ * dZ);
        return getArrowArc(duration, coeff, dir, arc);
    }

    /**
     *
     * @param duration
     * @param coeff
     * @param dir
     * @param arc
     * @return
     */
    private float getArrowArc(double duration, double coeff, double dir, double arc)
    {
        double dirCoeff = coeff * (dir * dir);
        arc = 2.0 * arc * (duration * duration);
        arc = coeff * (dirCoeff + arc);
        arc = Math.sqrt(duration * duration * duration * duration - arc);
        duration = duration * duration - arc;
        arc = Math.atan2(duration * duration + arc, coeff * dir);
        duration = Math.atan2(duration, coeff * dir);
        return (float) Math.min(arc, duration);
    }

    /**
     *
     * @param entity
     * @return
     */
    private boolean isValidAimTarget(Entity entity)
    {
        if (entity.isInvisible() && !invisiblesConfig.getValue())
        {
            return false;
        }
        return entity instanceof PlayerEntity && playersConfig.getValue()
                || EntityUtil.isMonster(entity) && monstersConfig.getValue()
                || EntityUtil.isNeutral(entity) && neutralsConfig.getValue()
                || EntityUtil.isPassive(entity) && animalsConfig.getValue();
    }
}
