package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.VanillaPlus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomGenerator {

    private int cryingObsidianChance = 16;

    public static BlockState replace(World world, BlockPos pos, BlockState block) {
        Block under = world.getBlockState(pos.add(0, -1, 0)).getBlock();
        if (world.getFluidState(pos).isStill()) {
            return generateObsidian(world, pos);
        } else if (contains(under)) {
            return under.getDefaultState();
        } else {
            return Blocks.COBBLESTONE.getDefaultState();
        }
    }

    private static boolean contains(Block block) {
        String id = block.toString().split("\\{|\\}")[1];
        return VanillaPlus.CONFIG.generatorBlocks().containsKey(id);
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
