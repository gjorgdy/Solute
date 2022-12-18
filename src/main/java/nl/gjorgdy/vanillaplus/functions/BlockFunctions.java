package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class BlockFunctions {

    public static Block getBlockFromID(@NotNull String id) {
        return Registries.BLOCK.get(new Identifier(id));
    }
}
