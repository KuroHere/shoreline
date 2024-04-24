package net.shoreline.client.api.config.setting;

import net.minecraft.item.Item;
import net.shoreline.client.api.config.Config;

import java.util.List;

public class ItemListConfig extends Config<List<Item>> {
    public ItemListConfig(String name, String desc, List<Item> value) {
        super(name, desc, value);
    }

    public void clear() {
        value.clear();
    }

    public void add(Item item) {
        value.add(item);
    }

    public boolean remove(Item item) {
        return value.remove(item);
    }
}
