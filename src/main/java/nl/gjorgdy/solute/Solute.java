package nl.gjorgdy.solute;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.session.telemetry.WorldLoadedEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.WorldEvents;
import nl.gjorgdy.solute.listeners.PlayerBlockBreakListener;
import nl.gjorgdy.solute.listeners.UseBlockCallbackListener;
import nl.gjorgdy.solute.utils.EnchantmentUtils;
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

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			var excavation = EnchantmentUtils.getEnchantmentFromString(server, "solute:excavation");
            excavation.ifPresent(enchantmentReference -> ENCHANTMENTS.EXCAVATION = enchantmentReference);
		});

	}

	public static class ENCHANTMENTS {
		public static RegistryEntry.Reference<Enchantment> EXCAVATION;
		public static RegistryEntry.Reference<Enchantment> DRILLING;
	}
}
