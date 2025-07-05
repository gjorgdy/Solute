package nl.gjorgdy.solute.config;

import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;

public class CraftingTableModuleConfig extends ToggleModuleConfig {

    public CraftingTableModuleConfig(boolean enabled) {
        super(enabled);
    }

    public ConfigGroup recipesGroup = new ConfigGroup("recipes");

    public boolean copperRails = true;

    public boolean cheaperRails = true;

    public boolean smeltRawMetalBlocks = true;

}
