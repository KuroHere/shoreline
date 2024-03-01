package net.shoreline.client.mixin.accessor;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 */
@Mixin(ClientWorld.class)
public interface AccessorClientWorld
{
    /**
     *
     * @return
     */
    @Invoker("getPendingUpdateManager")
    PendingUpdateManager hookGetPendingUpdateManager();
}
