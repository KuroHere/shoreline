package com.caspian.client.mixin.accessor;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(WorldRenderer.class)
public interface AccessorWorldRenderer
{
    /**
     *
     * @return
     */
    @Accessor("frustum")
    Frustum getFrustum();
}
