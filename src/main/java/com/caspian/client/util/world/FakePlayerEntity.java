package com.caspian.client.util.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class FakePlayerEntity extends OtherClientPlayerEntity
{
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
        setId(player.getId());
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
