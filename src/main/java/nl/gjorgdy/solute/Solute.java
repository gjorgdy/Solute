package nl.gjorgdy.solute;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import nl.gjorgdy.solute.config.SoluteConfig;
import nl.gjorgdy.solute.listeners.PlayerBlockBreakListener;
import nl.gjorgdy.solute.listeners.UseBlockCallbackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solute implements DedicatedServerModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Solute");
	public static final SoluteConfig CONFIG = ConfigApiJava.registerAndLoadConfig(SoluteConfig::new);
	public static final String CONFIG_FILE = "config";
	public static final String CONFIG_FOLDER = "solute";

	@Override
	public void onInitializeServer() {

		LOGGER.info("Igniting furnace to add solute to the base");

		UseBlockCallback.EVENT.register(new UseBlockCallbackListener());

		PlayerBlockBreakEvents.AFTER.register(new PlayerBlockBreakListener());

	}
}
