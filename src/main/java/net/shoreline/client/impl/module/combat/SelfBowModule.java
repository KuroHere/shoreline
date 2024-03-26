package net.shoreline.client.impl.module.combat;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.RotationModule;
import net.shoreline.client.impl.event.network.PlayerTickEvent;
import net.shoreline.client.init.Managers;

import java.util.HashSet;
import java.util.Set;

/**
 * @author linus
 * @since 1.0
 */
public class SelfBowModule extends RotationModule {

    //
    private final Set<StatusEffect> arrows = new HashSet<>();
    private int shootTicks;

    /**
     *
     */
    public SelfBowModule() {
        super("SelfBow", "Shoots player with beneficial tipped arrows", ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        mc.options.useKey.setPressed(false);
        arrows.clear();
    }

    @EventListener
    public void onTick(PlayerTickEvent event) {
        int arrowSlot = -1;
        StatusEffect arrowEffect = null;
        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof TippedArrowItem)) {
                continue;
            }
            Potion p = PotionUtil.getPotion(stack);
            for (StatusEffectInstance effect : p.getEffects()) {
                StatusEffect type = effect.getEffectType();
                if (type.isBeneficial() && !arrows.contains(type) && mc.player.getStatusEffect(type) == null) {
                    arrowSlot = i;
                    arrowEffect = type;
                    break;
                }
            }
            if (arrowSlot != -1) {
                break;
            }
        }
        int bowSlot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.BOW) {
                bowSlot = i;
                break;
            }
        }
        if (mc.player.getMainHandStack().getItem() != Items.BOW || bowSlot == -1 || arrowSlot == -1) {
            disable();
            return;
        }
        setRotation(mc.player.getYaw(), -90.0f);
        prioritizeArrow(arrowSlot, arrowEffect);
        float pullTime = BowItem.getPullProgress(mc.player.getItemUseTime());
        if (pullTime >= 0.15f) {
            arrows.add(arrowEffect);
            shootTicks = 0;
            mc.options.useKey.setPressed(false);
            Managers.NETWORK.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
            mc.player.stopUsingItem();
            return;
        }
        mc.options.useKey.setPressed(true);
    }

    private void prioritizeArrow(int slot, StatusEffect statusEffect) {
        ItemStack stack = mc.player.getInventory().getStack(9);
        Potion p = PotionUtil.getPotion(stack);
        boolean b1 = p.getEffects().stream().anyMatch(p1 -> p1.getEffectType() == statusEffect);
        if (stack.getItem() instanceof TippedArrowItem && b1) {
            return;
        }
        boolean returnClick = mc.player.currentScreenHandler.getCursorStack().isEmpty();
        Managers.INVENTORY.pickupSlot(slot);
        Managers.INVENTORY.pickupSlot(9);
        if (returnClick && !mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
            Managers.INVENTORY.pickupSlot(slot);
        }
    }
}
