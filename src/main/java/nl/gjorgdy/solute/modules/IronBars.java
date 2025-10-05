package nl.gjorgdy.solute.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class IronBars {

    public static final double TARGET_VELOCITY = -0.5;
    public static final double VELOCITY_MODIFIER = 0.85;

    public static void tick(PlayerEntity player) {
        if (!player.isSpectator() && !player.isOnGround() && player.getVelocity().getY() < 0 && !player.isSneaking() && isPole(player)) {
            Vec3d v = player.getVelocity();
            double newVerticalVelocity = v.y >= TARGET_VELOCITY ? v.y : v.y * VELOCITY_MODIFIER;
            player.setVelocity(
                    v.x,
                    newVerticalVelocity,
                    v.z
            );
            player.velocityModified = true;
            if (newVerticalVelocity >= TARGET_VELOCITY) {
                player.fallDistance = 0;
            }
        }
    }

    private static boolean isPole(PlayerEntity player) {
        BlockState block = player.getEntityWorld().getBlockState(player.getBlockPos());
        return block.isOf(Blocks.END_ROD)
                || (block.isOf(Blocks.IRON_BARS)
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.NORTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.EAST, SideShapeType.CENTER)
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.SOUTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.WEST, SideShapeType.CENTER));
    }

}
