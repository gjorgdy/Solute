package nl.gjorgdy.solute.modules;

import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import nl.gjorgdy.solute.utils.ItemUtils;

public class Rails {

    public static boolean place(ServerPlayerEntity player, ItemStack railItem, BlockState blockState, BlockPos pos) {
        World world = player.getWorld();
        RailShape shape = getRailShape(blockState);
        var mvVec = player.getMovementDirection().getVector();
        Vec3i vec = switch (shape) {
            case RailShape.EAST_WEST -> new Vec3i(1, 0, 0);
            case RailShape.NORTH_SOUTH -> new Vec3i(0, 0, 1);
            default -> throw new IllegalStateException("Unexpected value: " + shape);
        };
        if (!isSameAxis(mvVec, vec)) return false;
        BlockPos _pos = pos;
        for (int i = 0; i < 16; i++) {
            _pos = _pos.add(mvVec);
            var _blockState = world.getBlockState(_pos);
            if (_blockState.isOf(Blocks.AIR)) {
                return ItemUtils.place(railItem, player, _pos);
            } else if (world.getBlockState(_pos).getBlock() instanceof AbstractRailBlock) {
                if (getRailShape(_blockState) != shape) break;
            } else break;
        }
        return false;
    }

    private static boolean isSameAxis(Vec3i vecA, Vec3i vecB) {
        boolean x = (vecA.getX() == 0) == (vecB.getX() == 0);
        boolean y = (vecA.getY() == 0) == (vecB.getY() == 0);
        boolean z = (vecA.getZ() == 0) == (vecB.getZ() == 0);
        return x && y && z;
    }

    private static RailShape getRailShape(BlockState state) {
        return state
            .getOrEmpty(Properties.STRAIGHT_RAIL_SHAPE)
            .orElseGet(() -> state.get(Properties.RAIL_SHAPE));
    }

    // E-W X
    // S-N Z

}
