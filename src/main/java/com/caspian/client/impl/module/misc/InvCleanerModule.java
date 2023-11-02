package com.caspian.client.impl.module.misc;

import com.caspian.client.Caspian;
import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.config.setting.NumberConfig;
import com.caspian.client.api.event.EventStage;
import com.caspian.client.api.event.listener.EventListener;
import com.caspian.client.api.file.ConfigFile;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import com.caspian.client.impl.event.TickEvent;
import com.caspian.client.util.math.timer.CacheTimer;
import com.caspian.client.util.math.timer.Timer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class InvCleanerModule extends ToggleModule
{
    //
    Config<Float> delayConfig = new NumberConfig<>("Delay", "The delay " +
            "between removing items from the inventory", 0.05f, 0.0f, 1.0f);
    Config<Boolean> hotbarConfig = new BooleanConfig("Hotbar", "Cleans the " +
            "hotbar inventory slots", true);
    //
    private final Timer invCleanTimer = new CacheTimer();
    //
    private final List<Item> blacklist = new ArrayList<>();

    /**
     *
     */
    public InvCleanerModule()
    {
        super("InvCleaner", "Automatically cleans the player inventory",
                ModuleCategory.MISCELLANEOUS);
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
        for (Item item : blacklist)
        {
            for (int i = 9; i < (hotbarConfig.getValue() ? 45 : 36); i++)
            {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.isEmpty())
                {
                    continue;
                }
                if (stack.getItem() == item && invCleanTimer.passed(delayConfig.getValue() * 1000))
                {
                    mc.interactionManager.clickSlot(0, i, 0,
                            SlotActionType.PICKUP, mc.player);
                    mc.interactionManager.clickSlot(0, ScreenHandler.EMPTY_SPACE_SLOT_INDEX,
                            0, SlotActionType.PICKUP, mc.player);
                    invCleanTimer.reset();
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public ConfigFile getBlacklistFile(Path clientDir)
    {
        return new InvCleanerFile(clientDir);
    }

    /**
     *
     * @see ConfigFile
     */
    public class InvCleanerFile extends ConfigFile
    {
        /**
         *
         */
        public InvCleanerFile(Path clientDir)
        {
            super(clientDir, "inv-cleaner");
        }

        /**
         * Saves the configuration to a <tt>.json</tt> file in the local
         * <tt>Caspian</tt> directory
         */
        @Override
        public void save()
        {
            try
            {
                Path filepath = getFilepath();
                if (!Files.exists(filepath))
                {
                    Files.createFile(filepath);
                }
                JsonObject json = new JsonObject();
                //
                JsonArray itemArray = new JsonArray();
                for (Item item : blacklist)
                {
                    itemArray.add(item.getTranslationKey());
                }
                json.add("items", itemArray);
                write(filepath, serialize(json));
            }
            // error writing file
            catch (IOException e)
            {
                Caspian.error("Could not save file for inv cleaner!");
                e.printStackTrace();
            }
        }

        /**
         * Loads the configuration from the associated <tt>.json</tt> file
         */
        @Override
        public void load()
        {
            try
            {
                Path filepath = getFilepath();
                if (Files.exists(filepath))
                {
                    String content = read(filepath);
                    JsonObject object = parseObject(content);
                    JsonArray jsonArray = object.getAsJsonArray("items");
                    for (JsonElement element : jsonArray)
                    {
                        // blacklist.add(Registries.ITEM.get(new Identifier(element.getAsString())));
                    }
                }
            }
            // error writing file
            catch (IOException e)
            {
                Caspian.error("Could not read file for inv cleaner!");
                e.printStackTrace();
            }
        }
    }
}
