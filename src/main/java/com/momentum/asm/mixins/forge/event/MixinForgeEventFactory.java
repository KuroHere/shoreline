package com.momentum.asm.mixins.forge.event;

import com.momentum.Momentum;
import com.momentum.api.util.Wrapper;
import com.momentum.impl.events.forge.ItemUseEvent;
import com.momentum.impl.events.forge.ItemUseStage;
import com.momentum.impl.events.forge.RenderHudOverlayEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeEventFactory.class)
public class MixinForgeEventFactory implements Wrapper {

    // item use event
    private static final ItemUseEvent itemUseEvent = new ItemUseEvent();
    
    /**
     * Called when an item use is started
     */
    @Inject(method = "onItemUseStart", at = @At("HEAD"), remap = false)
    private static void onOnItemUseStart(EntityLivingBase entity, ItemStack item, int duration, CallbackInfoReturnable<Integer> cir) {
        
        // player item use
        if (entity == mc.player) {

            // post item use event
            itemUseEvent.setStage(ItemUseStage.START);
            Momentum.EVENT_BUS.dispatch(itemUseEvent);
        }
    }

    /**
     * Called when an item use is ticked (updated)
     */
    @Inject(method = "onItemUseTick", at = @At("HEAD"), remap = false)
    private static void onOnItemUseTick(EntityLivingBase entity, ItemStack item, int duration, CallbackInfoReturnable<Integer> cir) {

        // player item use
        if (entity == mc.player) {

            // post item use event
            ItemUseEvent itemUseEvent = new ItemUseEvent();
            itemUseEvent.setStage(ItemUseStage.TICK);
            Momentum.EVENT_BUS.dispatch(itemUseEvent);
        }
    }

    /**
     * Called when an item use is stopped
     */
    @Inject(method = "onUseItemStop", at = @At("HEAD"), remap = false)
    private static void onOnUseItemStop(EntityLivingBase entity, ItemStack item, int duration, CallbackInfoReturnable<Integer> cir) {

        // player item use
        if (entity == mc.player) {

            // post item use event
            ItemUseEvent itemUseEvent = new ItemUseEvent();
            itemUseEvent.setStage(ItemUseStage.STOP);
            Momentum.EVENT_BUS.dispatch(itemUseEvent);
        }
    }

    /**
     * Called when an item use is finished
     */
    @Inject(method = "onItemUseFinish", at = @At("HEAD"), remap = false)
    private static void onOnUseItemFinish(EntityLivingBase entity, ItemStack item, int duration, ItemStack result, CallbackInfoReturnable<ItemStack> cir) {

        // player item use
        if (entity == mc.player) {

            // post item use event
            ItemUseEvent itemUseEvent = new ItemUseEvent();
            itemUseEvent.setStage(ItemUseStage.FINISH);
            Momentum.EVENT_BUS.dispatch(itemUseEvent);
        }
    }

    @Inject(method = "renderBlockOverlay", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onRenderBlockOverlay(EntityPlayer player, float renderPartialTicks, OverlayType type, IBlockState block, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {

        // player check
        if (player == mc.player) {

            // post the render hud overlay event
            RenderHudOverlayEvent renderHudOverlayEvent = new RenderHudOverlayEvent(type);
            Momentum.EVENT_BUS.dispatch(renderHudOverlayEvent);

            // prevent overlay from rendering
            if (renderHudOverlayEvent.isCanceled()) {
                cir.cancel();
                cir.setReturnValue(false);
            }
        }
    }
}
