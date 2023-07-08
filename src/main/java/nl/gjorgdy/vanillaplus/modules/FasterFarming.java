package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FasterFarming {

    public static void farmArea(World world, BlockPos centerPos, PlayerEntity playerEntity, ItemStack toolStack) {

        Item toolItem = toolStack.getItem();

        int range;
        if (toolItem.equals(Items.WOODEN_HOE))
            range = 1;
        else if (toolItem.equals(Items.STONE_HOE))
            range = 2;
        else if (toolItem.equals(Items.IRON_HOE))
            range = 3;
        else if (toolItem.equals(Items.GOLDEN_HOE) || toolItem.equals(Items.DIAMOND_HOE))
            range = 4;
        else if (toolItem.equals(Items.NETHERITE_HOE))
            range = 6;
        else {
            farmCrop(world, centerPos, centerPos);
            return;
        }
        // Cube around centerPos
        for (int y = -range; y <= range; y++) { for (int z = -range; z <= range; z++) { for (int x = -range; x <= range; x++) {
            BlockPos _pos = centerPos.add(x, y, z);
            if (world.canPlayerModifyAt(playerEntity, _pos))
                if (farmCrop( world, centerPos, _pos) && !playerEntity.isCreative()) {
                    toolStack.damage(1, playerEntity, t -> {});
                    toolStack.postMine(world, world.getBlockState(_pos), _pos, playerEntity);
                }
            if (toolStack.getDamage() >= toolStack.getMaxDamage())
                return;
        }}}
    }

    public static boolean farmCrop(World world, BlockPos midPos, BlockPos pos) {
        // Get the block
        BlockState crop = world.getBlockState(pos);
        // Replace the crop with a new one if full-grown
        try {
            if (isStemBlock(crop) || CropBlock.MAX_AGE > crop.get(CropBlock.AGE))
                return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        // Break the crop
        world.breakBlock(pos, false);
        // Loop through the items that should be dropped
        Block.getDroppedStacks(crop, (ServerWorld) world, pos, null).forEach((stack) -> {
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
        return true;
    }

    public static boolean isStemBlock(BlockState cropBlock) {
        return cropBlock.getBlock() instanceof StemBlock;
    }

}
