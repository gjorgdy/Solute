package nl.gjorgdy.solute.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Translation;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.config.Config;
import net.minecraft.util.Identifier;
import nl.gjorgdy.solute.Solute;

@Version(version = 3)
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

    @Translation(prefix = "solute.copperGolemModule")
    @Comment(value = "Let copper golems put music discs directly into jukeboxes.")
    public ToggleModuleConfig copperGolemModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.creeperModule")
    @Comment(value = "Disables block damage caused by creepers and ghast fireballs.")
    public ToggleModuleConfig creeperModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.farmlandModule")
    @Comment(value = "Farm an area of crops in an instant using a hoe. Reduce the impact of fall damage on farmland.")
    public ToggleModuleConfig farmlandModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.glowBerryModule")
    @Comment(value = "When eating a glow berry, you will temporarily receive the 'glow' effect.")
    public ToggleModuleConfig glowBerryModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.grassModule")
    @Comment(value = "Use bone meal on a grass block to spread grass blocks faster.")
    public ToggleModuleConfig grassModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.ironBarsModule")
    @Comment(value = "Use stacked iron bars or end rods as a fireman pole.")
    public BarsModuleConfig ironBarsModule = new BarsModuleConfig(true, -0.75, 0.85);

    @Translation(prefix = "solute.ladderModule")
    @Comment(value = "Make ladders be able to support each other. Also allows right-clicking to lower them.")
    public ToggleModuleConfig ladderModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.lavaModule")
    @Comment(value = "Make (cobble)stone generators generate blocks based on the biome and height it's placed at.")
    public ToggleModuleConfig lavaModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.pathModule")
    @Comment(value = "Turn path blocks back into dirt by right-clicking them with a shovel.")
    public ToggleModuleConfig pathModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.purpurModule")
    @Comment(value = "Make redstone powered purpur blocks function like vertical teleporters.")
    public ToggleModuleConfig purpurModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.railsModule")
    @Comment(value = "Let rails extend forward by clicking on it with another rail block.")
    public ToggleModuleConfig railsModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.slimeModule")
    @Comment(value = "Right-click a slime ball to see if you're in a slime chunk.")
    public ToggleModuleConfig slimeModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.tintedModule")
    @Comment(value = "Make Tinted Glass blast proof")
    public ToggleModuleConfig tintedModule = new ToggleModuleConfig(true);

    @Translation(prefix = "solute.tntModule")
    @Comment(value = "Explode items into their 'crushed' alternative. (sandstone -> sand, cobblestone -> gravel)")
    public ToggleModuleConfig tntModule = new ToggleModuleConfig(true);

}
