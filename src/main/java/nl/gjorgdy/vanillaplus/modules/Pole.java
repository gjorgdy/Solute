package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;

public class Pole {

    public static void tick(PlayerEntity player) {
        if (!player.isSpectator() && !player.isOnGround() && player.getVelocity().getY() < 0 && !player.isSneaking() && isPole(player)) {
            int duration = player.getVelocity().getY() < -2 ? 1 : 3;
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, duration, 0, true, false, true));
        }
    }

    private static boolean isPole(PlayerEntity player) {
        BlockState block = player.getWorld().getBlockState(player.getBlockPos());
        return block.isOf(Blocks.END_ROD)
                || (block.isOf(Blocks.IRON_BARS)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.NORTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.EAST, SideShapeType.CENTER)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.SOUTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.WEST, SideShapeType.CENTER));
    }

}
