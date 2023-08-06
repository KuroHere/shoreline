package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.block.BlockSlipperinessEvent;
import com.caspian.client.impl.event.block.SteppedOnSlimeBlockEvent;
import com.caspian.client.impl.event.entity.VelocityMultiplierEvent;
import com.caspian.client.impl.event.network.MovementSlowdownEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.impl.event.network.SetCurrentHandEvent;
import com.caspian.client.init.Managers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NoSlowModule extends ToggleModule
{
    //
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "Strict NCP " +
            "bypass for ground slowdowns", false);
    Config<Boolean> airStrictConfig = new BooleanConfig("AirStrict",  "Strict" +
            " NCP bypass for air slowdowns", false);
    Config<Boolean> inventoryMoveConfig = new BooleanConfig("InventoryMove",
            "Allows the player to move while in inventories or screens", false);
    Config<Boolean> arrowMoveConfig = new BooleanConfig("ArrowMove", "Allows " +
            "the player to look while in inventories or screens by using the " +
            "arrow keys", false);
    Config<Boolean> itemsConfig = new BooleanConfig("Items", "Removes " +
            "the slowdown effect caused by using items",
            true);
    Config<Boolean> shieldsConfig = new BooleanConfig("Shields", "Removes the" +
            " slowdown effect caused by shields", true);
    Config<Boolean> websConfig = new BooleanConfig("Webs", "Removes the " +
            "slowdown caused when moving through webs", false);
    Config<Float> webSpeedConfig = new NumberConfig<>("WebSpeed", "Speed to " +
            "fall through webs", 0.0f, 3.5f, 20.0f, () -> websConfig.getValue());
    Config<Boolean> soulsandConfig = new BooleanConfig("SoulSand", "Removes " +
            "the slowdown effect caused by walking over SoulSand blocks", false);
    Config<Boolean> honeyblockConfig = new BooleanConfig("HoneyBlock", "Removes " +
            "the slowdown effect caused by walking over Honey blocks", false);
    Config<Boolean> slimeblockConfig = new BooleanConfig("SlimeBlock",  "Removes " +
            "the slowdown effect caused by walking over Slime blocks", false);
    
    //
    private boolean sneaking;
    //
    private static KeyBinding[] MOVE_KEYBINDS;
    
    /**
     *
     */
    public NoSlowModule()
    {
        super("NoSlow", "Prevents items from slowing down player",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     *
     */
    @Override
    public void onEnable()
    {
        MOVE_KEYBINDS = new KeyBinding[]
            {
                    mc.options.forwardKey,
                    mc.options.backKey,
                    mc.options.rightKey,
                    mc.options.leftKey
            };
    }
    
    /**
     *
     *
     */
    @Override
    public void onDisable()
    {
        if (sneaking && airStrictConfig.getValue())
        {
            Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
        }
        sneaking = false;
    }
    
    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onSetCurrentHand(SetCurrentHandEvent event)
    {
        if (!sneaking && checkSlowed() && airStrictConfig.getValue())
        {
            sneaking = true;
            Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                    ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
        }
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
            if (sneaking && !mc.player.isUsingItem()
                    && airStrictConfig.getValue())
            {
                sneaking = false;
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
            if (checkSlowed())
            {
                // Old NCP
                // Managers.NETWORK.sendSequencedPacket(id ->
                //        new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND,
                //                new BlockHitResult(Vec3d.ZERO, Direction.UP,
                //                        BlockPos.ORIGIN, false), id));
            }
            if (checkScreen() && inventoryMoveConfig.getValue())
            {
                final long handle = mc.getWindow().getHandle();
                for (KeyBinding binding : MOVE_KEYBINDS)
                {
                    binding.setPressed(InputUtil.isKeyPressed(handle,
                            binding.getDefaultKey().getCode()));
                }
                if (arrowMoveConfig.getValue())
                {
                    float yaw = mc.player.getYaw();
                    float pitch = mc.player.getPitch();
                    if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_UP))
                    {
                        pitch -= 3.0f;
                    }
                    else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_DOWN))
                    {
                       pitch += 3.0f;
                    }
                    else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_LEFT))
                    {
                        yaw -= 3.0f;
                    }
                    else if (InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_RIGHT))
                    {
                        yaw += 3.0f;
                    }
                    mc.player.setYaw(yaw);
                    mc.player.setPitch(MathHelper.clamp(pitch, -90.0f, 90.0f));
                }
            }
            final BlockPos pos = Managers.POSITION.getBlockPos();
            final BlockState state = mc.world.getBlockState(pos);
            if (state.getBlock() == Blocks.COBWEB && websConfig.getValue()
                    && !Managers.POSITION.isOnGround())
            {
        
            }
        }
    }
    
    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onMovementSlowdown(MovementSlowdownEvent event)
    {
        final Input input = event.getInput();
        if (checkSlowed())
        {
            input.movementForward *= 5.0f;
            input.movementSideways *= 5.0f;
        }
    }
    
    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onVelocityMultiplier(VelocityMultiplierEvent event)
    {
        if (event.getBlock() == Blocks.SOUL_SAND && soulsandConfig.getValue()
                || event.getBlock() == Blocks.HONEY_BLOCK && honeyblockConfig.getValue())
        {
            event.cancel();
        }
    }
    
    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onSteppedOnSlimeBlock(SteppedOnSlimeBlockEvent event)
    {
        if (slimeblockConfig.getValue())
        {
            event.cancel();
        }
    }
    
    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onBlockSlipperiness(BlockSlipperinessEvent event)
    {
        if (event.getBlock() == Blocks.SLIME_BLOCK
                && slimeblockConfig.getValue())
        {
            event.cancel();
            event.setSlipperiness(0.6f);
        }
    }
    
    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event)
    {
        if (mc.player != null && mc.world != null)
        {
            if (event.getPacket() instanceof PlayerMoveC2SPacket packet)
            {
                if (checkSlowed() && packet.changesPosition()
                        && strictConfig.getValue())
                {
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(
                            Managers.INVENTORY.getServerSlot()));
                }
            }
            else if (event.getPacket() instanceof ClickSlotC2SPacket)
            {
                if (strictConfig.getValue())
                {
                    if (mc.player.isUsingItem())
                    {
                        mc.player.stopUsingItem();
                    }
                    if (sneaking || Managers.POSITION.isSneaking())
                    {
                        Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                                ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                    }
                    if (Managers.POSITION.isSprinting())
                    {
                        Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                                ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                    }
                }
            }
        }
    }
    
    /**
     *
     *
     * @return
     */
    public boolean checkSlowed()
    {
        return !mc.player.isRiding()
                && (mc.player.isUsingItem() && itemsConfig.getValue())
                || (mc.player.isBlocking() && shieldsConfig.getValue());
    }
    
    /**
     *
     *
     * @return
     */
    public boolean checkScreen()
    {
        return mc.currentScreen != null
                && !(mc.currentScreen instanceof ChatScreen
                || mc.currentScreen instanceof SignEditScreen);
    }
}
