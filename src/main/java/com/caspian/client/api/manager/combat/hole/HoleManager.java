package com.caspian.client.api.manager.combat.hole;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.util.Globals;
import com.caspian.client.util.world.BlastResistantBlocks;
import com.caspian.client.util.world.BlockUtil;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author linus
 * @since 1.0
 */
public class HoleManager implements Globals
{
    //
    private final List<Hole> holes = new CopyOnWriteArrayList<>();

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (mc.player == null || mc.world == null)
        {
            return;
        }
        if (event.getPacket() instanceof ChunkDataS2CPacket packet)
        {
            final Set<BlockPos> checked = new HashSet<>();
            //
            int x = packet.getX() << 4;
            int z = packet.getZ() << 4;
            for (int i = 0; i < 16; i++)
            {
                for (int j = -64; j < 256; j++)
                {
                    for (int k = 0; k < 16; k++)
                    {
                        BlockPos pos = new BlockPos(x + i, j, z + k);
                        //
                        if (checked.contains(pos))
                        {
                            continue;
                        }
                        Hole hole = checkHole(pos);
                        if (hole != null)
                        {
                            checked.addAll(hole.getHoleOffsets());
                            holes.add(hole);
                        }
                    }
                }
            }
        }
        else if (event.getPacket() instanceof BlockUpdateS2CPacket packet)
        {
            for (Hole hole : holes)
            {
                List<BlockPos> offs = hole.getHoleOffsets();
                if (offs.contains(packet.getPos()))
                {
                    Hole check = checkHole(packet.getPos());
                    if (check == null)
                    {
                        holes.remove(hole);
                        return;
                    }
                }
            }
        }
    }

    /**
     *
     * @param pos
     * @return
     */
    public Hole checkHole(BlockPos pos)
    {
        if (pos.getY() == mc.world.getBottomY() && !BlastResistantBlocks.isUnbreakable(pos))
        {
            return new Hole(pos, HoleSafety.VOID);
        }
        int resistant = 0;
        int unbreakable = 0;
        if (BlockUtil.isBlockAccessible(pos))
        {
            BlockPos pos1 = pos.add(-1, 0, 0);
            BlockPos pos2 = pos.add(0, 0, 1);
            if (BlastResistantBlocks.isBlastResistant(pos1))
            {
                resistant++;
            }
            else if (BlastResistantBlocks.isUnbreakable(pos1))
            {
                unbreakable++;
            }
            if (BlastResistantBlocks.isBlastResistant(pos2))
            {
                resistant++;
            }
            else if (BlastResistantBlocks.isUnbreakable(pos2))
            {
                unbreakable++;
            }
            if (resistant + unbreakable < 2)
            {
                return null;
            }
            BlockPos pos3 = pos.add(0, 0, -1);
            BlockPos pos4 = pos.add(1, 0, 0);
            boolean air3 = mc.world.isAir(pos3);
            boolean air4 = mc.world.isAir(pos4);
            // Quad hole, player can stand in the middle of four blocks
            // to prevent placements on these blocks
            if (air3 && air4)
            {
                BlockPos pos5 = pos.add(1, 0, -1);
                if (!mc.world.isAir(pos5))
                {
                    return null;
                }
                BlockPos[] quad = new BlockPos[]
                        {
                                pos3.add(-1, 0, 0),
                                pos3.add(0, 0, -1),
                                pos4.add(1, 0, 0),
                                pos4.add(0, 0, 1),
                                pos5.add(1, 0, 0),
                                pos5.add(0, 0, -1)
                        };
                for (BlockPos p : quad)
                {
                    if (BlastResistantBlocks.isBlastResistant(p))
                    {
                        resistant++;
                    }
                    else if (BlastResistantBlocks.isUnbreakable(p))
                    {
                        unbreakable++;
                    }
                }
                if (resistant != 8 && unbreakable != 8 && resistant + unbreakable != 8)
                {
                    return null;
                }
                Hole quadHole = new Hole(pos, resistant == 8 ? HoleSafety.RESISTANT :
                        unbreakable == 8 ? HoleSafety.UNBREAKABLE : HoleSafety.MIXED,
                        pos1, pos2, pos3, pos4, pos5);
                quadHole.addHoleOffsets(quad);
                return quadHole;
            }
            // Double Z hole, player can stand in the middle of the blocks
            // to prevent placements on these blocks
            else if (air3 && BlockUtil.isBlockAccessible(pos3))
            {
                BlockPos[] doubleZ = new BlockPos[]
                        {
                                pos.add(1, 0, 0),
                                pos3.add(-1, 0, 0),
                                pos3.add(1, 0, 0),
                                pos3.add(0, 0, -1)
                        };
                for (BlockPos p : doubleZ)
                {
                    if (BlastResistantBlocks.isBlastResistant(p))
                    {
                        resistant++;
                    }
                    else if (BlastResistantBlocks.isUnbreakable(p))
                    {
                        unbreakable++;
                    }
                }
                if (resistant != 6 && unbreakable != 6 && resistant + unbreakable != 6)
                {
                    return null;
                }
                Hole doubleZHole = new Hole(pos, resistant == 6 ? HoleSafety.RESISTANT :
                        unbreakable == 6 ? HoleSafety.UNBREAKABLE : HoleSafety.MIXED,
                        pos1, pos2, pos3);
                doubleZHole.addHoleOffsets(doubleZ);
                return doubleZHole;
            }
            // Double X hole, player can stand in the middle of the blocks
            // to prevent placements on these blocks
            else if (air4 && BlockUtil.isBlockAccessible(pos4))
            {
                BlockPos[] doubleX = new BlockPos[]
                        {
                                pos.add(0, 0, -1),
                                pos4.add(1, 0, 0),
                                pos4.add(0, 0, 1),
                                pos4.add(0, 0, -1)
                        };
                for (BlockPos p : doubleX)
                {
                    if (BlastResistantBlocks.isBlastResistant(p))
                    {
                        resistant++;
                    }
                    else if (BlastResistantBlocks.isUnbreakable(p))
                    {
                        unbreakable++;
                    }
                }
                if (resistant != 6 && unbreakable != 6 && resistant + unbreakable != 6)
                {
                    return null;
                }
                Hole doubleXHole = new Hole(pos, resistant == 6 ? HoleSafety.RESISTANT :
                        unbreakable == 6 ? HoleSafety.UNBREAKABLE : HoleSafety.MIXED,
                        pos1, pos2, pos4);
                doubleXHole.addHoleOffsets(doubleX);
                return doubleXHole;
            }
            // Standard hole, player can stand in them to prevent
            // large amounts of explosion damage
            else
            {
                if (BlastResistantBlocks.isBlastResistant(pos3))
                {
                    resistant++;
                }
                else if (BlastResistantBlocks.isUnbreakable(pos3))
                {
                    unbreakable++;
                }
                if (BlastResistantBlocks.isBlastResistant(pos4))
                {
                    resistant++;
                }
                else if (BlastResistantBlocks.isUnbreakable(pos4))
                {
                    unbreakable++;
                }
                if (resistant != 4 && unbreakable != 4 && resistant + unbreakable != 4)
                {
                    return null;
                }
                return new Hole(pos, resistant == 4 ? HoleSafety.RESISTANT :
                        unbreakable == 4 ? HoleSafety.UNBREAKABLE : HoleSafety.MIXED,
                        pos1, pos2, pos3, pos4);
            }
        }
        return null;
    }

    /**
     *
     * @return
     */
    public List<Hole> getHoles()
    {
        return holes;
    }
}
