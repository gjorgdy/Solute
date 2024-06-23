package nl.gjorgdy.vanillaplus.mixins.enhanced_ender;

import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.interfaces.EnderPearlEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin implements EnderPearlEntityInterface {

    @Shadow protected abstract void onCollision(HitResult hitResult);

    @Unique
    private static final int TIME_TO_LIVE_DEFAULT = 12000;
    @Unique
    private int timeToLive = TIME_TO_LIVE_DEFAULT;
    @Unique
    EnderPearlEntity pearlEntity = (EnderPearlEntity) (Object) this;

    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), index = 1)
    public float finishUsing(float amount) {
        if (pearlEntity.getWorld().getRegistryKey() == World.END) {
            return 0;
        } else {
            return amount;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    void onTick(CallbackInfo ci) {
        if (vanillaPlus$getTimeToLive(true) <= 0 && pearlEntity.getOwner() != null) {
            vanillaPlus$setTimeToLive();
            onCollision(new EntityHitResult(pearlEntity.getOwner()));
        } else if (pearlEntity.getOwner() == null) {
            pearlEntity.discard();
        }
    }

    @Override
    public void vanillaPlus$setTimeToLive() {
        this.timeToLive = TIME_TO_LIVE_DEFAULT;
    }

    @Override
    public void vanillaPlus$setTimeToLive(int value) {
        this.timeToLive = value;
    }

    @Override
    public int vanillaPlus$getTimeToLive(boolean decrement) {
        if (decrement) {
            --timeToLive;
        }
        return this.timeToLive;
    }

}
