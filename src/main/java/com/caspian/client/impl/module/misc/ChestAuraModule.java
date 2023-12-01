package com.caspian.client.impl.module.misc;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.RotationModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.InventoryEvent;
import com.caspian.client.util.world.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class ChestAuraModule extends RotationModule
{
    //
    Config<Float> rangeConfig = new NumberConfig<>("Range", "The range to " +
            "automatically open chests", 0.1f, 4.5f, 5.0f);
    Config<Boolean> onGroundConfig = new BooleanConfig("OnGround", "Allows " +
            "chests to be opened only if the player is on the ground", true);
    Config<Float> delayConfig = new NumberConfig<>("Delay", "The delay " +
            "between opening chests", 0.0f, 1.0f, 100.0f);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates " +
            "before opening chests", false);
    //
    private int openDelay;
    //
    private final List<BlockPos> openedBlocks = new ArrayList<>();

    /**
     *
     */
    public ChestAuraModule()
    {
        super("ChestAura", "Automatically opens nearby chest containers",
                ModuleCategory.MISCELLANEOUS);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        openDelay = 0;
        openedBlocks.clear();
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (openDelay > 0 || isRotationBlocked()
                || onGroundConfig.getValue() && !mc.player.isOnGround())
        {
            return;
        }
        // BlockEntity blockEntity = null;
        for (BlockEntity block : BlockUtil.blockEntities())
        {
            if (block.getType() != BlockEntityType.CHEST)
            {
                continue;
            }
            final BlockPos pos = block.getPos();
            double dist = mc.player.squaredDistanceTo(pos.toCenterPos());
            if (dist > rangeConfig.getValue() * rangeConfig.getValue())
            {
                continue;
            }
            if (!openedBlocks.contains(pos))
            {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND,
                        new BlockHitResult(new Vec3d(pos.getX(), pos.getY(),
                                pos.getZ()), Direction.UP, pos, false));
                BlockState state = block.getCachedState();
                if (state.contains(ChestBlock.CHEST_TYPE))
                {
                    Direction direction = state.get(ChestBlock.FACING);
                    switch (state.get(ChestBlock.CHEST_TYPE))
                    {
                        case LEFT -> openedBlocks.add(pos.offset(direction.rotateYClockwise()));
                        case RIGHT -> openedBlocks.add(pos.offset(direction.rotateYCounterclockwise()));
                    }
                }
                openedBlocks.add(pos);
                openDelay = delayConfig.getValue().intValue();
            }
        }
        openDelay--;
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onInventory(InventoryEvent event)
    {
        if (event.getPacket().getSyncId() == mc.player.currentScreenHandler.syncId)
        {
            mc.player.closeHandledScreen();
        }
    }
}
