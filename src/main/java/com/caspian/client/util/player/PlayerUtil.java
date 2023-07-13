package com.caspian.client.util.player;

import com.caspian.client.util.Globals;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class PlayerUtil implements Globals
{
    /**
     *
     * @return
     */
    public static int getHandSwingDuration()
    {
        if (StatusEffectUtil.hasHaste(mc.player))
        {
            return 6 - (1 + StatusEffectUtil.getHasteAmplifier(mc.player));
        }
        return mc.player.hasStatusEffect(StatusEffects.MINING_FATIGUE) ?
                6 + (1 + mc.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
    }
}
