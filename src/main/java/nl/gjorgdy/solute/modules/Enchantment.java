package nl.gjorgdy.solute.modules;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import nl.gjorgdy.solute.utils.ToolUtils;

import java.util.function.Consumer;

public class Enchantment {

    public static void excavate(World world, PlayerEntity player, BlockPos pos, BlockState blockState) {
        var toolHandler = new ToolUtils.ToolHandler(player.getMainHandStack());
        if (!toolHandler.test(blockState)) return;
        HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hitResult;
            float hardnessRef = blockState.getBlock().getHardness();
            ForAxis(bhr.getSide(), pos, (_pos) -> {
                BlockState _blockState = world.getBlockState(_pos);
                float hardness = _blockState.getBlock().getHardness();
                if (Math.abs(hardnessRef - hardness) < 0.5f && toolHandler.test(_blockState)) {
                    world.breakBlock(_pos, !player.isCreative(), player);
                }
            });
        }
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
