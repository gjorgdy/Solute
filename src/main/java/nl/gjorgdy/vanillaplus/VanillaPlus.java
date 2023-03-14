package nl.gjorgdy.vanillaplus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.gjorgdy.vanillaplus.functions.ItemMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.*;

public class VanillaPlus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("VanillaPlus");
	public static final Database DATABASE = new Database();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Be prepared for your Quality of Life to be improved");

		// Register '/filter' command
		//CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
		//	literal("filter")
		//		.executes(context -> {
		//			// For versions below 1.19, replace "Text.literal" with "new LiteralText".
		//			ServerCommandSource src = context.getSource();
		//			ServerPlayerEntity player = src.getPlayerOrThrow();
		//			player.giveItemStack(ItemMaps.createItemMap(player.getMainHandStack().getItem()));
		//			return 1;
		//		})
		//	)
		//);
	}
}
