package nl.gjorgdy.solute.modules;

import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import nl.gjorgdy.solute.utils.ItemUtils;

public class Rails {

    public static boolean place(ServerPlayerEntity player, ItemStack railItem, BlockState blockState, BlockPos pos) {
        World world = player.getEntityWorld();
        RailShape shape = getRailShape(blockState);
        var playerDirection = player.getMovementDirection();
        boolean xAxisRails = isXAxis(shape);
        boolean xAxisPlayer = (playerDirection == Direction.EAST) || (playerDirection == Direction.WEST);
        return xAxisRails == xAxisPlayer &&
            forward(world, player, playerDirection.getVector(), railItem, pos, xAxisRails, 8, false);
    }

    private static boolean forward(World world, PlayerEntity player, Vec3i vec, ItemStack railItem, BlockPos pos, boolean xAxisRails, int depth, boolean movedVertically) {
        if (depth == 0) return false;
        var _blockState = world.getBlockState(pos);
        // if there is place
        if (_blockState.isAir()) {
            var _blockStateDown = world.getBlockState(pos.down());
            if ((_blockStateDown.isAir() || _blockStateDown.getBlock() instanceof AbstractRailBlock) && !movedVertically) {
                pos = pos.down();
                movedVertically = true;
                depth++;
            } else {
                return ItemUtils.place(railItem, player, pos, BlockSoundGroup.METAL.getPlaceSound());
            }
        }
        // if block in way
        if (!_blockState.isAir() && !(_blockState.getBlock() instanceof AbstractRailBlock) && !movedVertically) {
            pos = pos.up();
            movedVertically = true;
            depth++;
        }
        // if rails and on same axis
        if (_blockState.getBlock() instanceof AbstractRailBlock && isXAxis(getRailShape(_blockState)) == xAxisRails) {
            pos = pos.add(vec);
            movedVertically = false;
        }
        return forward(world, player, vec, railItem, pos, xAxisRails, depth - 1, movedVertically);
    }

    private static boolean isXAxis(RailShape shape) {
        return (shape == RailShape.EAST_WEST) || (shape == RailShape.ASCENDING_EAST) || (shape == RailShape.ASCENDING_WEST);
    }

    private static RailShape getRailShape(BlockState state) {
        return state
            .getOrEmpty(Properties.STRAIGHT_RAIL_SHAPE)
            .orElseGet(() -> state.get(Properties.RAIL_SHAPE));
    }

}
