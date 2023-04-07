package com.momentum.asm.mixin.accessor;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor interface for {@link ChatScreen}
 *
 * @author linus
 * @since 1.0
 *
 * @see ChatScreen
 */
@Mixin(ChatScreen.class)
public interface AccessorChatScreen
{
    // {@see ChatScreen#chatField}
    @Accessor("chatField")
    TextFieldWidget getChatField();
}
