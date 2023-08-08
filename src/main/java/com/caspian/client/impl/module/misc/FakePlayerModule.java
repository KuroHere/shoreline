package com.caspian.client.impl.module.misc;

import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.util.world.FakePlayerEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class FakePlayerModule extends ToggleModule
{
    //
    private FakePlayerEntity fakePlayer;

    /**
     *
     */
    public FakePlayerModule()
    {
        super("FakePlayer", "Spawns an indestructible client-side player",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     *
     */
    @Override
    public void onEnable()
    {
        if (mc.player != null && mc.world != null)
        {
            fakePlayer = new FakePlayerEntity(mc.player, "FakePlayer");
            fakePlayer.spawnPlayer();
        }
    }

    /**
     *
     *
     */
    @Override
    public void onDisable()
    {
        if (mc.world != null)
        {
            fakePlayer.despawnPlayer();
            fakePlayer = null;
        }
    }
}
