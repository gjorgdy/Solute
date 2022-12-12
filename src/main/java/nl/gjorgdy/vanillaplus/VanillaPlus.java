package nl.gjorgdy.vanillaplus;

import net.fabricmc.api.ModInitializer;
import nl.gjorgdy.vanillaplus.callbacks.PlayerEntityCallback;
import nl.gjorgdy.vanillaplus.functions.Elevator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaPlus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("VanillaPlus");
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world! Your leader speaking, please do what I say");

		//PlayerJumpCallback.EVENT.register(player -> {
		//	LOGGER.info("Player jumped");
		//	return ActionResult.SUCCESS;
		//});
		Elevator elevator = new Elevator(LOGGER);
		PlayerEntityCallback.JUMP_EVENT.register(elevator::up);
	}
}
