package com.caspian.client.mixin.text;

import com.caspian.client.Caspian;
import com.caspian.client.impl.event.text.TextVisitEvent;
import com.caspian.client.util.Globals;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Mixin(TextVisitFactory.class)
public class MixinTextVisitFactory implements Globals
{
    /**
     *
     * @param text
     * @return
     */
    @ModifyArg(method = "visitFormatted(Ljava/lang/String;Lnet/minecraft/text/" +
            "Style;Lnet/minecraft/text/CharacterVisitor;)Z", at = @At(value =
            "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;" +
            "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;" +
            "Lnet/minecraft/text/CharacterVisitor;)Z", ordinal = 0), index = 0)
    private static String hookVisitFormatted(String text)
    {
        if (mc.player == null)
        {
            return text;
        }
        final TextVisitEvent textVisitEvent = new TextVisitEvent(text);
        Caspian.EVENT_HANDLER.dispatch(textVisitEvent);
        if (textVisitEvent.isCanceled())
        {
            return textVisitEvent.getText();
        }
        return text;
    }
}
