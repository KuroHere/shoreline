package net.shoreline.client.impl.module.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.RunTickEvent;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.init.Modules;
import net.shoreline.client.util.world.EndCrystalUtil;

import java.util.List;

/**
 * @author linus
 * @since 1.0
 */
public class AutoTotemModule extends ToggleModule {
    //
    Config<TickStage> tickStageConfig = new EnumConfig<>("Tick-Sync", "Tick stage to run the totem swap. (Note: LOOP will run much more frequently " +
            "than TICK, may cause issues)", TickStage.TICK, TickStage.values());
    Config<OffhandItem> offhandConfig = new EnumConfig<>("Offhand", "Item to keep in offhand when in a safe environment", OffhandItem.TOTEM, OffhandItem.values());
    Config<OffhandItem> offhand2Config = new EnumConfig<>("Offhand-Fallback", "Fallback item to keep in offhand", OffhandItem.TOTEM, OffhandItem.values());
    Config<Float> healthConfig = new NumberConfig<>("Health", "Lethal offhand health", 0.0f, 0.0f, 20.0f);
    Config<Boolean> lethalConfig = new BooleanConfig("Lethal", "Calculate lethal damage sources and place totem in offhand", false);
    Config<Boolean> instantConfig = new BooleanConfig("Instant", "Attempt to instantly replace totem in offhand after popping a totem", false);
    Config<Boolean> strictActionConfig = new BooleanConfig("Strict-Click", "Stops actions before clicking slots", false);
    Config<Boolean> strictMoveConfig = new BooleanConfig("Strict-Move", "Stops motion before clicking slots", false);
    Config<Boolean> offhandOverrideConfig = new BooleanConfig("Offhand-Override", "Overrides the Offhand item with a GOLDEN_APPLE when trying to use an item", false);
    Config<Boolean> offhandPotionConfig = new BooleanConfig("Offhand-Potions", "Uses potions in the inventory before gapples", false);
    Config<Boolean> hotbarConfig = new BooleanConfig("AllowHotbar", "Allow offhand items to be taken from the hotbar", false);
    Config<Boolean> noCollisionConfig = new BooleanConfig("CollisionTotem", "If the mainhand is already holding the item in the offhand, place a totem in the offhand instead", false);
    Config<Boolean> crappleConfig = new BooleanConfig("Crapple", "Attempts to take an advantage of a glitch in older versions to fully restore absorption hearts", false);
    Config<Boolean> crystalCheckConfig = new BooleanConfig("Crystal-Check", "Checks if a crystal is needed in the offhand", false);
    Config<Boolean> hotbarTotemConfig = new BooleanConfig("HotbarTotem", "Attempts to swap totems into the offhand from the hotbar", false);
    Config<Integer> totemSlotConfig = new NumberConfig<>("TotemSlot", "Slot in the hotbar that is dedicated for hotbar totem swaps", 0, 8, 8, () -> hotbarTotemConfig.getValue());
    Config<Boolean> debugConfig = new BooleanConfig("Debug-Offhand", "Displays the current item in the offhand", false);
    //
    private Item offhand;
    //
    private boolean sneaking, sprinting;
    //
    private boolean popTick;
    private int slotTotem, slotOffhand;
    private int totems;
    //
    private static final int OFFHAND_SLOT = 45;

