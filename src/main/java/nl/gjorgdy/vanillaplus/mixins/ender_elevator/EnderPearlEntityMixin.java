package nl.gjorgdy.vanillaplus.mixins.ender_elevator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderPearlEntity.class)
public class EnderPearlEntityMixin {

    EnderPearlEntity pearlEntity = (EnderPearlEntity) (Object) this;

    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    public float finishUsing(float amount) {
        if (pearlEntity.world.getRegistryKey() == World.END) {
            return 0;
        } else {
            return amount;
        }
    }

    @Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;requestTeleportAndDismount(DDD)V"))
    public void teleportVehicle(ServerPlayerEntity instance, double destX, double destY, double destZ) {
        Entity vehicle = instance.getVehicle();
        if (vehicle instanceof LivingEntity) {
            vehicle.requestTeleport(destX, destY, destZ);
            if (vehicle.getWorld().getRegistryKey() != World.END && !instance.isCreative()) {
                vehicle.damage(pearlEntity.getDamageSources().fall(), 2.5F);
            }
        } else {
            instance.requestTeleportAndDismount(destX, destY, destZ);
        }
    }

}
