package nl.gjorgdy.solute.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.BlockEvent;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import nl.gjorgdy.solute.utils.ToolUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static nl.gjorgdy.solute.utils.BlockUtils.breakBlockReturnDrop;

public class Enchantment {

    public static Optional<ItemStack> crush(Random random, Block block, int fortune) {
        if (block == Blocks.BLACK_CONCRETE) {
            return Optional.ofNullable(Items.BLACK_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.LIGHT_BLUE_CONCRETE) {
            return Optional.ofNullable(Items.LIGHT_BLUE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.BLUE_CONCRETE) {
            return Optional.ofNullable(Items.BLUE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.CYAN_CONCRETE) {
            return Optional.ofNullable(Items.CYAN_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.LIME_CONCRETE) {
            return Optional.ofNullable(Items.LIME_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.GREEN_CONCRETE) {
            return Optional.ofNullable(Items.GREEN_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.YELLOW_CONCRETE) {
            return Optional.ofNullable(Items.YELLOW_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.ORANGE_CONCRETE) {
            return Optional.ofNullable(Items.ORANGE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.BROWN_CONCRETE) {
            return Optional.ofNullable(Items.BROWN_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.RED_CONCRETE) {
            return Optional.ofNullable(Items.RED_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.PURPLE_CONCRETE) {
            return Optional.ofNullable(Items.PURPLE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.MAGENTA_CONCRETE) {
            return Optional.ofNullable(Items.MAGENTA_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.PINK_CONCRETE) {
            return Optional.ofNullable(Items.PINK_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.WHITE_CONCRETE) {
            return Optional.ofNullable(Items.WHITE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.LIGHT_GRAY_CONCRETE) {
            return Optional.ofNullable(Items.LIGHT_GRAY_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.GRAY_CONCRETE) {
            return Optional.ofNullable(Items.GRAY_CONCRETE_POWDER.getDefaultStack());
        }
        // convert
        if (block == Blocks.SANDSTONE) {
            var stack = Items.SAND.getDefaultStack();
            stack.setCount(getAmount(fortune, random));
            return Optional.of(stack);
        }
        if (block == Blocks.RED_SANDSTONE) {
            var stack = Items.RED_SAND.getDefaultStack();
            stack.setCount(getAmount(fortune, random));
            return Optional.of(stack);
        }
        if (block == Blocks.COBBLESTONE) {
            var stack = Items.GRAVEL.getDefaultStack();
            stack.setCount(getAmount(fortune, random));
            return Optional.of(stack);
        }
        return Optional.empty();
    }

    private static int getAmount(int fortune, Random random) {
        // calculate amount
        int amount = random.nextInt(1) + 1;
        return Math.min(amount + fortune, 4);
    }

    public static void drill(World world, PlayerEntity player, BlockPos pos, BlockState blockState, int depth) {
        var tool = player.getMainHandStack();
        var toolHandler = new ToolUtils.ToolHandler(tool);
        if (!toolHandler.test(blockState)) return;
        HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0, false);

        List<ItemStack> drops = new ArrayList<>();

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hitResult;
            float hardnessRef = blockState.getBlock().getHardness();
            var dir = bhr.getSide().getVector().multiply(-1);
            for (int i = 0; i < depth; i++) {
                var _pos = pos.add(dir.multiply(i));
                var breakDrops = tryBreakBlock(world, _pos, player, tool, hardnessRef, toolHandler);
                if (breakDrops.isEmpty()) break;
                drops.addAll(breakDrops);
            }
        }

        if (!player.isCreative())
            drops.forEach(drop -> Block.dropStack(world, pos, drop));
    }

    public static void excavate(World world, PlayerEntity player, BlockPos pos, BlockState blockState) {
        var tool = player.getMainHandStack();
        var toolHandler = new ToolUtils.ToolHandler(tool);
        if (!toolHandler.test(blockState)) return;

        List<ItemStack> drops = new ArrayList<>();

        HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hitResult;
            float hardnessRef = blockState.getBlock().getHardness();
            ForAxis(bhr.getSide(), pos, (_pos) -> {
                drops.addAll(tryBreakBlock(world, _pos, player, tool, hardnessRef, toolHandler));
            });
        }

        if (!player.isCreative())
            drops.forEach(drop -> Block.dropStack(world, pos, drop));
    }

    private static List<ItemStack> tryBreakBlock(World world, BlockPos pos, PlayerEntity player, ItemStack tool, float hardnessRef, ToolUtils.ToolHandler toolHandler) {

        BlockState _blockState = world.getBlockState(pos);
        float hardness = _blockState.getBlock().getHardness();
        if (Math.abs(hardnessRef - hardness) < 0.5f && toolHandler.test(_blockState)) {
            return breakBlockReturnDrop(world, pos, player, tool);
        }
        return List.of();
    }

    private static void ForAxis(Direction direction, BlockPos center, Consumer<BlockPos> consumer) {
        Vec3i vec = direction.getVector();
        ForAxis(vec.getX() == 0, vec.getY() == 0, vec.getZ() == 0, center, consumer);
    }

    private static void ForAxis(boolean xAxis, boolean yAxis, boolean zAxis, BlockPos center, Consumer<BlockPos> consumer) {
        for (int x = xAxis ? -1 : 0; x <= (xAxis ? 1 : 0); x++) {
            for (int y = yAxis ? -1 : 0; y <= (yAxis ? 1 : 0); y++) {
                for (int z = zAxis ? -1 : 0; z <= (zAxis ? 1 : 0); z++) {
                    consumer.accept(center.add(x, y, z));
                }
            }
        }
    }

}
