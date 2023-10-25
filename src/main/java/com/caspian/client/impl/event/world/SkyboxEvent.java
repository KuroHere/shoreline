package com.caspian.client.impl.event.world;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class SkyboxEvent extends Event
{
    private Vec3d color;

    public Vec3d getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        setColor(new Vec3d(color.getRed(), color.getGreen(), color.getBlue()));
    }

    public void setColor(Vec3d color)
    {
        this.color = color;
    }

    @Cancelable
    public static class Sky extends SkyboxEvent
    {

    }

    @Cancelable
    public static class Cloud extends SkyboxEvent
    {

    }
}
