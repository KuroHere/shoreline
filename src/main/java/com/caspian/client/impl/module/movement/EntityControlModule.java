package com.caspian.client.impl.module.movement;

import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.entity.passive.EntitySteerEvent;
import com.caspian.client.impl.event.network.MountJumpStrengthEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class EntityControlModule extends ToggleModule
{
    /**
     *
     */
    public EntityControlModule()
    {
        super("EntityControl", "Allows you to steer entities without a saddle",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        Entity vehicle = mc.player.getVehicle();
        if (vehicle == null)
        {
            return;
        }
        vehicle.setYaw(mc.player.getYaw());
        if (vehicle instanceof LlamaEntity llama)
        {
            llama.headYaw = mc.player.getHeadYaw();
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onEntitySteer(EntitySteerEvent event)
    {
        event.cancel();
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onMountJumpStrength(MountJumpStrengthEvent event)
    {
        event.cancel();
    }
}
