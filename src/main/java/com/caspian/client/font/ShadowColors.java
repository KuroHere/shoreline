package com.caspian.client.font;

import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class ShadowColors
{
    private static final Map<Character, Integer> SHADOW_CACHE;

    /**
     *
     * @param formatting
     * @return
     */
    public static int getShadowColor(Formatting formatting)
    {
        if (formatting.getColorIndex() != -1)
        {
            return SHADOW_CACHE.get(formatting.getCode());
        }
        return -1;
    }

    static
    {
        SHADOW_CACHE = new HashMap<>();
        SHADOW_CACHE.put('0', 0xff000000);
        SHADOW_CACHE.put('1', 0xff00002a);
        SHADOW_CACHE.put('2', 0xff002a00);
        SHADOW_CACHE.put('3', 0xff002a2a);
        SHADOW_CACHE.put('4', 0xff2a0000);
        SHADOW_CACHE.put('5', 0xff2a002a);
        SHADOW_CACHE.put('6', 0xff2a2a00);
        SHADOW_CACHE.put('7', 0xff2a2a2a);
        SHADOW_CACHE.put('8', 0xff151515);
        SHADOW_CACHE.put('9', 0xff15153f);
        SHADOW_CACHE.put('a', 0xff153f15);
        SHADOW_CACHE.put('b', 0xff153f3f);
        SHADOW_CACHE.put('c', 0xff3f1515);
        SHADOW_CACHE.put('d', 0xff3f153f);
        SHADOW_CACHE.put('e', 0xff3f3f15);
        SHADOW_CACHE.put('f', 0xff3f3f3f);
    }
}
