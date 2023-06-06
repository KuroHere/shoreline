package com.caspian.client.impl.module.combat;

import com.caspian.client.Caspian;
import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.ItemConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.init.Modules;
import com.caspian.client.util.world.EndCrystalUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.math.Direction;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoTotemModule extends ToggleModule
{
    //
    Config<Item> offhandConfig = new ItemConfig("Offhand", "Item to keep" +
            " in offhand when in a safe environment", Items.TOTEM_OF_UNDYING,
            new Item[]
                    {
                            Items.TOTEM_OF_UNDYING,
                            Items.END_CRYSTAL,
                            Items.GOLDEN_APPLE
                    });
    Config<Float> healthConfig = new NumberConfig<>("Health", "Lethal offhand" +
            " health", 0.0f, 0.0f, 20.0f);
    Config<Boolean> lethalConfig = new BooleanConfig("Lethal",
            "Calculate lethal damage sources and place totem in offhand",
            false);
    Config<Boolean> instantConfig = new BooleanConfig("Instant",
            "Attempt to instantly replace totem in offhand after popping a " +
                    "totem", false);
    Config<Boolean> fallbackCrystalConfig = new BooleanConfig(
            "FallbackCrystal", "Fallback to END_CRYSTAL when there are no " +
            "totems", true);
    Config<Boolean> strictActionsConfig = new BooleanConfig("StrictActions",
            "Stops actions before clicking slots", false);
    Config<Boolean> strictMoveConfig = new BooleanConfig("StrictMove",
            "Stops motion before clicking slots", false);
    Config<Boolean> offhandOverrideConfig = new BooleanConfig(
            "OffhandOverride", "Overrides the Offhand item with a " +
            "GOLDEN_APPLE when trying to use an item", false);
    Config<Boolean> hotbarConfig = new BooleanConfig("HotbarItem",
            "Allow offhand items to be taken from the hotbar", false);
    Config<Boolean> noCollisionConfig = new BooleanConfig("CollisionTotem",
            "If the mainhand is already holding the item in the offhand, " +
                    "place a totem in the offhand instead", false);
    Config<Boolean> crappleConfig = new BooleanConfig("Crapple",
            "Attempts to take an advantage of a glitch in older versions to " +
                    "fully restore absorption hearts", false);
    Config<Boolean> crystalCheckConfig = new BooleanConfig("CrystalCheck",
            "Checks if a crystal is needed in the offhand", false);
    Config<Boolean> hotbarTotemConfig = new BooleanConfig("HotbarTotem",
            "Attempts to swap totems into the offhand from the hotbar", false);
    Config<Integer> totemSlotConfig = new NumberConfig<>("TotemSlot", "Slot " +
            "in the hotbar that is dedicated for hotbar totem swaps", 0,
            8, 8);
    //
    private Item offhand;
    //
    private boolean critical;
    private int calcTotem;
    private int totems;
    //
    private boolean sneaking, sprinting;
    //
    private static final int OFFHAND_SLOT = 45;

    /**
     *
     */
    public AutoTotemModule()
    {
        super("AutoTotem", "Automatically replaces totems in the offhand",
                ModuleCategory.COMBAT);
    }

    /**
     *
     *
     */
    @Override
    public void onDisable()
    {
        offhand = null;
        sneaking = false;
        sprinting = false;
        calcTotem = -1;
        totems = 0;
    }

    /**
     *
     * @return
     */
    @Override
    public String getMetaData()
    {
        return Integer.toString(totems);
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        // runs before mc gameloop ...
        if (event.getStage() == EventStage.PRE)
        {
            calcTotem = -1;
            ItemStack off = mc.player.getOffHandStack();
            if (off.getItem() == Items.TOTEM_OF_UNDYING)
            {
                totems++;
            }
            if (hotbarTotemConfig.getValue())
            {
                int idx = totemSlotConfig.getValue() + 36;
                ItemStack slot = mc.player.getInventory().getStack(idx);
                if (slot.getItem() == Items.TOTEM_OF_UNDYING)
                {
                    totems++;
                    calcTotem = totemSlotConfig.getValue();
                }
            }
            else
            {
                for (int i = 9; i < 45; i++)
                {
                    ItemStack slot = mc.player.getInventory().getStack(i);
                    if (slot.getItem() == Items.TOTEM_OF_UNDYING)
                    {
                        totems++;
                        calcTotem = i;
                        break;
                    }
                }
            }
            if (mc.currentScreen == null)
            {
                if (!critical)
                {
                    offhand = crystalCheckConfig.getValue()
                            && Modules.AUTO_CRYSTAL.isPlacing() ?
                            Items.END_CRYSTAL : offhandConfig.getValue();
                    //
                    ItemStack mainhand = mc.player.getMainHandStack();
                    if (mainhand.getItem() instanceof SwordItem
                            && mc.options.useKey.isPressed()
                            && offhandOverrideConfig.getValue())
                    {
                        offhand = Items.GOLDEN_APPLE;
                    }
                    if (!mc.player.isCreative())
                    {
                        float health = mc.player.getHealth()
                                + mc.player.getAbsorptionAmount();
                        if (health + 0.5 < healthConfig.getValue())
                        {
                            offhand = Items.TOTEM_OF_UNDYING;
                        }
                        if (lethalConfig.getValue())
                        {
                            if (health <= mc.player.fallDistance
                                    - mc.player.getSafeFallDistance() / 2.0f + 3.5f)
                            {
                                offhand = Items.TOTEM_OF_UNDYING;
                            }
                            for (Entity e : mc.world.getEntities())
                            {
                                if (e == null || !e.isAlive())
                                {
                                    continue;
                                }
                                if (e instanceof EndCrystalEntity crystal)
                                {
                                    if (mc.player.squaredDistanceTo(e) < 144.0)
                                    {
                                        double potential = EndCrystalUtil.getDamageTo(mc.player,
                                                crystal);
                                        if (health + 0.5 < potential)
                                        {
                                            offhand = Items.TOTEM_OF_UNDYING;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (noCollisionConfig.getValue() && mainhand.getItem() == offhand)
                    {
                        offhand = Items.TOTEM_OF_UNDYING;
                    }
                }
                else
                {
                    offhand = Items.TOTEM_OF_UNDYING;
                    critical = false;
                }
                // TOTEM SECTION
                if (offhand == Items.TOTEM_OF_UNDYING)
                {
                    if (calcTotem == -1)
                    {
                        Caspian.error("No TOTEM_OF_UNDYING left in inventory!");
                        
                    }
                    else if (off.isEmpty()
                            || off.getItem() != Items.TOTEM_OF_UNDYING)
                    {
                        if (hotbarTotemConfig.getValue())
                        {
                            int prev = mc.player.getInventory().selectedSlot;
                            mc.player.getInventory().selectedSlot = calcTotem;
                            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                                    PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                                    mc.player.getBlockPos(), Direction.UP));
                            mc.player.getInventory().selectedSlot = prev;
                        }
                        else
                        {
                            preClickSlot();
                            Managers.INVENTORY.pickupSlot(calcTotem);
                            boolean rt = !off.isEmpty();
                            Managers.INVENTORY.pickupSlot(OFFHAND_SLOT);
                            if (rt)
                            {
                                Managers.INVENTORY.pickupSlot(calcTotem);
                            }
                            postClickSlot();
                        }
                        calcTotem = -1;
                    }
                    if (!fallbackCrystalConfig.getValue())
                    {
                        return;
                    }
                    offhand = Items.END_CRYSTAL;
                }
                // OFFHAND SECTION
                int calcOffhand = -1;
                // Glint state boolean. For GOLDEN_APPLE, the loop will
                // exit when the preferred glint state is found
                boolean glint = false;
                for (int i = 9; i < (hotbarConfig.getValue() ? 45 : 36); i++)
                {
                    ItemStack slot = mc.player.getInventory().getStack(i);
                    if (slot.getItem() == Items.GOLDEN_APPLE
                            && offhand == Items.GOLDEN_APPLE)
                    {
                        if (glint)
                        {
                            break;
                        }
                        calcOffhand = i;
                        // in 1.12.2 we can restore all of our absorption
                        // hearts using crapples when the absorption
                        // effect is active
                        glint = mc.player.hasStatusEffect(StatusEffects.ABSORPTION)
                                && crappleConfig.getValue() != slot.hasGlint();
                    }
                    else if (slot.getItem() == offhand)
                    {
                        calcOffhand = i;
                        break;
                    }
                }
                if (calcOffhand == -1)
                {
                    Caspian.error("No %s left in inventory!",
                            offhand.getName());
                    return;
                }
                ItemStack cSlot =
                        mc.player.getInventory().getStack(calcOffhand);
                if (!isStackInOffhand(cSlot))
                {
                    preClickSlot();
                    Managers.INVENTORY.pickupSlot(calcOffhand);
                    boolean rt = !off.isEmpty();
                    Managers.INVENTORY.pickupSlot(OFFHAND_SLOT);
                    if (rt)
                    {
                        Managers.INVENTORY.pickupSlot(calcOffhand);
                    }
                    postClickSlot();
                }
            }
        }
    }
    
    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        ClientWorld world = mc.world;
        if (mc.player != null && world != null)
        {
            if (instantConfig.getValue())
            {
                if (event.getPacket() instanceof EntityStatusS2CPacket packet)
                {
                    // PLAYER POPPED TOTEM! attempt to instantly replace
                    if (packet.getEntity(world) == mc.player
                            && packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING)
                    {
                        critical = true;
                        ItemStack off = mc.player.getOffHandStack();
                        if (calcTotem == -1)
                        {
                            Caspian.error("No TOTEM_OF_UNDYING left in inventory!");
                            return;
                        }
                        preClickSlot();
                        Managers.INVENTORY.pickupSlot(calcTotem);
                        boolean rt = !off.isEmpty();
                        Managers.INVENTORY.pickupSlot(OFFHAND_SLOT);
                        if (rt)
                        {
                            Managers.INVENTORY.pickupSlot(calcTotem);
                        }
                        postClickSlot();
                    }
                }
            }
        }
    }
    
    /**
     *
     *
     */
    private void preClickSlot()
    {
        sneaking = Managers.POSITION.isSneaking();
        sprinting = Managers.POSITION.isSprinting();
        // INV_MOVE checks
        if (strictMoveConfig.getValue())
        {
            if (Managers.POSITION.isOnGround())
            {
                double x = Managers.POSITION.getX();
                double y = Managers.POSITION.getY();
                double z = Managers.POSITION.getZ();
                Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        x, y, z, true));
            }
        }
        if (strictActionsConfig.getValue())
        {
            if (mc.player.isUsingItem())
            {
                mc.player.stopUsingItem();
            }
            if (sneaking)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                                ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
            if (sprinting)
            {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
        }
    }
    
    /**
     *
     *
     */
    private void postClickSlot()
    {
        if (strictActionsConfig.getValue())
        {
            if (sneaking)
            {
                sneaking = false;
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            }
            if (sprinting)
            {
                sprinting = false;
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
        }
    }

    /**
     *
     *
     * @param s
     * @return
     */
    private boolean isStackInOffhand(ItemStack s)
    {
        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.isEmpty())
        {
            return false;
        }
        if (offhand.getItem() == Items.GOLDEN_APPLE
                && s.getItem() == Items.GOLDEN_APPLE)
        {
            return offhand.hasGlint() == s.hasGlint();
        }
        return offhand.getItem() == s.getItem();
    }
}
