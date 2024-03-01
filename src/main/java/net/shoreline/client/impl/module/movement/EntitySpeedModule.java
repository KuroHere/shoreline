package net.shoreline.client.impl.module.movement;

import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.NumberConfig;
import net.shoreline.client.api.event.EventStage;
import net.shoreline.client.api.event.listener.EventListener;
import net.shoreline.client.api.module.ModuleCategory;
import net.shoreline.client.api.module.ToggleModule;
import net.shoreline.client.impl.event.TickEvent;
import net.shoreline.client.impl.event.network.PacketEvent;
import net.shoreline.client.init.Managers;
import net.shoreline.client.util.math.timer.CacheTimer;
import net.shoreline.client.util.math.timer.Timer;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.shoreline.client.util.Globals;

import java.text.DecimalFormat;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class EntitySpeedModule extends ToggleModule
{
    //
    Config<Float> speedConfig = new NumberConfig<>("Speed", "The speed of the" +
            " entity while moving", 0.1f, 0.5f, 4.0f);
    Config<Boolean> antiStuckConfig = new BooleanConfig("AntiStuck",
            "Prevents entities from getting stuck when moving up", false);
    Config<Boolean> strictConfig = new BooleanConfig("Strict", "The NCP" +
            "-Updated bypass for speeding up entity movement", false);
    //
    private final Timer entityJumpTimer = new CacheTimer();

    /**
     *
     */
    public EntitySpeedModule()
    {
        super("EntitySpeed", "Increases riding entity speeds", ModuleCategory.MOVEMENT);
    }

    /**
     *
     * @return
     */
    @Override
    public String getModuleData()
    {
        DecimalFormat decimal = new DecimalFormat("0.0");
        return decimal.format(speedConfig.getValue());
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        if (event.getStage() != EventStage.PRE)
        {
            return;
        }
        if (Globals.mc.player.isRiding() && Globals.mc.player.getVehicle() != null)
        {
            double d = Math.cos(Math.toRadians(Globals.mc.player.getYaw() + 90.0f));
            double d2 = Math.sin(Math.toRadians(Globals.mc.player.getYaw() + 90.0f));
            BlockPos pos1 = BlockPos.ofFloored(Globals.mc.player.getX() + (2.0 * d),
                    Globals.mc.player.getY() - 1.0, Globals.mc.player.getZ() + (2.0 * d2));
            BlockPos pos2 = BlockPos.ofFloored(Globals.mc.player.getX() + (2.0 * d),
                    Globals.mc.player.getY() - 2.0, Globals.mc.player.getZ() + (2.0 * d2));
            if (antiStuckConfig.getValue() && !Globals.mc.player.getVehicle().isOnGround()
                    && !Globals.mc.world.getBlockState(pos1).getMaterial().blocksMovement()
                    && !Globals.mc.world.getBlockState(pos2).getMaterial().blocksMovement())
            {
                entityJumpTimer.reset();
                return;
            }
            BlockPos pos3 = BlockPos.ofFloored(Globals.mc.player.getX() + (2.0 * d),
                    Globals.mc.player.getY(), Globals.mc.player.getZ() + (2.0 * d2));
            if (antiStuckConfig.getValue() && Globals.mc.world.getBlockState(pos3).getMaterial().blocksMovement())
            {
                entityJumpTimer.reset();
                return;
            }
            BlockPos pos4 = BlockPos.ofFloored(Globals.mc.player.getX() + d,
                    Globals.mc.player.getY() + 1.0, Globals.mc.player.getZ() + d2);
            if (antiStuckConfig.getValue() && Globals.mc.world.getBlockState(pos4).getMaterial().blocksMovement())
            {
                entityJumpTimer.reset();
                return;
            }
            if (Globals.mc.player.input.jumping)
            {
                entityJumpTimer.reset();
            }
            if (entityJumpTimer.passed(10000) || !antiStuckConfig.getValue())
            {
                if (!Globals.mc.player.getVehicle().isTouchingWater() || Globals.mc.player.input.jumping
                        || !entityJumpTimer.passed(1000))
                {
                    if (Globals.mc.player.getVehicle().isOnGround())
                    {
                        Globals.mc.player.getVehicle().setVelocity(Globals.mc.player.getVelocity().x,
                                0.4, Globals.mc.player.getVelocity().z);
                    }
                    Globals.mc.player.getVehicle().setVelocity(Globals.mc.player.getVelocity().x,
                            -0.4, Globals.mc.player.getVelocity().z);
                }
                if (strictConfig.getValue())
                {
                    Managers.NETWORK.sendPacket(PlayerInteractEntityC2SPacket.interact(
                            Globals.mc.player.getVehicle(), false, Hand.MAIN_HAND));
                }
                handleEntityMotion(speedConfig.getValue(), d, d2);
                entityJumpTimer.reset();
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventListener
    public void onPacketInbound(PacketEvent.Inbound event)
    {
        if (Globals.mc.player == null || !Globals.mc.player.isRiding() || Globals.mc.options.sneakKey.isPressed()
                || Globals.mc.player.getVehicle() == null)
        {
            return;
        }
        if (strictConfig.getValue())
        {
            if (event.getPacket() instanceof EntityPassengersSetS2CPacket)
            {
                event.cancel();
            }
            else if (event.getPacket() instanceof PlayerPositionLookS2CPacket)
            {
                event.cancel();
            }
        }
    }

    /**
     *
     * @param entitySpeed
     * @param d
     * @param d2
     */
    private void handleEntityMotion(float entitySpeed, double d, double d2)
    {
        Vec3d motion = Globals.mc.player.getVehicle().getVelocity();
        //
        float forward = Globals.mc.player.input.movementForward;
        float strafe = Globals.mc.player.input.movementSideways;
        if (forward == 0 && strafe == 0)
        {
            Globals.mc.player.getVehicle().setVelocity(0.0, motion.y, 0.0);
            return;
        }
        Globals.mc.player.getVehicle().setVelocity((forward * entitySpeed * d) + (strafe * entitySpeed * d2),
                motion.y, (forward * entitySpeed * d2) - (strafe * entitySpeed * d));
    }
}
