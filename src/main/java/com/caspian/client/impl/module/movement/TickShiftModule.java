package com.caspian.client.impl.module.movement;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.impl.event.network.SetCurrentHandEvent;
import com.caspian.client.init.Managers;
import com.caspian.client.util.player.MovementUtil;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class TickShiftModule extends ToggleModule
{
    //
    Config<Integer> ticksConfig = new NumberConfig<>("MaxTicks", "Maximum " +
            "charge ticks", 1, 20, 120);
    Config<Float> packetsConfig = new NumberConfig<>("Packets", "Packets" +
            " to release from storage every tick", 1.0f, 1.0f, 5.0f);
    Config<Boolean> consumablesConfig = new BooleanConfig("Consumables",
            "Allows player to consume items faster", false);
    //
    private int packets;

    /**
     *
     */
    public TickShiftModule()
    {
        super("TickShift", "Exploits NCP to speed up ticks",
                ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @return
     */
    @Override
    public String getModuleData()
    {
        return String.valueOf(packets);
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (MovementUtil.isMoving() || !mc.player.isOnGround())
        {
            packets--;
            if (packets <= 0)
            {
                packets = 0;
                Managers.TICK.setClientTick(1.0f);
            }
            else if (packets >= ticksConfig.getValue() - 1)
            {
                Managers.TICK.setClientTick(packetsConfig.getValue() + 1.0f);
            }
        }
        else
        {
            packets++;
            if (packets > ticksConfig.getValue())
            {
                packets = ticksConfig.getValue();
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onSetCurrentHand(SetCurrentHandEvent event)
    {
        if (consumablesConfig.getValue())
        {
            ItemStack stack = event.getStackInHand();
            if (!stack.getItem().isFood() && !(stack.getItem() instanceof PotionItem))
            {
                return;
            }
            int use = stack.getMaxUseTime();
            if (packets >= use)
            {
                for (int i = 0; i < use; i++)
                {
                    Managers.NETWORK.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(),
                            mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
                    event.cancel();
                    stack.getItem().finishUsing(stack, mc.world, mc.player);
                    packets -= use;
                }
            }
        }
    }
}
