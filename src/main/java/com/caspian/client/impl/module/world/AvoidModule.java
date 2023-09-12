package com.caspian.client.impl.module.world;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.world.BlockCollisionEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.world.BlockUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AvoidModule extends ToggleModule
{
    //
    Config<Boolean> voidConfig = new BooleanConfig("Void", "Prevents player " +
            "from falling into the void", true);
    Config<Boolean> fireConfig = new BooleanConfig("Fire", "Prevents player " +
            "from walking into fire", false);
    Config<Boolean> berryBushConfig = new BooleanConfig("BerryBush",
            "Prevents player from walking into sweet berry bushes", false);
    Config<Boolean> cactiConfig = new BooleanConfig("Cactus", "Prevents " +
            "player from walking into cacti", false);
    Config<Boolean> unloadedConfig = new BooleanConfig("Unloaded", "Prevents " +
            "player from entering chunks that haven't been loaded", false);

    /**
     *
     */
    public AvoidModule()
    {
        super("Avoid", "Prevents player from entering harmful areas",
                ModuleCategory.WORLD);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (voidConfig.getValue() && !mc.player.isSpectator()
                && mc.player.getY() < mc.world.getBottomY())
        {
            sendModuleMessage(Formatting.RED + "Prevented player from falling" +
                    " into void!");
            Managers.MOVEMENT.setMotionY(0.0);
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onBlockCollision(BlockCollisionEvent event)
    {
        BlockPos pos = event.getPos();
        if (fireConfig.getValue() && event.getBlock() == Blocks.FIRE
                && mc.player.getY() < pos.getY() + 1.0
                || cactiConfig.getValue()
                && event.getBlock() == Blocks.CACTUS
                || berryBushConfig.getValue()
                && event.getBlock() == Blocks.SWEET_BERRY_BUSH
                || unloadedConfig.getValue()
                && !BlockUtil.isBlockLoaded(pos.getX(), pos.getZ()))
        {
            event.cancel();
            event.setVoxelShape(VoxelShapes.fullCube());
        }
    }
}
