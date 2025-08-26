package nl.gjorgdy.solute.modules;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import nl.gjorgdy.solute.utils.ItemUtils;

public class Ladder {

    public static boolean lower(PlayerEntity player, ItemStack stack, World world, BlockPos pos) {
        for (int i = 0 ; i < 16 ; i++) {
            BlockPos _pos = pos.down(i);
            BlockState _block = world.getBlockState(_pos);
            if (_block.isOf(Blocks.AIR) || _block.isOf(Blocks.WATER)) {
                if (ItemUtils.place(stack, player, _pos, SoundEvents.BLOCK_LADDER_PLACE)) {
                    return true;
                } else {
                    break;
                }
            } else if (!_block.isOf(Blocks.LADDER)) break;
        }
        return false;
    }

    public static boolean canBeSupported(BlockState blockState, WorldView world, BlockPos pos) {
        BlockState upperBlock = world.getBlockState(pos.up());
        if (!blockState.isOf(Blocks.LADDER) || !upperBlock.isOf(Blocks.LADDER)) return false;
        return blockState.get(LadderBlock.FACING) == upperBlock.get(LadderBlock.FACING);
    }

    public static boolean isSupported(WorldView world, BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        return canBeSupported(block, world, pos);
    }

    public static void updateLadder(WorldView world, BlockPos pos) {
        if (world instanceof WorldAccess worldAccess) {
            BlockState block = world.getBlockState(pos);
            if (block.isOf(Blocks.LADDER)) {
                Direction _facing = block.get(LadderBlock.FACING);
                BlockPos _facingPos = pos.offset(_facing.getOpposite());
                if (!(world.getBlockState(_facingPos).isSolidBlock(world, _facingPos) || isSupported(world, pos))) {
                    worldAccess.breakBlock(pos, true);
                    updateLadder(world, pos.down());
                }
            }
        }
    }

}
