package nl.gjorgdy.solute.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.minecraft.util.Identifier;
import nl.gjorgdy.solute.Solute;

@Version(version = 1)
public class SoluteConfig extends Config {

    public SoluteConfig() {
        super(Identifier.of(Solute.CONFIG_FOLDER, Solute.CONFIG_FILE), "", "", "Solute");
    }

    @Translation(prefix = "solute.bedModule")
    @Comment(value = "Sleep through the night by speeding up time.")
    public ToggleModuleConfig bedModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.bricksModule")
    @Comment(value = "Interact with bricks to switch between cracked, mossy and normal variants.")
    public ToggleModuleConfig bricksModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.cauldronModule")
    @Comment(value = "Washing blocks in cauldrons. example ; concrete powder to concrete")
    public ToggleModuleConfig cauldronModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.craftingTableModule")
    @Comment(value = "Add a selection of new crafting recipes.")
    public CraftingTableModuleConfig craftingTableModule = new CraftingTableModuleConfig(true);

    @Translation(prefix = "solute.enchantmentsModule")
    @Comment(value = "Add custom enchantments for easier mining.")
    public EnchantmentModuleConfig enchantmentsModule = new EnchantmentModuleConfig(true);


}
