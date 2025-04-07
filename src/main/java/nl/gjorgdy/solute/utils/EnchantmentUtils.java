package nl.gjorgdy.solute.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class EnchantmentUtils {

    public static Optional<RegistryEntry.Reference<Enchantment>> getEnchantmentFromString(MinecraftServer server, String enchantmentId) {
        Identifier id = Identifier.tryParse(enchantmentId);
        if (id != null) return server.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(id);
        return Optional.empty();
    }

}
