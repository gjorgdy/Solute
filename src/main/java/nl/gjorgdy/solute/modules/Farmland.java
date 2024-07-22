package nl.gjorgdy.solute.modules;

import net.minecraft.block.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.solute.utils.BlockPosUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Farmland {

    public static void farmArea(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return;
        ItemStack toolStack = context.getStack();
        int range = getRange(toolStack);
        // farm the crops
        int affectedBlocks = new Process(context)
                .start(range)
                .getAffectedBlocks();
        // deal damage to tool
        int damage = affectedBlocks / range;
        toolStack.damage(damage, player, context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    private static int getRange(ItemStack toolStack) {
        int range = 0;
        if (toolStack.getItem() instanceof ToolItem toolItem) {
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
        }
        return range;
    }

    private static class Process {

        private final ItemUsageContext itemUsageContext;
        private final ServerWorld world;
        private final BlockPos centerPos;
        private final ItemStack toolStack;
        private final ServerPlayerEntity player;
        private final List<ItemStack> itemStacks;

        private int affectedBlocks = 0;

        public Process(ItemUsageContext context) {
            this.itemUsageContext = context;
            this.world = (ServerWorld) context.getWorld();
            this.centerPos = context.getBlockPos();
            this.toolStack = context.getStack();
            this.player = (ServerPlayerEntity) context.getPlayer();
            this.itemStacks = new ArrayList<>();
        }

        public Process start(int range) {
            farmArea(itemUsageContext.getBlockPos(), itemUsageContext.getBlockPos(), range);
            return drop();
        }

        private void farmArea(BlockPos centerPos, BlockPos sourcePos, int depth) {
            // farm block
            BlockState centerState = itemUsageContext.getWorld().getBlockState(centerPos);
            if (centerState.getBlock() instanceof CropBlock) farmCrop(centerPos, centerState);
            if (depth == 0) return;
            // get surrounding
            BlockPosUtils.forNeighbours(centerPos, blockPos -> {
                if (blockPos.equals(sourcePos)) return;
                BlockState relativeState = itemUsageContext.getWorld().getBlockState(blockPos);
                if (relativeState.getBlock() instanceof CropBlock) {
                    farmArea(blockPos, sourcePos, depth - 1);
                }
            });
        }

        public void farmCrop(BlockPos pos, BlockState state) {
            // Loop through the items that should be dropped
            AtomicBoolean hasReplanted = new AtomicBoolean(false);
            Block.getDroppedStacks(state, world, pos, null, player, toolStack).forEach((stack) -> {
                // If drop is a seed and block is air
                if (!hasReplanted.get()
                        && stack.getItem() instanceof BlockItem blockItem
                        && blockItem.getBlock() instanceof CropBlock cropBlock
                ) {
                    // Remove a single seed from the stack
                    stack.decrement(1);
                    // Set a new block
                    BlockState cropBlockState = cropBlock.getDefaultState();
                    if (!world.getBlockState(pos).equals(cropBlockState)) {
                        world.setBlockState(pos, cropBlock.getDefaultState());
                        affectedBlocks++;
                    }
                    hasReplanted.set(true);
                }
                // Drop the stack that's left
                if (!stack.isEmpty()) {
                    itemStacks.add(stack);
                }
            });
            if (!hasReplanted.get()) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }

        private Process drop() {
            if (affectedBlocks > 0) {
                world.playSound(null, centerPos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS);
                itemStacks.forEach(itemStack -> Block.dropStack(world, centerPos, itemStack));
            }
            return this;
        }

        public int getAffectedBlocks() {
            return affectedBlocks;
        }

    }

}
