package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ExpandedFarming {

    public static void farmArea(World world, BlockPos pos, ItemStack tool, int range) {
        if (range == 0) {
            farmCrop(world, pos);
            return;
        }
        //int width = range * 2+1;
        for (int z = -range; z <= range; z++) {
            for (int x = -range; x <= range; x++) {
                farmCrop(world, pos.add(x, 0, z));
            }
        }
        tool.damage(range, Random.create(), null);
    }

    public static void farmCrop(World world, BlockPos pos) {
        // Get the block
        BlockState crop = world.getBlockState(pos);
        Block cropBlock = crop.getBlock();
        // Replace the crop with a new one if full-grown
        try {
            if (CropBlock.MAX_AGE == crop.get(CropBlock.AGE)) {
                world.breakBlock(pos, true);
                world.setBlockState(pos, cropBlock.getDefaultState());
            }
        } catch (Exception e) {
            return;
        }

    }

    public static int getRange(Item tool) {
        if (tool == Items.WOODEN_HOE) {
            return 1;
        } else if (tool == Items.STONE_HOE) {
            return  2;
        } else if (tool == Items.IRON_HOE | tool == Items.GOLDEN_HOE) {
            return  3;
        } else if (tool == Items.DIAMOND_HOE) {
            return  4;
        } else if (tool == Items.NETHERITE_HOE) {
            return 5;
        } else {
            return 0;
        }
    }

}
