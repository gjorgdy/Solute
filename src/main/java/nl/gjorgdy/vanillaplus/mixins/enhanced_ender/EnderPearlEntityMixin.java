package nl.gjorgdy.vanillaplus.mixins.enhanced_ender;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.interfaces.EnderPearlEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin implements EnderPearlEntityInterface {

    @Shadow protected abstract void onCollision(HitResult hitResult);

    private static final int TIME_TO_LIVE_DEFAULT = 12000;
    private int timeToLive = TIME_TO_LIVE_DEFAULT;
    EnderPearlEntity pearlEntity = (EnderPearlEntity) (Object) this;

    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    public float finishUsing(float amount) {
        if (pearlEntity.getWorld().getRegistryKey() == World.END) {
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

    @Inject(method = "tick", at = @At("TAIL"))
    void onTick(CallbackInfo ci) {
        if (getTimeToLive(true) <= 0 && pearlEntity.getOwner() != null) {
            setTimeToLive();
            onCollision(new EntityHitResult(pearlEntity.getOwner()));
        } else if (pearlEntity.getOwner() == null) {
            pearlEntity.discard();
        }
    }

    @Override
    public void setTimeToLive() {
        this.timeToLive = TIME_TO_LIVE_DEFAULT;
    }

    @Override
    public void setTimeToLive(int value) {
        this.timeToLive = value;
    }

    @Override
    public int getTimeToLive(boolean decrement) {
        if (decrement) {
            --timeToLive;
        }
        return this.timeToLive;
    }

}
