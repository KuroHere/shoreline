package com.caspian.client.impl.module.world;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.api.render.RenderManager;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.AttackBlockEvent;
import com.caspian.client.impl.event.render.RenderWorldEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SpeedmineModule extends ToggleModule
{
    //
    Config<Boolean> instantConfig = new BooleanConfig("Instant",
            "Instantly removes the mining block", false);
    Config<Float> rangeConfig = new NumberConfig<>("Range", "Range for mine",
            1.0f, 4.5f, 5.0f);
    Config<Swap> swapConfig = new EnumConfig<>("Swap", "Swaps to the best " +
            "tool once the mining is complete", Swap.SILENT, Swap.values());
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "Swaps to tool" +
            " using alternative packets", false);
    Config<Boolean> remineConfig = new BooleanConfig("Remine",
            "Attempts to remine blocks", false);
    Config<Boolean> fastConfig = new BooleanConfig("Fast", "Attempts to " +
            "instantly remine blocks", false);
    Config<Boolean> remineFullConfig = new BooleanConfig("RemineFull",
            "Resets the block breaking state when remining", true);
    Config<Boolean> infiniteRemineConfig = new BooleanConfig("InfiniteRemine",
            "Attempts to remine blocks infinitely", false);
    Config<Integer> maxRemineConfig = new NumberConfig<>("MaxRemines",
            "Maximum remines of a block before reset", 0, 2, 10);

    // Mining block info
    private BlockPos mining;
    private BlockState state;
    private Direction direction;
    private float damage;
    // Number of remines on the current mining block.
    private int remines;

    /**
     *
     */
    public SpeedmineModule()
    {
        super("Speedmine", "Mines faster", ModuleCategory.WORLD);
    }

    /**
     *
     *
     */
    @Override
    public void onDisable()
    {
        mining = null;
        state = null;
        direction = null;
        damage = 0.0f;
        remines = 0;
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            if (!mc.player.isCreative())
            {
                if (mining != null)
                {
                    state = mc.world.getBlockState(mining);
                    int prev = mc.player.getInventory().selectedSlot;
                    int slot = getBestTool(state);
                    //
                    double dist = mc.player.squaredDistanceTo(mining.toCenterPos());
                    if (dist > rangeConfig.getValue() * rangeConfig.getValue()
                            || state.isAir() || damage > 3.0f
                            || !remineConfig.getValue()
                            || !infiniteRemineConfig.getValue()
                            && remines > maxRemineConfig.getValue())
                    {
                        mining = null;
                        state = null;
                        direction = null;
                        damage = 0.0f;
                        remines = 0;
                    }
                    else if (damage > 1.0f)
                    {
                        if (!Modules.AUTO_CRYSTAL.isAttacking()
                                && !Modules.AUTO_CRYSTAL.isPlacing()
                                && !Modules.AUTO_CRYSTAL.isRotating())
                        {
                            if (swapConfig.getValue() == Swap.NORMAL)
                            {
                                swap(slot);
                            }
                            else if (swapConfig.getValue() == Swap.SILENT)
                            {
                                if (strictConfig.getValue())
                                {
                                    // Managers.NETWORK.sendPacket(new ClickSlotC2SPacket(
                                    //        mc.player.getInventory(),
                                    //        mc.player.getInventory().selectedSlot,
                                    //        SlotActionType.SWAP,
                                    //        mc.player));
                                }
                                else
                                {
                                    swap(slot);
                                }
                            }
                            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                                    mining, direction));
                            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                                    mining, Direction.UP));
                            if (fastConfig.getValue())
                            {
                                Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                                        PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                                        mining, direction));
                            }
                            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
                                    mining, direction));
                            if (swapConfig.getValue() == Swap.SILENT)
                            {
                                if (prev != -1)
                                {
                                    if (strictConfig.getValue())
                                    {

                                    }
                                    else
                                    {
                                        swap(prev);
                                    }
                                }
                            }
                            // reset, this position needs to be remined if we
                            // attempt to mine again
                            if (remineFullConfig.getValue())
                            {
                                damage = 0.0f;
                            }
                            remines++;
                        }
                    }
                    else
                    {
                        damage += calcBlockBreakingDelta(state, mc.world,
                                mining);
                    }
                }
                else
                {
                    damage = 0.0f;
                }
            }
        }
    }

    /**
     *
     *
     * @param slot
     */
    private void swap(int slot)
    {
        mc.player.getInventory().selectedSlot = slot;
        Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onAttackBlock(AttackBlockEvent event)
    {
        ClientWorld world = mc.world;
        if (mc.player != null && world != null)
        {
            state = mc.world.getBlockState(event.getPos());
            if (!mc.player.isCreative() && isBreakable(state)
                    && !world.getWorldBorder().contains(event.getPos())
                    && !state.isAir())
            {
                if (mining != event.getPos())
                {
                    mining = event.getPos();
                    direction = event.getDirection();
                    damage = 0.0f;
                    remines = 0;
                    if (mining != null && direction != null)
                    {
                        Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.START_DESTROY_BLOCK,
                                mining, direction));
                        Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                                PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK,
                                mining, Direction.UP));
                        if (instantConfig.getValue())
                        {
                            mc.world.removeBlock(mining, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the hotbar slot of the best {@link ToolItem} for the
     * {@link BlockState} of the mining {@link BlockPos}
     *
     * @param state The block state of the mining position
     * @return The hotbar slot of the best tool
     */
    public int getBestTool(BlockState state)
    {
        int slot = 0;
        float best = 0.0f;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ToolItem)
            {
                float speed = stack.getMiningSpeedMultiplier(state);
                int efficiency = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY,
                        stack);
                if (efficiency > 0)
                {
                    speed += efficiency * efficiency + 1.0;
                }
                if (speed > best)
                {
                    best = speed;
                    slot = i;
                }
            }
        }
        return slot;
    }

    /**
     *
     *
     * @param state
     * @param world
     * @param pos
     * @return
     */
    private float calcBlockBreakingDelta(BlockState state, BlockView world,
                                         BlockPos pos)
    {
        if (swapConfig.getValue() == Swap.OFF)
        {
            return state.calcBlockBreakingDelta(mc.player, mc.world, pos);
        }
        float f = state.getHardness(world, pos);
        if (f == -1.0f)
        {
            return 0.0f;
        }
        else
        {
            int i = canHarvest(state) ? 30 : 100;
            return getBlockBreakingSpeed(state) / f / (float) i;
        }
    }

    /**
     *
     *
     * @param block
     * @return
     */
    private float getBlockBreakingSpeed(BlockState block)
    {
        int tool = getBestTool(block);
        float f = mc.player.getInventory().getStack(tool).getMiningSpeedMultiplier(block);
        if (f > 1.0F)
        {
            int i = EnchantmentHelper.getEfficiency(mc.player);
            ItemStack stack = mc.player.getInventory().getStack(tool);
            if (i > 0 && !stack.isEmpty())
            {
                f += (float) (i * i + 1);
            }
        }
        if (StatusEffectUtil.hasHaste(mc.player))
        {
            f *= 1.0f + (float) (StatusEffectUtil.getHasteAmplifier(mc.player) + 1) * 0.2f;
        }
        if (mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE))
        {
            float g = switch (mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier())
            {
                case 0 -> 0.3f;
                case 1 -> 0.09f;
                case 2 -> 0.0027f;
                default -> 8.1e-4f;
            };
            f *= g;
        }
        if (mc.player.isSubmergedIn(FluidTags.WATER)
                && !EnchantmentHelper.hasAquaAffinity(mc.player))
        {
            f /= 5.0f;
        }
        if (!Managers.POSITION.isOnGround())
        {
            f /= 5.0f;
        }
        return f;
    }

    /**
     * Determines whether the player is able to harvest drops from the specified block state.
     * If a block requires a special tool, it will check
     * whether the held item is effective for that block, otherwise
     * it returns {@code true}.
     *
     * @param state
     *
     * @see net.minecraft.item.Item#isSuitableFor(BlockState)
     */
    private boolean canHarvest(BlockState state)
    {
        if (state.isToolRequired())
        {
            int tool = getBestTool(state);
            return mc.player.getInventory().getStack(tool).isSuitableFor(state);
        }
        return true;
    }

    /**
     * Returns <tt>true</tt> if the {@link BlockState} of the mining block is
     * breakable in survival mode
     *
     * @param state The block state of the mining block
     * @return <tt>true</tt> if the mining block is breakable
     */
    public boolean isBreakable(BlockState state)
    {
        return state.getBlock() != Blocks.BEDROCK
                && state.getBlock() != Blocks.BARRIER
                && state.getBlock() != Blocks.COMMAND_BLOCK
                && state.getBlock() != Blocks.CHAIN_COMMAND_BLOCK
                && state.getBlock() != Blocks.REPEATING_COMMAND_BLOCK;
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onRenderWorld(RenderWorldEvent event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (mining != null && !mc.player.isCreative())
            {
                Vec3d center = new Box(mining).getCenter();
                Box scaled = new Box(center.getX(), center.getY(),
                        center.getZ(), center.getX(), center.getY(), center.getZ());
                float scale = damage;
                if (scale > 1.0f)
                {
                    scale = 1.0f;
                }
                scaled = scaled.expand(0.5 * scale);
                RenderManager.renderBox(scaled, damage > 0.95f ? 0x6000ff00 :
                        0x60ff0000);
            }
        }
    }

    public enum Swap
    {
        NORMAL,
        SILENT,
        OFF
    }
}
