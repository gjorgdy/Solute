package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Map;

public class FasterFarming {

    private static Map<Item, Integer> ranges = Map.of(
            Items.WOODEN_HOE, 1,
            Items.STONE_HOE, 2,
            Items.IRON_HOE, 3,
            Items.GOLDEN_HOE, 4,
            Items.DIAMOND_HOE, 4,
            Items.NETHERITE_HOE, 5
    );

    public static void farmArea(World world, BlockPos pos, ItemStack tool) {
        // Get range of tool
        int range = ranges.getOrDefault(tool.getItem(), 0);

        if (range == 0) {
            farmCrop(world, pos, pos);
            return;
        }
        //int width = range * 2+1;
        for (int y = -range; y <= range; y++) {
            for (int z = -range; z <= range; z++) {
                for (int x = -range; x <= range; x++) {
                    farmCrop( world, pos, pos.add(x, y, z) );
                }
            }
        }
        tool.damage(range, Random.create(), null);
    }

    public static void farmCrop(World world, BlockPos midPos, BlockPos pos) {
        // Get the block
        BlockState crop = world.getBlockState(pos);
        // Replace the crop with a new one if full-grown
        try {
            if (CropBlock.MAX_AGE > crop.get(CropBlock.AGE))
                return;
        } catch (IllegalArgumentException e) {
            return;
        }
        // Break the crop
        world.breakBlock(pos, false);
        // Loop through the items that should be dropped
        Block.getDroppedStacks(crop, (ServerWorld) world, pos, (BlockEntity) null).forEach((stack) -> {
            // If drop is a seed and block is air
            if (world.getBlockState(pos).isAir() && stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof CropBlock) {
                // Set a new block
                world.setBlockState(pos, ((BlockItem) stack.getItem()).getBlock().getDefaultState());
                // Remove a single seed from the stack
                stack.decrement(1);
            }
            // Drop the stack that's left
            Block.dropStack(world, midPos, stack);
        });
}

}
