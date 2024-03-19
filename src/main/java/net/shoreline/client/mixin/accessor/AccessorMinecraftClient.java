package net.shoreline.client.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author linus
 * @see MinecraftClient
 * @since 1.0
 */
@Mixin(MinecraftClient.class)
public interface AccessorMinecraftClient {
    /**
     * @param itemUseCooldown
     */
    @Accessor("itemUseCooldown")
    void hookSetItemUseCooldown(int itemUseCooldown);

    /**
     * @return
     */
    @Accessor("itemUseCooldown")
    int hookGetItemUseCooldown();

    /**
     * @param attackCooldown
     */
    @Accessor("attackCooldown")
    void hookSetAttackCooldown(int attackCooldown);
}
