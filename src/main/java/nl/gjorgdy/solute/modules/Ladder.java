package nl.gjorgdy.solute.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class Ladder {

    public static void lower(PlayerEntity player, ItemStack stack, World world, BlockPos pos) {
        BlockState ladderBlock = world.getBlockState(pos);
        for (int i = 0 ; i < 16 ; i++) {
            BlockPos _pos = pos.down(i);
            BlockState _block = world.getBlockState(_pos);
            if (_block.isOf(Blocks.AIR) || _block.isOf(Blocks.WATER)) {
                if (world.canPlayerModifyAt(player, _pos)) {
                    world.setBlockState(_pos, ladderBlock);
                    world.playSound(null, _pos, SoundEvents.BLOCK_LADDER_PLACE, SoundCategory.BLOCKS);
                    if (!player.isCreative())
                        stack.setCount(stack.getCount() - 1);
                }
                return;
            } else if (!_block.isOf(Blocks.LADDER)) {
                return;
            }
        }
    }

    public static boolean isSupported(WorldView world, BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        BlockState upperBlock = world.getBlockState(pos.up());
        return block.equals(upperBlock);
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
