package nl.gjorgdy.solute.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Bars {

    public static final double TARGET_VELOCITY = -0.75;
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
            player.velocityDirty = true;
            if (newVerticalVelocity >= TARGET_VELOCITY) {
                player.fallDistance = 0;
            }
        }
    }

    public static void jump(PlayerEntity player) {
        if (!player.isSpectator() && !player.isOnGround() && player.getVelocity().getY() < 0 && !player.isSneaking() && isPole(player)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                var direction = serverPlayer.getHorizontalFacing();
                serverPlayer.setVelocity(
                    serverPlayer.getVelocity().add(
                        direction.getDoubleVector().multiply(0.3)
                    ).add(0, 0.5, 0)
                );
            }
        }
    }

    private static boolean isPole(PlayerEntity player) {
        BlockState block = player.getEntityWorld().getBlockState(player.getBlockPos());
        return block.isOf(Blocks.END_ROD)
                || (block.isOf(Blocks.IRON_BARS)
                || Blocks.COPPER_BARS.getAll().contains(block.getBlock())
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.NORTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.EAST, SideShapeType.CENTER)
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.SOUTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getEntityWorld(), player.getBlockPos(), Direction.WEST, SideShapeType.CENTER));
    }

}
