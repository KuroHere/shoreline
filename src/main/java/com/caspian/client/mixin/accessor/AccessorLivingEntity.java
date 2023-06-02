package com.caspian.client.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see LivingEntity
 */
@Mixin(LivingEntity.class)
public interface AccessorLivingEntity
{
    /**
     *
     *
     * @return
     */
    @Invoker("getHandSwingDuration")
    int hookGetHandSwingDuration();
}
