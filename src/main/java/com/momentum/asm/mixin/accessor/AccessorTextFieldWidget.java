package com.momentum.asm.mixin.accessor;

import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor interface for {@link TextFieldWidget}
 *
 * @author linus
 * @since 1.0
 *
 * @see TextFieldWidget
 */
@Mixin(TextFieldWidget.class)
public interface AccessorTextFieldWidget
{
    // {@link TextFieldWidget#drawsBackground}
    @Accessor("drawsBackground")
    boolean isDrawsBackground();
}
