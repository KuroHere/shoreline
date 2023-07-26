package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.MovementPacketsEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.string.EnumFormatter;
import com.caspian.client.util.world.EntityUtil;
import com.caspian.client.util.world.VecUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AuraModule extends ToggleModule
{
    // RANGES
    Config<TargetMode> modeConfig = new EnumConfig<>("Mode", "The mode for " +
            "targeting entities to attack", TargetMode.SWITCH,
            TargetMode.values());
    Config<Float> rangeConfig = new NumberConfig<>("Range", "Range to attack " +
            "entities", 1.0f, 4.5f, 5.0f);
    Config<Float> wallRangeConfig = new NumberConfig<>("WallRange", "Range to" +
            " attack entities through walls", 1.0f, 4.5f, 5.0f);
    Config<Vector> hitVectorConfig = new EnumConfig<>("HitVector", "The " +
            "vector to aim for when attacking entities", Vector.FEET,
            Vector.values());
    Config<Integer> ticksExistedConfig = new NumberConfig<>("TicksExisted",
            "The minimum age of the entity to be considered for attack", 0, 0, 200);
    Config<Boolean> stopSprintConfig = new BooleanConfig("StopSprint",
            "Stops sprinting before attacking to maintain vanilla behavior", false);
    //
    Config<Boolean> playersConfig = new BooleanConfig("Players",
            "Target players", true);
    Config<Boolean> monstersConfig = new BooleanConfig("Monsters",
            "Target monsters", false);
    Config<Boolean> neutralsConfig = new BooleanConfig("Neutrals",
            "Target neutrals", false);
    Config<Boolean> animalsConfig = new BooleanConfig("Animals",
            "Target animals", false);
    //
    private boolean sprinting;

    /**
     *
     *
     */
    public AuraModule()
    {
        super("Aura", "Attacks nearby entities", ModuleCategory.COMBAT);
    }

    /**
     *
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        return EnumFormatter.formatEnum(modeConfig.getValue());
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onDisconnect(DisconnectEvent event)
    {
        disable();
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onMovementPackets(MovementPacketsEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (Modules.AUTO_CRYSTAL.isAttacking()
                    || Modules.AUTO_CRYSTAL.isPlacing()
                    || Modules.AUTO_CRYSTAL.isRotating())
            {
                return;
            }


        }
    }

    /**
     *
     *
     * @param entity
     * @return
     */
    private boolean attack(Entity entity)
    {
        if (preAttackCheck())
        {
            preAttack();
            Managers.NETWORK.sendPacket(PlayerInteractEntityC2SPacket.attack(entity,
                    Managers.POSITION.isSneaking()));
            mc.player.resetLastAttackedTicks();
            postAttack();
            return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    private boolean preAttackCheck()
    {

    }

    /**
     *
     *
     */
    private void preAttack()
    {
        sprinting = false;
        if (stopSprintConfig.getValue())
        {
            sprinting = Managers.POSITION.isSprinting();
            if (sprinting)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
        }
    }

    /**
     *
     *
     */
    private void postAttack()
    {
        if (stopSprintConfig.getValue() && sprinting)
        {
            Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
    }

    /**
     *
     *
     * @return
     */
    private Entity getAuraTarget()
    {
        Entity target = null;
        for (Entity e : mc.world.getEntities())
        {
            if (e != null && e.isAlive() && !Managers.SOCIAL.isFriend(e.getUuid()))
            {
                if (e instanceof EndCrystalEntity)
                {
                    continue;
                }
                if (e.age < ticksExistedConfig.getValue())
                {
                    continue;
                }
                final Vec3d eyepos = Managers.POSITION.getEyePos();
                if (isEnemy(e))
                {
                    final Vec3d hitVec = getHitVec(e);
                    //
                    double dist = eyepos.distanceTo(hitVec);
                    if (dist > rangeConfig.getValue())
                    {
                        continue;
                    }
                    BlockHitResult result = mc.world.raycast(new RaycastContext(
                            Managers.POSITION.getCameraPosVec(1.0f),
                            hitVec, RaycastContext.ShapeType.COLLIDER,
                            RaycastContext.FluidHandling.NONE, mc.player));
                    if (result != null && dist > wallRangeConfig.getValue())
                    {
                        continue;
                    }

                    target = e;
                }
            }
        }
        return target;
    }

    /**
     *
     *
     * @return
     */
    private int getWeaponSlot()
    {
        float damage = 0.0f;
        int slot = -1;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            final Item item = stack.getItem();
            if (item instanceof SwordItem sword)
            {
                if (sword.getAttackDamage() > damage)
                {
                    damage = sword.getAttackDamage();
                    slot = i;
                }
            }
            else if (item instanceof MiningToolItem tool)
            {
                if (tool.getAttackDamage() > damage)
                {
                    damage = tool.getAttackDamage();
                    slot = i;
                }
            }
        }
        return slot;
    }

    /**
     *
     *
     * @param entity
     * @return
     */
    public Vec3d getHitVec(Entity entity)
    {
        return switch (hitVectorConfig.getValue())
        {
            case EYES -> VecUtil.toEyePos(entity);
            case TORSO -> VecUtil.toTorsoPos(entity);
            case FEET -> entity.getPos();
        };
    }

    /**
     * Returns <tt>true</tt> if the {@link Entity} is a valid enemy to attack.
     *
     * @param e The potential enemy entity
     * @return <tt>true</tt> if the entity is an enemy
     *
     * @see EntityUtil
     */
    private boolean isEnemy(Entity e)
    {
        return e instanceof PlayerEntity && playersConfig.getValue()
                || EntityUtil.isMonster(e) && monstersConfig.getValue()
                || EntityUtil.isNeutral(e) && neutralsConfig.getValue()
                || EntityUtil.isPassive(e) && animalsConfig.getValue();
    }

    public enum TargetMode
    {
        SWITCH,
        SINGLE
    }

    public enum Vector
    {
        EYES,
        TORSO,
        FEET
    }
}
