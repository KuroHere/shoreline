package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.ScreenOpenEvent;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.DisconnectEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SurroundModule extends ToggleModule
{
    //
    Config<Float> placeRangeConfig = new NumberConfig<>("PlaceRange", "The " +
            "placement range for surround", 0.0f, 4.0f, 5.0f);
    Config<Boolean> rotateConfig = new BooleanConfig("Rotate", "Rotates to " +
            "block before placing", false);
    Config<Boolean> strictDirectionConfig = new BooleanConfig("StrictDirection",
            "Places on visible sides only", false);
    Config<Boolean> attackConfig = new BooleanConfig("Attack", "Attacks " +
            "crystals in the way of surround", true);
    Config<Boolean> extendConfig = new BooleanConfig("Extend", "Extends " +
            "surround if the player is not in the center of a block", true);
    Config<Boolean> jumpDisableConfig = new BooleanConfig("AutoDisable",
            "Disables after moving out of the hole", true);
    //
    private List<BlockPos> surround = new ArrayList<>();
    private List<BlockPos> placements = new ArrayList<>();
    //
    private double prevY;

    /**
     *
     */
    public SurroundModule()
    {
        super("Surround", "Surrounds feet with obsidian", ModuleCategory.COMBAT);
    }

    /**
     *
     */
    @Override
    public void onEnable()
    {
        if (mc.player != null)
        {
            prevY = mc.player.getY();
        }
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
            if (jumpDisableConfig.getValue() && mc.player.getY() > prevY)
            {
                disable();
                return;
            }
            BlockPos pos = BlockPos.ofFloored(mc.player.getX(),
                    mc.player.getY(), mc.player.getZ());
            //
            final List<BlockPos> temp = new ArrayList<>();
            final Set<Direction> intersectDirs = new HashSet<>();
            // surround.clear();
            placements.clear();
            for (Direction dir : Direction.values())
            {
                if (dir == Direction.UP)
                {
                    continue;
                }
                final BlockPos off = pos.offset(dir);
                if (isEntityIntersecting(off))
                {
                    if (extendConfig.getValue())
                    {
                        for (Direction dir2 : Direction.values())
                        {
                            if (dir2 == Direction.UP)
                            {
                                continue;
                            }
                            BlockPos extendOff = off.offset(dir2);
                            if (extendOff == pos)
                            {
                                continue;
                            }
                            if (mc.world.isAir(extendOff))
                            {
                                if (isEntityIntersecting(extendOff))
                                {
                                    intersectDirs.add(dir2);
                                }
                                else
                                {
                                    temp.add(extendOff);
                                }
                            }
                        }
                        if (intersectDirs.size() > 1)
                        {
                            BlockPos corner = pos;
                            for (Direction dir2 : intersectDirs)
                            {
                                corner.offset(dir2);
                            }
                            for (Direction dir2 : Direction.values())
                            {
                                if (dir2 == Direction.UP)
                                {
                                    continue;
                                }
                                BlockPos cornerOff = corner.offset(dir2);
                                if (!isEntityIntersecting(cornerOff))
                                {
                                    temp.add(cornerOff);
                                }
                            }
                        }
                    }
                }
                else
                {
                    temp.add(off);
                }
            }
            surround = temp;
            for (BlockPos p : surround)
            {
                if (mc.world.isAir(p))
                {
                    placements.add(p);
                }
            }
            if (!placements.isEmpty())
            {
                int slot = getSurroundBlockItem();
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
                for (BlockPos p : placements)
                {
                    double dist = mc.player.squaredDistanceTo(p.toCenterPos());
                    if (dist > placeRangeConfig.getValue() * placeRangeConfig.getValue())
                    {
                        continue;
                    }
                    Managers.INTERACT.placeBlock(pos, rotateConfig.getValue(),
                            strictDirectionConfig.getValue());
                }
                if (prev != slot)
                {
                    mc.player.getInventory().selectedSlot = prev;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                }
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player == null)
        {
            return;
        }
        if (event.getPacket() instanceof BlockUpdateS2CPacket packet)
        {
            final BlockState state = packet.getState();
            final BlockPos pos = packet.getPos();
            if (surround.contains(pos) && state.isAir())
            {
                int slot = getSurroundBlockItem();
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
                Managers.INTERACT.placeBlock(pos, rotateConfig.getValue(),
                        strictDirectionConfig.getValue());
                if (prev != slot)
                {
                    mc.player.getInventory().selectedSlot = prev;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                }
            }
        }
        else if (event.getPacket() instanceof PlaySoundS2CPacket packet
                && packet.getCategory() == SoundCategory.BLOCKS
                && packet.getSound().value() == SoundEvents.ENTITY_GENERIC_EXPLODE)
        {
            final BlockPos pos = BlockPos.ofFloored(packet.getX(),
                    packet.getY(), packet.getZ());
            if (surround.contains(pos))
            {
                int slot = getSurroundBlockItem();
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
                Managers.INTERACT.placeBlock(pos, rotateConfig.getValue(),
                        strictDirectionConfig.getValue());
                if (prev != slot)
                {
                    mc.player.getInventory().selectedSlot = prev;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                }
            }
        }
    }

    /**
     *
     * @param pos
     * @return
     */
    private boolean isEntityIntersecting(BlockPos pos)
    {
        for (Entity e : mc.world.getEntities())
        {
            if (e == null || e instanceof ExperienceOrbEntity
                    || e instanceof ItemEntity
                    || e instanceof EndCrystalEntity)
            {
                continue;
            }
            final Box p = new Box(pos);
            if (e.getBoundingBox().intersects(p))
            {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    private int getSurroundBlockItem()
    {
        int slot = -1;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem block
                    && block.getBlock() == Blocks.OBSIDIAN)
            {
                slot = i;
                break;
            }
        }
        if (slot == -1)
        {
            for (int i = 0; i < 9; i++)
            {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() instanceof BlockItem block
                        && block.getBlock() == Blocks.ENDER_CHEST)
                {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }
}
