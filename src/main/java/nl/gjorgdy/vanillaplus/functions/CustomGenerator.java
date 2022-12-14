package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomGenerator {

    private Map<Block, Block> validBlocks = new HashMap<>();
    private int cryingObsidianChance = 16;

    public void registerBlock(Block bottomBlock, Block generateBlock) {
        validBlocks.put(bottomBlock, generateBlock);
    }

    public boolean replace(World world, BlockPos pos) {
        Block under = world.getBlockState(pos.add(0, -1, 0)).getBlock();
        if (world.getFluidState(pos).isStill()) {
            generateObsidian(world, pos);
        } else {
            world.setBlockState(pos, validBlocks.getOrDefault(under, Blocks.COBBLESTONE).getDefaultState());
        }
        return true;
    }

    private void generateObsidian(World world, BlockPos pos) {
        Random rand = new Random();
        if (rand.nextInt(cryingObsidianChance) < 1) {
            world.setBlockState(pos, Blocks.CRYING_OBSIDIAN.getDefaultState());
        } else {
            world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
        }
    }

}
