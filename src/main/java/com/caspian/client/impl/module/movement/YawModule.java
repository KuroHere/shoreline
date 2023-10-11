package com.caspian.client.impl.module.movement;

import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class YawModule extends ToggleModule
{
    /**
     *
     */
    public YawModule()
    {
        super("Yaw", "Locks player yaw to a cardinal axis",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() == EventStage.PRE)
        {
            float yaw = Math.round(mc.player.getYaw() / 90.0f) * 90.0f;
            Entity vehicle = mc.player.getVehicle();
            if (vehicle != null)
            {
                vehicle.setYaw(yaw);
                if (vehicle instanceof LlamaEntity llama)
                {
                    llama.setHeadYaw(yaw);
                }
                return;
            }
            mc.player.setYaw(yaw);
            mc.player.setHeadYaw(yaw);
        }
    }
}
