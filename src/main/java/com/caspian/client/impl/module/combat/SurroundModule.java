package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.PlaceBlockModule;
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
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SurroundModule extends PlaceBlockModule
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
    Config<Boolean> floorConfig = new BooleanConfig("Floor", "Creates a " +
            "floor for the surround if there is none", false);
    Config<Boolean> jumpDisableConfig = new BooleanConfig("AutoDisable",
            "Disables after moving out of the hole", true);
    //
    private List<BlockPos> surround = new ArrayList<>();
    private final List<BlockPos> placements = new ArrayList<>();
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
            // surround.clear();
            placements.clear();
            surround = getSurroundPositions(pos);
            for (BlockPos p : surround)
            {
                if (mc.world.isAir(p))
                {
                    placements.add(p);
                }
            }
            if (!placements.isEmpty())
            {
                placeBlocks(placements, rotateConfig.getValue(),
                        strictDirectionConfig.getValue());
            }
        }
    }

    /**
     *
     *
     * @param pos
     * @return
     */
    public List<BlockPos> getSurroundPositions(BlockPos pos)
    {
        final List<BlockPos> entities = new ArrayList<>();
        entities.add(pos);
        if (extendConfig.getValue())
        {
            for (Direction dir : Direction.values())
            {
                if (!dir.getAxis().isHorizontal())
                {
                    continue;
                }
                BlockPos pos1 = pos.add(dir.getVector());
                //
                List<Entity> box = mc.world.getOtherEntities(null, new Box(pos1));
                if (box.isEmpty())
                {
                    continue;
                }
                for (Entity entity : box)
                {
                    entities.addAll(getAllInBox(entity.getBoundingBox()));
                }
            }
        }
        List<BlockPos> blocks = new ArrayList<>();
        Vec3d playerPos = Managers.POSITION.getEyePos();
        for (BlockPos epos : entities)
        {
            for (Direction dir2 : Direction.values())
            {
                if (!dir2.getAxis().isHorizontal())
                {
                    continue;
                }
                BlockPos pos2 = epos.add(dir2.getVector());
                if (entities.contains(pos2) || blocks.contains(pos2))
                {
                    continue;
                }
                double dist = playerPos.squaredDistanceTo(pos2.toCenterPos());
                if (dist > placeRangeConfig.getValue() * placeRangeConfig.getValue())
                {
                    continue;
                }
                blocks.add(pos2);
            }
        }
        if (floorConfig.getValue())
        {
            for (BlockPos epos1 : entities)
            {
                BlockPos floor = epos1.down();
                double dist = playerPos.squaredDistanceTo(floor.toCenterPos());
                if (dist > placeRangeConfig.getValue() * placeRangeConfig.getValue())
                {
                    continue;
                }
                blocks.add(floor);
            }
        }
        return blocks;
    }

    /**
     *
     * @param box
     * @return
     */
    private List<BlockPos> getAllInBox(Box box)
    {

        double x = Math.ceil(box.maxX - box.minX);
        double z = Math.ceil(box.maxZ - box.minZ);


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
                placeBlock(pos, rotateConfig.getValue(),
                        strictDirectionConfig.getValue());
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
                placeBlock(pos, rotateConfig.getValue(),
                        strictDirectionConfig.getValue());
            }
        }
    }

}
