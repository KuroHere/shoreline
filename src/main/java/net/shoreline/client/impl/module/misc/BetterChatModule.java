package net.shoreline.client.impl.module.misc;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.EnumConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.util.render.animation.Easing;
import net.shoreline.client.util.render.animation.TimeAnimation;

import java.util.HashMap;
import java.util.Map;

//TODO: add easing when linus fixes enumconfig...
public class BetterChatModule extends ToggleModule
{
    Config<Boolean> animation = new BooleanConfig("Animation", "Animates the chat", false);
    Config<Integer> time = new NumberConfig<>("Time", "Time for the animation", 0, 200, 1000);
    /* Config<Easing> easing = new EnumConfig<>("Easing", "Easing for the animation", Easing.LINEAR, new Easing[]{
            Easing.LINEAR,
            Easing.SINE_IN,
            Easing.SINE_OUT,
            Easing.SINE_IN_OUT,
            Easing.CUBIC_IN,
            Easing.CUBIC_OUT,
            Easing.CUBIC_IN_OUT,
            Easing.QUAD_IN,
            Easing.QUAD_OUT,
            Easing.QUAD_IN_OUT,
            Easing.QUART_IN,
            Easing.QUART_OUT,
            Easing.QUART_IN_OUT,
            Easing.QUINT_IN,
            Easing.QUINT_OUT,
            Easing.QUINT_IN_OUT,
            Easing.CIRC_IN,
            Easing.CIRC_OUT,
            Easing.CIRC_IN_OUT,
            Easing.EXPO_IN,
            Easing.EXPO_OUT,
            Easing.EXPO_IN_OUT,
            Easing.ELASTIC_IN,
            Easing.ELASTIC_OUT,
            Easing.ELASTIC_IN_OUT,
            Easing.BACK_IN,
            Easing.BACK_OUT,
            Easing.BACK_IN_OUT,
            Easing.BOUNCE_IN,
            Easing.BOUNCE_OUT,
            Easing.BOUNCE_IN_OUT
    }); */

    public final Map<ChatHudLine, TimeAnimation> animationMap = new HashMap<>();

    public BetterChatModule()
    {
        super("BetterChat", "Modifications for the chat", ModuleCategory.MISCELLANEOUS);
    }

    public Config<Boolean> getAnimationConfig()
    {
        return animation;
    }

    public Config<Integer> getTimeConfig()
    {
        return time;
    }

    /*
    public Config<Easing> getEasingConfig()
    {
        return easing;
    }
    */

    public Easing getEasingConfig()
    {
        return Easing.LINEAR;
    }
}
