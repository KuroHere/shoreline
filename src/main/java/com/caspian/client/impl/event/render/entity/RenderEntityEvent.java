package com.caspian.client.impl.event.render.entity;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Cancelable
public class RenderEntityEvent extends Event
{
    public final LivingEntity entity;

    public final float f;
    public final float g;
    public final MatrixStack matrixStack;
    public final int i;
    public final EntityModel model;

    /**
     *
     * @param entity
     * @param f
     * @param g
     * @param matrixStack
     * @param i
     * @param model
     */
    public RenderEntityEvent(LivingEntity entity, float f, float g,
                             MatrixStack matrixStack, int i, EntityModel model)
    {
        this.entity = entity;
        this.f = f;
        this.g = g;
        this.matrixStack = matrixStack;
        this.i = i;
        this.model = model;
    }
}
