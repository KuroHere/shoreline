package net.shoreline.client.util.world;

import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.shoreline.client.init.Managers;

import java.util.Comparator;
import java.util.Set;

import static net.shoreline.client.util.Globals.mc;

public class BlockDirection
{

    public static Direction getPlaceDirection(BlockPos blockPos, boolean strictDirection, boolean exposedDirection)
    {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        if (strictDirection)
        {
            final BlockPos playerPos = mc.player.getBlockPos();
            int x1 = playerPos.getX();
            int y1 = (int) Math.floor(mc.player.getY() + mc.player.getStandingEyeHeight());
            int z1 = playerPos.getZ();
            if (x != x1 && y != y1 && z != z1)
            {
                Set<Direction> placeDirsNCP = Managers.NCP.getPlaceDirectionsNCP(
                        x1, y1, z1, x, y, z);
                if (exposedDirection)
                {
                    placeDirsNCP.removeIf(d ->
                    {
                        final BlockPos off = blockPos.offset(d);
                        final BlockState state1 = mc.world.getBlockState(off);
                        return state1.isFullCube(mc.world, off);
                    });
                }
                if (!placeDirsNCP.isEmpty())
                {
                    return placeDirsNCP.stream().min(Comparator.comparing(d ->
                            mc.player.getEyePos().squaredDistanceTo(blockPos.offset(d).toCenterPos()))).orElse(Direction.UP);
                }
            }
        }
        else
        {
            if (mc.world.isInBuildLimit(blockPos))
            {
                return Direction.DOWN;
            }
            BlockHitResult result = mc.world.raycast(new RaycastContext(
                    mc.player.getEyePos(), new Vec3d(x + 0.5, y + 0.5, z + 0.5),
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE, mc.player));
            if (result != null && result.getType() == HitResult.Type.BLOCK)
            {
                return result.getSide();
            }
        }
        return Direction.UP;
    }
    public static Vec3d getDirectionVec3d(BlockPos pos, Direction direction)
    {
        return null;
    }
}