    /**
     *
     */
    public AutoTotemModule() {
        super("AutoTotem", "Automatically replaces totems in the offhand",
                ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        offhand = null;
        slotTotem = -1;
        totems = 0;
        sneaking = false;
        sprinting = false;
        popTick = false;
    }

    @Override
    public String getModuleData() {
        if (offhand != null && debugConfig.getValue()) {
            return offhand.getName().getString();
        }
        return Integer.toString(totems);
    }

    @EventListener
    public void onRunTick(RunTickEvent event) {
        // runs before mc gameloop ...
        if (mc.player != null && mc.world != null
                && tickStageConfig.getValue() == TickStage.LOOP) {
            placeOffhand();
        }
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (tickStageConfig.getValue() == TickStage.TICK
                && event.getStage() == EventStage.PRE) {
            placeOffhand();
        }
    }

    /**
     *
     */
    private void placeOffhand() {
        int totemCount = 0;
        //
        int slotPotion = -1;
        slotTotem = -1;
        slotOffhand = -1;
        final ItemStack off = mc.player.getOffHandStack();
        if (hotbarTotemConfig.getValue()) {
            int totemIdx = totemSlotConfig.getValue() + 36;
            ItemStack slot = mc.player.getInventory().getStack(totemIdx);
            if (slot.getItem() == Items.TOTEM_OF_UNDYING) {
                totemCount++;
                slotTotem = totemSlotConfig.getValue();
            }
        } else {
            for (int i = 9; i < 45; i++) {
                ItemStack slot = mc.player.getInventory().getStack(i);
                if (slot.getItem() == Items.TOTEM_OF_UNDYING) {
                    totemCount++;
                    if (slotTotem != -1) {
                        continue;
                    }
                    slotTotem = i;
                } else if (slot.getItem() == Items.POTION) {
                    final List<StatusEffectInstance> list =
                            PotionUtil.getPotionEffects(slot);
                    boolean harm = list.stream().anyMatch(p -> !p.getEffectType().isBeneficial());
                    if (slotPotion == -1 && !harm
                            && !mc.player.getStatusEffects().containsAll(list)) {
                        slotPotion = i;
                    }
                }
            }
        }
        totems = totemCount;
        if (mc.currentScreen != null) {
            return;
        }
        OffhandItem offhandItem = offhandConfig.getValue();
        offhand = crystalCheckConfig.getValue() && Modules.AUTO_CRYSTAL.isPlacing() ? Items.END_CRYSTAL : offhandItem.getItem();
        //
        final ItemStack mainhand = mc.player.getMainHandStack();
        boolean offhandOverride = mainhand.getItem() instanceof SwordItem && mc.options.useKey.isPressed();
        if (offhandOverride && offhandOverrideConfig.getValue()) {
            offhand = slotPotion != -1 && offhandPotionConfig.getValue() ? Items.POTION : Items.GOLDEN_APPLE;
        }
        if (!mc.player.isCreative()) {
            float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            if (health + 0.5 < healthConfig.getValue()) {
                offhand = Items.TOTEM_OF_UNDYING;
            }
            int fall = computeFallDamage(mc.player.fallDistance);
            if (health + 0.5 <= fall) {
                offhand = Items.TOTEM_OF_UNDYING;
            }
            if (lethalConfig.getValue()) {
                for (Entity e : mc.world.getEntities()) {
                    if (e == null || !e.isAlive() || !(e instanceof EndCrystalEntity crystal)) {
                        continue;
                    }
                    if (Managers.POSITION.squaredDistanceTo(e) > 144.0) {
                        continue;
                    }
                    double potential = EndCrystalUtil.getDamageTo(mc.player, crystal.getPos());
                    if (health + 0.5 > potential) {
                        continue;
                    }
                    offhand = Items.TOTEM_OF_UNDYING;
                    break;
                }
            }
        }
        if (noCollisionConfig.getValue() && mainhand.getItem() == offhand) {
            offhand = Items.TOTEM_OF_UNDYING;
        }
        // Glint state boolean. For GOLDEN_APPLE, the loop will
        // exit when the preferred glint state is found
        if (offhand == Items.GOLDEN_APPLE) {
            // in 1.12.2 we can restore all of our absorption
            // hearts using crapples when the absorption
            // effect is active
            if (crappleConfig.getValue() && mc.player.hasStatusEffect(StatusEffects.ABSORPTION)) {
                offhand = Items.GOLDEN_APPLE;
            } else {
                offhand = Items.ENCHANTED_GOLDEN_APPLE;
            }
        }
        // TOTEM SECTION
        if (offhand == Items.TOTEM_OF_UNDYING) {
            if (slotTotem == -1) {
                // ChatUtil.error("No TOTEM_OF_UNDYING left in inventory!");
                offhand = offhand2Config.getValue().getItem();
            } else if (off.isEmpty() || off.getItem() != Items.TOTEM_OF_UNDYING) {
                if (hotbarTotemConfig.getValue()) {
                    int prev = mc.player.getInventory().selectedSlot;
                    mc.player.getInventory().selectedSlot = slotTotem;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(slotTotem));
                    Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(
                            PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                            BlockPos.ORIGIN, Direction.DOWN));
                    mc.player.getInventory().selectedSlot = prev;
                    Managers.NETWORK.sendPacket(new UpdateSelectedSlotC2SPacket(prev));
                } else {
                    preClickSlot();
                    boolean returnClick = !off.isEmpty();
                    if (returnClick) {
                        Managers.INVENTORY.pickupSlot(OFFHAND_SLOT);
                    }
                    Managers.INVENTORY.pickupSlot(slotTotem);
                    Managers.INVENTORY.pickupSlot(OFFHAND_SLOT);
                    // Managers.INVENTORY.closeScreen();
                    postClickSlot();
                }
                slotTotem = -1;
            }
            return;
        }
        // OFFHAND SECTION
        slotOffhand = offhand == Items.POTION ? slotPotion : searchItemSlot(offhand);
        if (slotOffhand == -1) {
            Item offhandItem2 = offhand2Config.getValue().getItem();
            if (!off.isEmpty() && off.getItem().equals(offhand) || offhand == offhandItem2) {
                return;
            }
            // ChatUtil.error("No %s left in inventory!", offhand.getName());
            slotOffhand = offhandItem2 == Items.TOTEM_OF_UNDYING && slotTotem != -1 ? slotTotem : searchItemSlot(offhandItem2);
        }
        if (off.isEmpty() || !off.getItem().equals(offhand)) {
            preClickSlot();
            Managers.INVENTORY.pickupSlot(slotOffhand);
            Managers.INVENTORY.pickupSlot(OFFHAND_SLOT);
            Managers.INVENTORY.pickupSlot(slotOffhand);
            // Managers.INVENTORY.closeScreen();
            postClickSlot();
            slotOffhand = -1;
        }
    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        // PLAYER POPPED TOTEM! attempt to instantly replace
        if (event.getPacket() instanceof EntityStatusS2CPacket packet
                && packet.getEntity(mc.world) == mc.player
                && packet.getStatus() == EntityStatuses.USE_TOTEM_OF_UNDYING
                && instantConfig.getValue()) {
            popTick = true;
            ItemStack off = mc.player.getOffHandStack();
            if (slotTotem == -1) {
                // ChatUtil.error("No TOTEM_OF_UNDYING left in " +
                //        "inventory!");
                return;
            }
            preClickSlot();
            boolean returnClick = !off.isEmpty();
            Managers.INVENTORY.pickupSlot(slotTotem);
            Managers.INVENTORY.pickupSlot(OFFHAND_SLOT);
            if (returnClick) {
                Managers.INVENTORY.pickupSlot(slotTotem);
            }
            // Managers.INVENTORY.closeScreen();
            postClickSlot();
        }
    }

    // Add to inventory manager?
    private int searchItemSlot(Item item) {
        int itemSlot = -1;
        for (int i = 9; i < (hotbarConfig.getValue() ? 45 : 36); i++) {
            final ItemStack slot = mc.player.getInventory().getStack(i);
            if (slot.getItem() == item) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }

    private void preClickSlot() {
        sneaking = Managers.POSITION.isSneaking();
        sprinting = Managers.POSITION.isSprinting();
        // INV_MOVE checks
        if (strictMoveConfig.getValue() && Managers.POSITION.isOnGround()) {
            double x = Managers.POSITION.getX();
            double y = Managers.POSITION.getY();
            double z = Managers.POSITION.getZ();
            Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true));
        }
        if (strictActionConfig.getValue()) {
            if (mc.player.isUsingItem()) {
                mc.player.stopUsingItem();
            }
            if (sneaking) {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
            }
            if (sprinting) {
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
        }
    }

    private void postClickSlot() {
        if (strictActionConfig.getValue()) {
            if (sneaking) {
                sneaking = false;
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            }
            if (sprinting) {
                sprinting = false;
                Managers.NETWORK.sendPacket(new ClientCommandC2SPacket(mc.player,
                        ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
        }
    }

    private int computeFallDamage(float fallDistance) {
        StatusEffectInstance statusEffectInstance = mc.player.getStatusEffect(StatusEffects.JUMP_BOOST);
        float f = statusEffectInstance == null ? 0.0f :
                (float) (statusEffectInstance.getAmplifier() + 1);
        return MathHelper.ceil(fallDistance - 3.0f - f);
    }

    private boolean isStackInOffhand(final ItemStack stack) {
        final ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.isEmpty()) {
            return false;
        }
        if (offhand.getItem() == Items.GOLDEN_APPLE
                && stack.getItem() == Items.GOLDEN_APPLE) {
            return offhand.hasGlint() == stack.hasGlint();
        }
        return offhand.getItem() == stack.getItem();
    }

    public enum TickStage {
        TICK,
        // EXPERIMENTAL
        LOOP
    }

    public enum OffhandItem {
        TOTEM(Items.TOTEM_OF_UNDYING),
        CRYSTAL(Items.END_CRYSTAL),
        GAPPLE(Items.GOLDEN_APPLE);
        // POTION(Items.POTION);

        //
        private final Item item;

        OffhandItem(Item item) {
            this.item = item;
        }

        public Item getItem() {
            return item;
        }
    }
}
