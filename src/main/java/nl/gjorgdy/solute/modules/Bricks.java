package nl.gjorgdy.solute.modules;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.solute.utils.BlockUtils;

public class Bricks {

    private static boolean setBlock(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player, BlockState newState) {
        if (newState != state) {
            var oldState = world.getBlockState(pos);
            var entity = world.getBlockEntity(pos);
            world.setBlockState(pos, newState);
            PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(world, player, pos, oldState, entity);
            return true;
        }
        return false;
    }

    public static boolean usePickaxeOnStone(ServerWorld world, BlockPos pos, BlockState state) {
        BlockState newState = crackStone(state);
        if (newState != state) {
            world.setBlockState(pos, newState);
            return true;
        }
        return false;
    }

    public static boolean useClayOnStone(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockState newState = repairStone(state);
        return setBlock(world, pos, state, player, newState);
    }

    private static BlockState crackStone(BlockState blockState) {
        if (blockState.isOf(Blocks.STONE_BRICKS)) {
            return Blocks.CRACKED_STONE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.DEEPSLATE_BRICKS)) {
            return Blocks.CRACKED_DEEPSLATE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.DEEPSLATE_TILES)) {
            return Blocks.CRACKED_DEEPSLATE_TILES.getDefaultState();
        }
        if (blockState.isOf(Blocks.NETHER_BRICKS)) {
            return Blocks.CRACKED_NETHER_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.POLISHED_BLACKSTONE_BRICKS)) {
            return Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.INFESTED_STONE_BRICKS)) {
            return Blocks.INFESTED_CRACKED_STONE_BRICKS.getDefaultState();
        }
        return blockState;
    }

    private static BlockState repairStone(BlockState blockState) {
        if (blockState.isOf(Blocks.CRACKED_STONE_BRICKS)) {
            return Blocks.STONE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.CRACKED_DEEPSLATE_BRICKS)) {
            return Blocks.DEEPSLATE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.CRACKED_DEEPSLATE_TILES)) {
            return Blocks.DEEPSLATE_TILES.getDefaultState();
        }
        if (blockState.isOf(Blocks.CRACKED_NETHER_BRICKS)) {
            return Blocks.NETHER_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS)) {
            return Blocks.POLISHED_BLACKSTONE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.INFESTED_CRACKED_STONE_BRICKS)) {
            return Blocks.INFESTED_STONE_BRICKS.getDefaultState();
        }
        return blockState;
    }

    public static boolean shearMoss(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockState newState = removeMoss(state);
        return setBlock(world, pos, state, player, newState);
    }

    private static BlockState removeMoss(BlockState blockState) {
        // stone bricks
        if (blockState.isOf(Blocks.MOSSY_STONE_BRICK_STAIRS)) {
            return BlockUtils.changeStairs(blockState, Blocks.STONE_BRICK_STAIRS);
        }
        if (blockState.isOf(Blocks.MOSSY_STONE_BRICK_SLAB)) {
            return BlockUtils.changeSlab(blockState, Blocks.STONE_BRICK_SLAB);
        }
        if (blockState.isOf(Blocks.MOSSY_STONE_BRICK_WALL)) {
            return BlockUtils.changeWall(blockState, Blocks.STONE_BRICK_WALL);
        }
        if (blockState.isOf(Blocks.MOSSY_STONE_BRICKS)) {
            return Blocks.STONE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.INFESTED_MOSSY_STONE_BRICKS)) {
            return Blocks.INFESTED_STONE_BRICKS.getDefaultState();
        }
        /// cobblestone
        if (blockState.isOf(Blocks.MOSSY_COBBLESTONE_STAIRS)) {
            return BlockUtils.changeStairs(blockState, Blocks.COBBLESTONE_STAIRS);
        }
        if (blockState.isOf(Blocks.MOSSY_COBBLESTONE_SLAB)) {
            return BlockUtils.changeSlab(blockState, Blocks.COBBLESTONE_SLAB);
        }
        if (blockState.isOf(Blocks.MOSSY_COBBLESTONE_WALL)) {
            return BlockUtils.changeWall(blockState, Blocks.COBBLESTONE_WALL);
        }
        if (blockState.isOf(Blocks.MOSSY_COBBLESTONE)) {
            return Blocks.COBBLESTONE.getDefaultState();
        }
        return blockState;
    }

    public static boolean placeVines(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockState newState = addMoss(state);
        return setBlock(world, pos, state, player, newState);
    }

    private static BlockState addMoss(BlockState blockState) {
        // stone bricks
        if (blockState.isOf(Blocks.STONE_BRICK_STAIRS)) {
            return BlockUtils.changeStairs(blockState, Blocks.MOSSY_STONE_BRICK_STAIRS);
        }
        if (blockState.isOf(Blocks.STONE_BRICK_SLAB)) {
            return BlockUtils.changeSlab(blockState, Blocks.MOSSY_STONE_BRICK_SLAB);
        }
        if (blockState.isOf(Blocks.STONE_BRICK_WALL)) {
            return BlockUtils.changeWall(blockState, Blocks.MOSSY_STONE_BRICK_WALL);
        }
        if (blockState.isOf(Blocks.STONE_BRICKS)) {
            return Blocks.MOSSY_STONE_BRICKS.getDefaultState();
        }
        if (blockState.isOf(Blocks.INFESTED_STONE_BRICKS)) {
            return Blocks.INFESTED_MOSSY_STONE_BRICKS.getDefaultState();
        }
        /// cobblestone
        if (blockState.isOf(Blocks.COBBLESTONE_STAIRS)) {
            return BlockUtils.changeStairs(blockState, Blocks.MOSSY_COBBLESTONE_STAIRS);
        }
        if (blockState.isOf(Blocks.COBBLESTONE_SLAB)) {
            return BlockUtils.changeSlab(blockState, Blocks.MOSSY_COBBLESTONE_SLAB);
        }
        if (blockState.isOf(Blocks.COBBLESTONE_WALL)) {
            return BlockUtils.changeWall(blockState, Blocks.MOSSY_COBBLESTONE_WALL);
        }
        if (blockState.isOf(Blocks.COBBLESTONE)) {
            return Blocks.MOSSY_COBBLESTONE.getDefaultState();
        }
        return blockState;
    }

}
