package com.caspian.client.impl.module.combat;

import com.caspian.client.api.config.Config;
import com.caspian.client.api.config.setting.BooleanConfig;
import com.caspian.client.api.module.ModuleCategory;
import com.caspian.client.api.module.ToggleModule;
import net.minecraft.util.math.Vec3d;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class BowAimModule extends ToggleModule
{
    //
    Config<Boolean> playersConfig = new BooleanConfig("Players",
            "Aims bow at players", true);
    Config<Boolean> monstersConfig = new BooleanConfig("Monsters",
            "Aims bow at monsters", false);
    Config<Boolean> neutralsConfig = new BooleanConfig("Neutrals",
            "Aims bow at neutrals", false);
    Config<Boolean> animalsConfig = new BooleanConfig("Animals",
            "Aims bow at animals", false);

    /**
     *
     */
    public BowAimModule()
    {
        super("BowAim", "Automatically aims charged bow at nearby entities",
                ModuleCategory.COMBAT);
    }

    /**
     *
     * @param pos
     * @param target
     * @return
     */
    private float[] getBowRotationTo(Vec3d pos, Vec3d target)
    {
        return null;
    }
}
