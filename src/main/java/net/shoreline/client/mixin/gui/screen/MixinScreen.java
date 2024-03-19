package net.shoreline.client.mixin.gui.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.shoreline.client.Shoreline;
import net.shoreline.client.impl.event.gui.RenderTooltipEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(Screen.class)
public abstract class MixinScreen
{
    /**
     *
     * @param drawableElement
     * @return
     * @param <T>
     */
    @Shadow
    protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    /**
     *
     */
    @Shadow
    public void tick()
    {

    }

    //
    @Shadow
    public int width;
    //
    @Shadow
    public int height;
    //
    @Shadow
    @Final
    private List<Drawable> drawables;

    /**
     *
     * @return
     */
    @Unique
    public List<Drawable> getDrawables()
    {
        return drawables;
    }
}
