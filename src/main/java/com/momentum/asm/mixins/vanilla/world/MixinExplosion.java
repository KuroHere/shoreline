package com.momentum.asm.mixins.vanilla.world;

import net.minecraft.world.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;

@Mixin(Explosion.class)
public class MixinExplosion {

    @Inject(method = "doExplosionB", at = )
}
