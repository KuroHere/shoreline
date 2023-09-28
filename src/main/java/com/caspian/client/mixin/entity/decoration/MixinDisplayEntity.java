package com.caspian.client.mixin.entity.decoration;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.entity.decoration.TeamColorEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see DisplayEntity
 */
@Mixin(DisplayEntity.class)
public abstract class MixinDisplayEntity
{
    /**
     *
     * @param cir
     */
    @Inject(method = "getTeamColorValue", at = @At(value = "HEAD"),
            cancellable = true)
    private void hookGetTeamColorValue(CallbackInfoReturnable<Integer> cir)
    {
        TeamColorEvent teamColorEvent =
                new TeamColorEvent((Entity) (Object) this);
        Caspian.EVENT_HANDLER.dispatch(teamColorEvent);
        if (teamColorEvent.isCanceled())
        {
            cir.setReturnValue(teamColorEvent.getColor());
            cir.cancel();
        }
    }
}
