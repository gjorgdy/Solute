package nl.gjorgdy.solute.modules;

import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Farmland {

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
            range = 0;
            return;
        }
        // farm the crops
        int affectedBlocks = new Process(world, playerEntity, toolStack)
                .farmArea(centerPos, range)
                .drop(centerPos)
                .getAffectedBlocks();
        // deal damage to tool
        int damage = affectedBlocks / range;
        if (playerEntity.getStackInHand(Hand.MAIN_HAND) == toolStack) {
            toolStack.damage(damage, playerEntity, EquipmentSlot.MAINHAND);
        } else {
            toolStack.damage(damage, playerEntity, EquipmentSlot.OFFHAND);
        }
    }

    private static class Process {

        private final World world;
        private final PlayerEntity playerEntity;
        private final ItemStack toolStack;
        private final List<ItemStack> itemStacks;

        private int affectedBlocks = 0;

        public Process(World world, PlayerEntity playerEntity, ItemStack toolStack) {
            this.world = world;
            this.playerEntity = playerEntity;
            this.toolStack = toolStack;
            this.itemStacks = new ArrayList<>();
        }

        public Process farmArea(BlockPos centerPos, int depth) {
            return farmArea(centerPos, centerPos, depth);
        }

        public Process farmArea(BlockPos centerPos, BlockPos sourcePos, int depth) {
            // farm block
            BlockState centerState = world.getBlockState(centerPos);
            if (isValidCrop(centerState)) farmCrop(centerPos, centerState);
            if (depth == 0) return this;
            // get surrounding
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos relativePos = centerPos.add(x, y, z);
                        if (relativePos.equals(centerPos) || relativePos.equals(sourcePos)) continue;
                        BlockState relativeState = world.getBlockState(relativePos);
                        if (relativeState.getBlock() instanceof CropBlock) {
                            farmArea(relativePos, depth - 1);
                        }
                    }
                }
            }
            return this;
        }

        public void farmCrop(BlockPos pos, BlockState state) {
            // Loop through the items that should be dropped
            AtomicBoolean hasReplanted = new AtomicBoolean(false);
            Block.getDroppedStacks(state, (ServerWorld) world, pos, null, playerEntity, toolStack).forEach((stack) -> {
                // If drop is a seed and block is air
                if (!hasReplanted.get()
                        && stack.getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof CropBlock cropBlock
                ) {
                    // Remove a single seed from the stack
                    stack.decrement(1);
                    // Set a new block
                    world.setBlockState(pos, cropBlock.getDefaultState());
                    hasReplanted.set(true);
                }
                // Drop the stack that's left
                itemStacks.add(stack);
            });
            if (!hasReplanted.get()) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
            affectedBlocks++;
        }

        public Process drop(BlockPos pos) {
            if (affectedBlocks > 0) {
                world.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS);
                itemStacks.forEach(itemStack -> {
                    Block.dropStack(world, pos, itemStack);
                });
            }
            return this;
        }

        public int getAffectedBlocks() {
            return affectedBlocks;
        }

    }

    public static boolean isValidCrop(BlockState cropBlock) {
        return !(cropBlock.getBlock() instanceof StemBlock)
                && cropBlock.getBlock() instanceof CropBlock
                && CropBlock.MAX_AGE <= cropBlock.get(CropBlock.AGE);
    }

}
