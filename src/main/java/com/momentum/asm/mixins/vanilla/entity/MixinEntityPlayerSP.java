package com.momentum.asm.mixins.vanilla.entity;

import com.mojang.authlib.GameProfile;
import com.momentum.Momentum;
import com.momentum.impl.events.vanilla.entity.MoveEvent;
import com.momentum.impl.events.vanilla.entity.SprintingEvent;
import com.momentum.impl.events.vanilla.entity.PushOutOfBlocksEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    // move method
    @Shadow
    public abstract void move(MoverType type, double x, double y, double z);

    // updateAutoJump method
    @Shadow
    protected abstract void updateAutoJump(float p_189810_1_, float p_189810_2_);

    /**
     * Dummy constructor
     */
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    /**
     * Called when an entity is pushed out of a block
     **/
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void onPushOutOfBlocks(double p_pushOutOfBlocks_1_, double p_pushOutOfBlocks_2_, double p_pushOutOfBlocks_3_, CallbackInfoReturnable<Boolean> cir) {

        // post the push out of blocks event
        PushOutOfBlocksEvent pushOutOfBlocksEvent = new PushOutOfBlocksEvent();
        Momentum.EVENT_BUS.dispatch(pushOutOfBlocksEvent);

        // cancel packet send if the event is canceled
        if (pushOutOfBlocksEvent.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(false);
        }
    }

    /**
     * Called when the sprint state updates on the entity living update
     */
    @Inject(method = "setSprinting", at = @At(value = "HEAD"), cancellable = true)
    public void onSetSprinting(boolean val, CallbackInfo ci) {

        // post living update event
        SprintingEvent sprintingEvent = new SprintingEvent();
        Momentum.EVENT_BUS.dispatch(sprintingEvent);

        // prevent val being false
        if (sprintingEvent.isCanceled() && !val) {
            ci.cancel();
        }
    }

    /**
     * Called when player moves
     */
    @Inject(method = "move", at = @At(value = "HEAD"), cancellable = true)
    public void onMove(MoverType type, double x, double y, double z, CallbackInfo ci) {

        // post move event
        MoveEvent moveEvent = new MoveEvent(x, y, z);
        Momentum.EVENT_BUS.dispatch(moveEvent);

        // use custom movement
        if (moveEvent.isCanceled()) {

            // cancel move
            ci.cancel();

            // previous xz pos
            double px = posX;
            double pz = posZ;

            // move
            super.move(type, moveEvent.getX(), moveEvent.getY(), moveEvent.getZ());

            // update auto jump
            updateAutoJump((float) (posX - px), (float) (posZ - pz));
        }
    }
}
