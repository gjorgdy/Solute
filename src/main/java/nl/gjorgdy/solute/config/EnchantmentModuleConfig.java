package nl.gjorgdy.solute.config;

import me.fzzyhmstrs.fzzy_config.config.ConfigGroup;

public class EnchantmentModuleConfig extends ToggleModuleConfig {

    public EnchantmentModuleConfig(boolean enabled) {
        super(enabled);
    }

    public ConfigGroup enchantmentsGroup = new ConfigGroup("enchantments");

    public boolean excavation = true;

    public boolean drilling = true;

    public boolean crushing = true;

}
