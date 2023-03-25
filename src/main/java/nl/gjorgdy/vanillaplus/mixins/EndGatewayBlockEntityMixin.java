package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndGatewayBlockEntity.class)
public abstract class EndGatewayBlockEntityMixin {

    @Inject(method = "tryTeleportingEntity", at = @At("HEAD"), cancellable = true)
    private static void vp$tryTeleportingEntity(World world, BlockPos pos, BlockState state, Entity entity, EndGatewayBlockEntity blockEntity, CallbackInfo ci) {
        if (entity.hasVehicle()) {
            EndGatewayBlockEntity.tryTeleportingEntity(world, pos, state, entity.getVehicle(), blockEntity);
            ci.cancel();
        }
    }

}
