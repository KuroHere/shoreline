package com.caspian.client.util.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class FakePlayerEntity extends OtherClientPlayerEntity
{
    //
    public static final AtomicInteger CURRENT_ID = new AtomicInteger(1000000);
    //
    private final PlayerEntity player;

    /**
     *
     *
     * @param player
     */
    public FakePlayerEntity(PlayerEntity player)
    {
        super(MinecraftClient.getInstance().world, player.getGameProfile());
        this.player = player;
        copyPositionAndRotation(player);
        // setBoundingBox(player.getBoundingBox());
        getInventory().clone(player.getInventory());
        setId(CURRENT_ID.incrementAndGet());
    }

    /**
     *
     *
     * @param player
     * @param pos
     */
    public FakePlayerEntity(PlayerEntity player, Vec3d pos)
    {
        this(player);
        setPosition(pos);
    }

    /**
     *
     *
     * @return
     */
    public PlayerEntity getPlayer()
    {
        return player;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isDead()
    {
        return false;
    }
}
