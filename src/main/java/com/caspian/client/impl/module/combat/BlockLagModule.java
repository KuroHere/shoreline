package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.ScreenOpenEvent;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BlockLagModule extends ToggleModule
{
    //
    Config<Boolean> selfFillConfig = new BooleanConfig("SelfFill", "Fills in " +
            "the block beneath you", false);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates " +
            "before placing the block", false);
    Config<Boolean> attackConfig = new BooleanConfig("Attack", "crystals in " +
            "the way of block", true);
    Config<Boolean> autoDisableConfig = new BooleanConfig("AutoDisable",
            "Automatically disables after placing block", false);
    //
    private BlockPos prevPos;

    /**
     *
     */
    public BlockLagModule()
    {
        super("BlockLag", "Rubberband clips you into a block",
                ModuleCategory.COMBAT);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        if (mc.player == null)
        {
            return;
        }
        prevPos = mc.player.getBlockPos();
    }

    /**
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
     * @param event
     */
    @EventListener
    public void onScreenOpen(ScreenOpenEvent event)
    {
        if (event.getScreen() instanceof DeathScreen)
        {
            disable();
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            if (prevPos != mc.player.getBlockPos() || !mc.player.isOnGround())
            {
                disable();
                return;
            }
            BlockPos pos = BlockPos.ofFloored(mc.player.getX(),
                    mc.player.getY(), mc.player.getZ());
            BlockState state = mc.world.getBlockState(pos);
            if (!isInsideBlock(state))
            {
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), mc.player.getY() + 0.41999998688698,
                        mc.player.getZ(), true));
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), mc.player.getY() + 0.7531999805211997,
                        mc.player.getZ(), true));
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), mc.player.getY() + 1.00133597911214,
                        mc.player.getZ(), true));
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX(), mc.player.getY() + 1.16610926093821,
                        mc.player.getZ(), true));
                Managers.POSITION.setPosition(mc.player.getX(),
                        mc.player.getY() + 1.16610926093821, mc.player.getZ());
                int slot = Modules.SURROUND.getResistantBlockItem();
                if (slot == -1)
                {
                    return;
                }
                int prev = mc.player.getInventory().selectedSlot;
                if (prev != slot)
                {
                    mc.player.getInventory().selectedSlot = slot;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                }
                //
                Managers.INTERACT.placeBlock(pos, rotateConfig.getValue());
                if (selfFillConfig.getValue())
                {
                    Managers.POSITION.setPosition(mc.player.getX(),
                            mc.player.getY() - 0.16610926093821,
                            mc.player.getZ());
                }
                else
                {
                    Managers.POSITION.setPosition(mc.player.getX(),
                            mc.player.getY() - 1.16610926093821, mc.player.getZ());
                    Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                            mc.player.getX(), mc.player.getY() + getLagOffset(),
                            mc.player.getZ(), false));
                }
                if (prev != slot)
                {
                    mc.player.getInventory().selectedSlot = prev;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                }
            }
            if (autoDisableConfig.getValue())
            {
                disable();
            }
        }
    }

    /**
     *
     * @param state
     * @return
     */
    private boolean isInsideBlock(BlockState state)
    {
        return state.getMaterial().blocksMovement() && !mc.player.verticalCollision;
    }

    /**
     *
     * @return
     */
    public double getLagOffset()
    {
        return 3.5;
    }
}
