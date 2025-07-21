package nl.gjorgdy.solute.config;

import me.fzzyhmstrs.fzzy_config.config.ConfigSection;

public class ToggleModuleConfig extends ConfigSection {

    public boolean enabled;

    public ToggleModuleConfig(boolean enabled) {
        this.enabled = enabled;
    }

}
