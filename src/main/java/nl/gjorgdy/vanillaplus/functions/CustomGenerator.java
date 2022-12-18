package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.VanillaPlus;

import java.util.Map;
import java.util.Random;

import static nl.gjorgdy.vanillaplus.functions.BlockFunctions.getBlockFromID;

public class CustomGenerator {

    private int cryingObsidianChance = 16;

    public static BlockState replace(World world, BlockPos pos, BlockState block) {
        Block under = world.getBlockState(pos.add(0, -1, 0)).getBlock();
        if (block.getBlock() == Blocks.OBSIDIAN) {
            return generateObsidian(world, pos);
        } else if (block.getBlock() == Blocks.COBBLESTONE) {
            return replacementMap(under).getDefaultState();
        } else {
            return block;
        }
    }

    private static Block replacementMap(Block block) {
        Map<String, String> config = VanillaPlus.CONFIG.generatorBlocks();
        for (String id : config.keySet()) {
            if (getBlockFromID(id) == block) {
                return getBlockFromID( config.get(id) );
            }
        }
        return Blocks.COBBLESTONE;
    }

    private static BlockState generateObsidian(World world, BlockPos pos) {
        Random rand = new Random();
        if (rand.nextInt(16) < 1) {
            return Blocks.CRYING_OBSIDIAN.getDefaultState();
        } else {
            return Blocks.OBSIDIAN.getDefaultState();
        }
    }

}
