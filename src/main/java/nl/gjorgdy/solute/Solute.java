package nl.gjorgdy.solute;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import nl.gjorgdy.solute.listeners.PlayerBlockBreakListener;
import nl.gjorgdy.solute.listeners.UseBlockCallbackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solute implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Solute");
	//public static final Database DATABASE = new Database();

	@Override
	public void onInitialize() {

		LOGGER.info("Igniting furnace to add solute to the base");

		UseBlockCallback.EVENT.register(new UseBlockCallbackListener());
		PlayerBlockBreakEvents.BEFORE.register(new PlayerBlockBreakListener());

	}
}
