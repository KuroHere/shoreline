package com.caspian.client.impl.event.item;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

@Cancelable
public class DurabilityEvent extends Event
{
    //
    private int damage;

    public DurabilityEvent(int damage)
    {
        this.damage = damage;
    }

    public int getItemDamage()
    {
        return Math.max(0, damage);
    }

    public int getDamage()
    {
        return damage;
    }

    public void setDamage(int damage)
    {
        this.damage = damage;
    }
}
