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
            if (isValidCrop(centerState)) farmCrop(centerPos, centerState);
            if (depth == 0) return;
            // get surrounding
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos relativePos = centerPos.add(x, y, z);
                        if (relativePos.equals(centerPos) || relativePos.equals(sourcePos)) continue;
                        BlockState relativeState = itemUsageContext.getWorld().getBlockState(relativePos);
                        if (relativeState.getBlock() instanceof CropBlock) {
                            farmArea(relativePos, sourcePos, depth - 1);
                        }
                    }
                }
            }
        }

        public void farmCrop(BlockPos pos, BlockState state) {
            // Loop through the items that should be dropped
            AtomicBoolean hasReplanted = new AtomicBoolean(false);
            Block.getDroppedStacks(state, (ServerWorld) world, pos, null, player, toolStack).forEach((stack) -> {
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

    public static boolean isValidCrop(BlockState cropBlock) {
        return !(cropBlock.getBlock() instanceof StemBlock)
                && cropBlock.getBlock() instanceof CropBlock
                && CropBlock.MAX_AGE <= cropBlock.get(CropBlock.AGE);
    }

}
