package nl.gjorgdy.solute.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import nl.gjorgdy.solute.Solute;

public class Bars {

    public static double getTargetVelocity() {
        return Solute.CONFIG.ironBarsModule.targetVelocity;
    }

    public static double getVelocityModifier() {
        return Solute.CONFIG.ironBarsModule.velocityModifier;
    }

    public static void tick(PlayerEntity player) {
        if (!player.isSpectator() && !player.isOnGround() && player.getVelocity().getY() < 0 && !player.isSneaking() && isPole(player)) {
            Vec3d v = player.getVelocity();
            double newVerticalVelocity = v.y >= getTargetVelocity() ? v.y : v.y * getVelocityModifier();
            var newVelocity = new Vec3d(v.x, newVerticalVelocity, v.z);
            if (player instanceof ServerPlayerEntity serverPlayer)
                setVelocity(serverPlayer, newVelocity);
            if (newVerticalVelocity >= getTargetVelocity()) {
                player.fallDistance = 0;
            }
        }
    }

    public static boolean jump(PlayerEntity player) {
        if (!player.isSpectator() && !player.isOnGround() && closeToTargetVelocity(player.getVelocity()) && !player.isSneaking() && isPole(player)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                var newVelocity = serverPlayer.getVelocity()
                                          .add(Vec3d.fromPolar(0, serverPlayer.getYaw()).multiply(0.3))
                                          .add(0, 0.5, 0);
                setVelocity(serverPlayer, newVelocity);
                return false;
            }
        }
        return true;
    }

    private static boolean closeToTargetVelocity(Vec3d velocity) {
        return Math.abs(velocity.getY() - getTargetVelocity()) < 0.25;
    }

    private static void setVelocity(ServerPlayerEntity player, Vec3d velocity) {
        player.setVelocity(velocity);
        player.networkHandler.send(new EntityVelocityUpdateS2CPacket(player.getId(), velocity), null);
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
