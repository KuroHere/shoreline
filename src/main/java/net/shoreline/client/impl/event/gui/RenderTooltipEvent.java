package net.shoreline.client.impl.event.gui;

import net.shoreline.client.api.event.Cancelable;
import net.shoreline.client.api.event.Event;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.shoreline.client.api.event.StageEvent;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
@Cancelable
public class RenderTooltipEvent extends StageEvent
{
    public final MatrixStack matrices;
    private final ItemStack stack;
    //
    private final int x, y;

    public RenderTooltipEvent(MatrixStack matrices, ItemStack stack, int x, int y)
    {
        this.matrices = matrices;
        this.stack = stack;
        this.x = x;
        this.y = y;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}
