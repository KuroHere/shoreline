package com.caspian.client.impl.module.world;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.BreakBlockEvent;
import com.caspian.client.impl.event.network.InteractBlockEvent;
import com.caspian.client.init.Managers;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoGlitchBlocksModule extends ToggleModule
{
    //
    Config<Boolean> placeConfig = new BooleanConfig("Place", "Places blocks " +
            "only after the server confirms", true);
    Config<Boolean> destroyConfig = new BooleanConfig("Destroy", "Destroys " +
            "blocks only after the server confirms", true);

    /**
     *
     */
    public NoGlitchBlocksModule()
    {
        super("NoGlitchBlocks", "Prevents blocks from being glitched in the world",
                ModuleCategory.WORLD);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onInteractBlock(InteractBlockEvent event)
    {
        if (placeConfig.getValue() && !mc.isInSingleplayer())
        {
            event.cancel();
            Managers.NETWORK.sendSequencedPacket(id ->
                    new PlayerInteractBlockC2SPacket(event.getHand(), event.getHitResult(), id));
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onBreakBlock(BreakBlockEvent event)
    {
        if (destroyConfig.getValue() && !mc.isInSingleplayer())
        {
            event.cancel();
            BlockState state = mc.world.getBlockState(event.getPos());
            state.getBlock().onBreak(mc.world, event.getPos(), state, mc.player);
        }
    }
}
