package net.shoreline.client.mixin.accessor;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireworkRocketEntity.class)
public interface AccessorFireworkRocketEntity
{
    @Invoker("explodeAndRemove")
    void hookExplodeAndRemove();
}
