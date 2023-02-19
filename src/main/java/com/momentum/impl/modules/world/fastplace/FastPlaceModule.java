package com.momentum.impl.modules.world.fastplace;

import com.momentum.api.feature.Option;
import com.momentum.api.module.Module;
import com.momentum.api.module.ModuleCategory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author linus
 * @since 02/18/2023
 */
public class FastPlaceModule extends Module {

    // whitelist
    public final Collection<Item> whitelist = Arrays.asList(
            Items.EXPERIENCE_BOTTLE,
            Items.SNOWBALL,
            Items.EGG
    );

    // blacklist
    public final Collection<Item> blacklist = Arrays.asList(
            Items.ENDER_EYE,
            Items.ENDER_PEARL
    );

    // fast place options
    public final Option<Collection<Item>> whitelistOption =
            new Option<>("Whitelist", "Fast place item whitelist", whitelist);
    public final Option<Collection<Item>> blacklistOption =
            new Option<>("Blacklist", "Fast place item whitelist", blacklist);

    public FastPlaceModule() {
        super("FastPlace", new String[] {"FastExp", "FastXp"}, "Place faster.", ModuleCategory.WORLD);

        // options
        associate(
                whitelistOption,
                blacklistOption
        );
    }
}
