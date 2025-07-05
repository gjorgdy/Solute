package nl.gjorgdy.solute;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import nl.gjorgdy.solute.config.SoluteConfig;
import nl.gjorgdy.solute.listeners.PlayerBlockBreakListener;
import nl.gjorgdy.solute.listeners.UseBlockCallbackListener;
import nl.gjorgdy.solute.utils.EnchantmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Solute implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("Solute");
	public static final SoluteConfig CONFIG = ConfigApiJava.registerAndLoadConfig(SoluteConfig::new);
	public static final String CONFIG_FILE = "config";
	public static final String CONFIG_FOLDER = "solute";

	@Override
	public void onInitialize() {

		LOGGER.info("Igniting furnace to add solute to the base");

		UseBlockCallback.EVENT.register(new UseBlockCallbackListener());
		PlayerBlockBreakEvents.BEFORE.register(new PlayerBlockBreakListener());

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			if (CONFIG.enchantmentsModule.excavation) {
				// Excavation
				var excavation = EnchantmentUtils.getEnchantmentFromString(server, "solute:excavation");
				excavation.ifPresent(enchantmentReference -> ENCHANTMENTS.EXCAVATION = enchantmentReference);
			}
			if (CONFIG.enchantmentsModule.drilling) {
				// Drilling
				var drilling = EnchantmentUtils.getEnchantmentFromString(server, "solute:drilling");
				drilling.ifPresent(enchantmentReference -> ENCHANTMENTS.DRILLING = enchantmentReference);
			}
			if (CONFIG.enchantmentsModule.crushing) {
				// Crushing
				var crushing = EnchantmentUtils.getEnchantmentFromString(server, "solute:crushing");
				crushing.ifPresent(enchantmentReference -> ENCHANTMENTS.CRUSHING = enchantmentReference);
			}
		});

	}

	public static class ENCHANTMENTS {
		public static RegistryEntry.Reference<Enchantment> EXCAVATION;
		public static RegistryEntry.Reference<Enchantment> DRILLING;
		public static RegistryEntry.Reference<Enchantment> CRUSHING;
	}
}
