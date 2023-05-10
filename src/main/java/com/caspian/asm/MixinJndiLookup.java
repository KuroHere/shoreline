package com.caspian.asm;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.lookup.JndiLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see JndiLookup
 */
@Pseudo
@Mixin(JndiLookup.class)
public class MixinJndiLookup
{
    /**
     *
     *
     * @param event
     * @param key
     * @param cir
     */
    @Inject(method = "lookup(Lorg/apache/logging/log4j/core/LogEvent;" +
            "Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "HEAD"),
            cancellable = true, remap = false)
    private void hookLookup(LogEvent event, String key,
                            CallbackInfoReturnable<String> cir)
    {
        // FIX Log4j exploit
        cir.setReturnValue(key);
        cir.cancel();
    }
}
