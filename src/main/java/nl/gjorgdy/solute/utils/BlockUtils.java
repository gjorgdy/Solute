package nl.gjorgdy.solute.utils;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.StairShape;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import nl.gjorgdy.solute.models.Drops;

public class BlockUtils {

    public static void dropExperience(ServerWorld world, BlockPos pos, int size) {
        if (world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), size);
        }
    }

    public static boolean isRedstonePowered(World world, BlockPos pos) {
        return world.getEmittedRedstonePower(pos, Direction.NORTH) > 0;
    }

    public static Drops breakBlockReturnDrop(ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack tool) {
        BlockState _blockState = world.getBlockState(pos);
        Block _block = _blockState.getBlock();
        BlockEntity _blockEntity = world.getBlockEntity(pos);
        boolean hasEntity = _blockEntity != null;
        // get drops
        var items = Block.getDroppedStacks(
                _blockState,
                world,
                pos,
                _blockEntity,
                player,
                tool
        );
        int experience = EnchantmentUtils.getExperienceDrops(world, tool, _block);
        // break the block and handle context
        world.breakBlock(pos, hasEntity, player);
        tool.postMine(world, _blockState, pos, player);
        player.incrementStat(Stats.MINED.getOrCreateStat(_block));
        player.addExhaustion(0.005F);
        PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(world, player, pos, _blockState, _blockEntity);
        // return drops
        return hasEntity ? Drops.EMPTY : new Drops(items, experience);
    }

    public static BlockState changeStairs(BlockState state, Block block) {
        if (state.getBlock() instanceof StairsBlock && block instanceof StairsBlock) {
            Direction facing = state.get(StairsBlock.FACING);
            BlockHalf half = state.get(StairsBlock.HALF);
            StairShape shape = state.get(StairsBlock.SHAPE);
            Boolean waterlogged = state.get(StairsBlock.WATERLOGGED);
            return block.getDefaultState()
                    .with(StairsBlock.FACING, facing)
                    .with(StairsBlock.HALF, half)
                    .with(StairsBlock.SHAPE, shape)
                    .with(StairsBlock.WATERLOGGED, waterlogged);
        }
        return state;
    }

    public static BlockState changeSlab(BlockState state, Block block) {
        if (state.getBlock() instanceof SlabBlock && block instanceof SlabBlock) {
            SlabType type = state.get(SlabBlock.TYPE);
            boolean waterlogged = state.get(SlabBlock.WATERLOGGED);
            return block.getDefaultState()
                    .with(SlabBlock.TYPE, type)
                    .with(SlabBlock.WATERLOGGED, waterlogged);
        }
        return state;
    }

    public static BlockState changeWall(BlockState state, Block block) {
        if (state.getBlock() instanceof WallBlock && block instanceof WallBlock) {
            WallShape northShape = state.get(WallBlock.NORTH_WALL_SHAPE);
            WallShape eastShape = state.get(WallBlock.EAST_WALL_SHAPE);
            WallShape southShape = state.get(WallBlock.SOUTH_WALL_SHAPE);
            WallShape westShape = state.get(WallBlock.WEST_WALL_SHAPE);
            boolean waterlogged = state.get(WallBlock.WATERLOGGED);
            boolean up = state.get(WallBlock.UP);
            return block.getDefaultState()
                    .with(WallBlock.NORTH_WALL_SHAPE, northShape)
                    .with(WallBlock.EAST_WALL_SHAPE, eastShape)
                    .with(WallBlock.SOUTH_WALL_SHAPE, southShape)
                    .with(WallBlock.WEST_WALL_SHAPE, westShape)
                    .with(WallBlock.WATERLOGGED, waterlogged)
                    .with(WallBlock.UP, up);
        }
        return state;
    }

    public static boolean isBottomSlab(BlockState blockState) {
        return blockState.getBlock() instanceof SlabBlock
                && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM;
    }

    public static boolean canBeMossy(Block block) {
        return block == Blocks.COBBLESTONE
                || block == Blocks.COBBLESTONE_WALL
                || block == Blocks.COBBLESTONE_STAIRS
                || block == Blocks.COBBLESTONE_SLAB
                || block == Blocks.STONE_BRICKS
                || block == Blocks.STONE_BRICK_WALL
                || block == Blocks.STONE_BRICK_STAIRS
                || block == Blocks.STONE_BRICK_SLAB
                || block == Blocks.INFESTED_STONE_BRICKS;
    }

    public static boolean isMossy(Block block) {
        return block == Blocks.MOSSY_COBBLESTONE
                || block == Blocks.MOSSY_COBBLESTONE_WALL
                || block == Blocks.MOSSY_COBBLESTONE_STAIRS
                || block == Blocks.MOSSY_COBBLESTONE_SLAB
                || block == Blocks.MOSSY_STONE_BRICKS
                || block == Blocks.MOSSY_STONE_BRICK_WALL
                || block == Blocks.MOSSY_STONE_BRICK_STAIRS
                || block == Blocks.MOSSY_STONE_BRICK_SLAB
                || block == Blocks.INFESTED_MOSSY_STONE_BRICKS;
    }

    public static boolean canCrack(Block block) {
        return block == Blocks.STONE_BRICKS
                || block == Blocks.DEEPSLATE_BRICKS
                || block == Blocks.DEEPSLATE_TILES
                || block == Blocks.NETHER_BRICKS
                || block == Blocks.POLISHED_BLACKSTONE_BRICKS
                || block == Blocks.INFESTED_STONE_BRICKS;
    }

    public static boolean isCracked(Block block) {
        return block == Blocks.CRACKED_STONE_BRICKS
                || block == Blocks.CRACKED_DEEPSLATE_BRICKS
                || block == Blocks.CRACKED_DEEPSLATE_TILES
                || block == Blocks.CRACKED_NETHER_BRICKS
                || block == Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS
                || block == Blocks.INFESTED_CRACKED_STONE_BRICKS;
    }

    public static boolean isConcretePowder(Block block) {
        return block instanceof ConcretePowderBlock;
    }

}
