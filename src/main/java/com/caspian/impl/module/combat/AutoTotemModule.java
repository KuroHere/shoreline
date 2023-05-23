package com.caspian.impl.module.combat;

import com.caspian.Caspian;
import com.caspian.api.config.Config;
import com.caspian.api.config.setting.BooleanConfig;
import com.caspian.api.config.setting.ItemConfig;
import com.caspian.api.config.setting.NumberConfig;
import com.caspian.api.event.EventStage;
import com.caspian.api.event.listener.EventListener;
import com.caspian.api.module.ModuleCategory;
import com.caspian.api.module.ToggleModule;
import com.caspian.impl.event.TickEvent;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class AutoTotemModule extends ToggleModule
{
    //
    Config<Boolean> deathVerboseConfig = new BooleanConfig("DeathVerbose",
            "", false);
    Config<Item> offhandConfig = new ItemConfig("Offhand", "Item to keep" +
            " in offhand when in a safe environment", Items.TOTEM_OF_UNDYING,
            new Item[]
                    {
                            Items.TOTEM_OF_UNDYING,
                            Items.END_CRYSTAL,
                            Items.GOLDEN_APPLE
                    });
    Config<Float> healthConfig = new NumberConfig<>("Health", "", 0.0f, 0.0f,
            20.0f);
    Config<Boolean> offhandOverrideConfig = new BooleanConfig(
            "OffhandOverride", "", false);
    Config<Boolean> crappleConfig = new BooleanConfig("Crapple",
            "", false);
    Config<Boolean> hotbarConfig = new BooleanConfig("Hotbar",
            "", false);

    //
    private Item offhand;
    //
    private int calcTotem;
    private int totems;

    /**
     *
     */
    public AutoTotemModule()
    {
        super("AutoTotem", "Automatically replaces totems in the offhand",
                ModuleCategory.COMBAT);
    }

    /**
     *
     *
     */
    @Override
    public void onEnable()
    {
        offhand = null;
        // calcTotem = -1;
    }

    /**
     *
     *
     * @param event
     */
    @EventListener
    public void onTick(TickEvent event)
    {
        // runs before mc gameloop ...
        if (event.getStage() == EventStage.PRE)
        {
            calcTotem = -1;
            ItemStack off = mc.player.getOffHandStack();
            if (off.getItem() == Items.TOTEM_OF_UNDYING)
            {
                totems++;
            }
            for (int i = 9; i < 45; i++)
            {
                ItemStack slot = mc.player.getInventory().getStack(i);
                if (slot.getItem() == Items.TOTEM_OF_UNDYING)
                {
                    totems++;
                    if (i < 36 || hotbarConfig.getValue())
                    {
                        calcTotem = i;
                        break;
                    }
                }
            }
            if (mc.currentScreen == null)
            {
                offhand = offhandConfig.getValue();
                //
                ItemStack mainhand = mc.player.getMainHandStack();
                if (mainhand.getItem() instanceof SwordItem
                        && mc.options.useKey.isPressed()
                        && offhandOverrideConfig.getValue())
                {
                    offhand = Items.GOLDEN_APPLE;
                }
                if (!mc.player.isCreative())
                {
                    float health = mc.player.getHealth()
                            + mc.player.getAbsorptionAmount();
                    if (health < healthConfig.getValue())
                    {
                        offhand = Items.TOTEM_OF_UNDYING;
                    }
                }
                if (offhand == Items.TOTEM_OF_UNDYING)
                {
                    if (calcTotem == -1)
                    {
                        Caspian.error("No TOTEM_OF_UNDYING left in inventory!");
                        // guaranteed death here because no totems when
                        // necessary
                        if (deathVerboseConfig.getValue())
                        {

                        }
                        return;
                    }

                }
                else
                {
                    int calcOffhand = -1;
                    //
                    boolean gapple = false;
                    for (int i = 9; i < (hotbarConfig.getValue() ? 45 : 36); i++)
                    {
                        ItemStack slot = mc.player.getInventory().getStack(i);
                        if (slot.getItem() == Items.GOLDEN_APPLE
                                && offhand == Items.GOLDEN_APPLE)
                        {
                            // in 1.12.2 we can restore all of our absorption hearts
                            if (mc.player.hasStatusEffect(StatusEffects.ABSORPTION)
                                    && crappleConfig.getValue())
                            {
                                if (!gapple)
                                {
                                    calcOffhand = i;
                                    gapple = !slot.hasGlint();
                                }
                            }
                            else if (!gapple)
                            {
                                calcOffhand = i;
                                gapple = slot.hasGlint();
                            }
                        }
                        else if (slot.getItem() == offhand)
                        {
                            calcOffhand = i;
                            break;
                        }
                    }
                    if (calcOffhand == -1)
                    {
                        Caspian.error("No %s left in inventory!",
                                offhand.getName());
                        return;
                    }
                    ItemStack calcSlot =
                            mc.player.getInventory().getStack(calcOffhand);
                    if (!isInOffhand(calcSlot))
                    {

                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param s
     * @return
     */
    private boolean isInOffhand(ItemStack s)
    {
        ItemStack offhand = mc.player.getOffHandStack();
        if (offhand.getItem() == Items.GOLDEN_APPLE
                && s.getItem() == Items.GOLDEN_APPLE)
        {
            return offhand.hasGlint() == s.hasGlint();
        }
        return offhand.getItem() == s.getItem();
    }
}
