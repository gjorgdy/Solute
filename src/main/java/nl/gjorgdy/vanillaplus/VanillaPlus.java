package nl.gjorgdy.vanillaplus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import nl.gjorgdy.vanillaplus.listeners.PlayerBlockBreakListener;
import nl.gjorgdy.vanillaplus.listeners.UseBlockCallbackListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaPlus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("VanillaPlus");
	//public static final Database DATABASE = new Database();

	@Override
	public void onInitialize() {

		LOGGER.info("Be prepared for your Quality of Life to be improved");

		UseBlockCallback.EVENT.register(new UseBlockCallbackListener());
		PlayerBlockBreakEvents.BEFORE.register(new PlayerBlockBreakListener());

	}
}
