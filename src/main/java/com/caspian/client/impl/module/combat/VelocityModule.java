package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.EnumConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.config.setting.NumberDisplay;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.network.PacketEvent;
import com.caspian.client.mixin.accessor.AccessorEntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Gavin
 * @since 1.0
 */
public class VelocityModule extends ToggleModule {

    Config<VelocityMode> modeConfig = new EnumConfig<>(
            "Mode",
            "The anti-cheat bypass for velocity",
            VelocityMode.REDUCE,
            VelocityMode.values());

    Config<Float> horizontalConfig = new NumberConfig<>(
            "Horizontal",
            "How much horizontal knock-back to take",
            0.0f, 0.0f, 100.0f, NumberDisplay.PERCENT);
    Config<Float> verticalConfig = new NumberConfig<>(
            "Vertical",
            "How much vertical knock-back to take",
            0.0f, 0.0f, 100.0f, NumberDisplay.PERCENT);

    private final Set<Integer> grimVelocityTransactions = new HashSet<>();
    private boolean funnyGrim;

    public VelocityModule() {
        super("Velocity",
                "Reduces the amount of velocity you take",
                ModuleCategory.COMBAT);
    }

    @EventListener
    public void onPacketOutbound(PacketEvent.Outbound event) {

    }

    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event) {
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {

            switch (modeConfig.getValue()) {
                case REDUCE -> {
                    if (horizontalConfig.getValue() == 0.0f && verticalConfig.getValue() == 0.0f) {
                        event.setCanceled(true);
                        return;
                    }

                    ((AccessorEntityVelocityUpdateS2CPacket) packet).setVelocityX(
                            (int) (packet.getVelocityX() * (horizontalConfig.getValue() / 100.0f)));
                    ((AccessorEntityVelocityUpdateS2CPacket) packet).setVelocityY(
                            (int) (packet.getVelocityY() * (verticalConfig.getValue() / 100.0f)));
                    ((AccessorEntityVelocityUpdateS2CPacket) packet).setVelocityZ(
                            (int) (packet.getVelocityZ() * (horizontalConfig.getValue() / 100.0f)));
                }

                case GRIM -> {

                }
            }

        } else if (event.getPacket() instanceof ScreenHandlerSlotUpdateS2CPacket packet) {

            // TODO: check if this is the equivalent to 1.8 C0F
            if (modeConfig.getValue() == VelocityMode.GRIM && funnyGrim) {
                grimVelocityTransactions.add(packet.getRevision());
            }
        }
    }

    private enum VelocityMode {
        REDUCE, GRIM
    }
}
