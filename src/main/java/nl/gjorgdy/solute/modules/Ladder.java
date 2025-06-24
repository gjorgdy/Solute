package nl.gjorgdy.solute.modules;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
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
                if (ItemUtils.place(stack, player, _pos)) {
                    world.playSound(null, _pos, SoundEvents.BLOCK_LADDER_PLACE, SoundCategory.BLOCKS);
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
        return blockState.get(HorizontalFacingBlock.FACING) == upperBlock.get(HorizontalFacingBlock.FACING);
    }

    public static boolean isSupported(WorldView world, BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        return canBeSupported(block, world, pos);
    }

    public static void updateLadder(WorldView world, BlockPos pos) {
        if (world instanceof WorldAccess worldAccess) {
            BlockState block = world.getBlockState(pos);
            int x = pos.getX();
            int z = pos.getZ();
            for (int y = pos.getY() - 1; y > -64; y--) {
                BlockPos _pos = new BlockPos(x, y, z);
                BlockState _block = world.getBlockState(_pos);
                if (_block.equals(block)) {
                    Direction _facing = _block.get(LadderBlock.FACING);
                    BlockPos _facingPos = _pos.offset(_facing.getOpposite());
                    if (!world.getBlockState(_facingPos).isSolidBlock(world, _facingPos)) {
                        worldAccess.breakBlock(_pos, true);
                        continue;
                    }
                }
                return;
            }
        }
    }

}
