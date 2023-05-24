package com.caspian.client.util.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SquidEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class EntityUtil
{
    /**
     *
     *
     * @param e
     * @return
     */
    public static boolean isMonster(Entity e)
    {
        return e instanceof Monster;
    }

    /**
     *
     *
     * @param e
     * @return
     */
    public static boolean isNeutral(Entity e)
    {
        return e instanceof Angerable && !((Angerable) e).hasAngerTime();
    }

    /**
     *
     *
     * @param e
     * @return
     */
    public static boolean isPassive(Entity e)
    {
        return e instanceof PassiveEntity || e instanceof AmbientEntity
                || e instanceof SquidEntity;
    }
}
