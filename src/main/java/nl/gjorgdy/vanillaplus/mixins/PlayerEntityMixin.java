package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.EnderElevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    private final PlayerEntity player = (PlayerEntity) (Object) this;

    /**
     * Run elevator upwards when player jumps
     * @param ci -
     */
    @Inject(at = @At("HEAD"), method = "jump")
    private void onJump(CallbackInfo ci) {
        if (player.getWorld().getRegistryKey() != World.END) {
            EnderElevator.moveVertical(player, true);
        }
    }

    /**
     * Run elevator downwards when player sneaks
     * @param ci -
     */
    @Inject(at = @At("TAIL"), method = "tick")
    private void onSneak(CallbackInfo ci) {
        // Start sneaking
        if (player.isSneaking() && player.getWorld().getRegistryKey() != World.END) {
            EnderElevator.moveVertical(player, false);
        }
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        if (!player.isSpectator() && !player.isOnGround() && !player.isFallFlying() && isPole(player)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 2, 0, true, false, true));
        }
    }

    private static boolean isPole(PlayerEntity player) {
        BlockState block = player.getWorld().getBlockState(player.getBlockPos());
        return block.isOf(Blocks.IRON_BARS)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.NORTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.EAST, SideShapeType.CENTER)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.SOUTH, SideShapeType.CENTER)
                && !block.isSideSolid(player.getWorld(), player.getBlockPos(), Direction.WEST, SideShapeType.CENTER);
    }


}
