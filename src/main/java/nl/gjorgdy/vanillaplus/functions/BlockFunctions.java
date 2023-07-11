package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.*;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFunctions {

    public static void updateBlock(World world, BlockPos pos, boolean save) {
        // BlockState
        BlockState blockState = world.getBlockState(pos);
        // Force updates to players
        world.updateListeners(pos, blockState.getBlock().getDefaultState(), blockState, 3);
        // Force saving sign
        if (save) {
            world.markDirty(pos);
        }
    }

    public static boolean isGlassBlock(Block block) {
        return block instanceof GlassBlock
                || block instanceof StainedGlassBlock
                || block instanceof TintedGlassBlock;
    }

    public static boolean isBottomSlab(BlockState blockState) {
        return blockState.getBlock() instanceof SlabBlock
                && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM;
    }

}
