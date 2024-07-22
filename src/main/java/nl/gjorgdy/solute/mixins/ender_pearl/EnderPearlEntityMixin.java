package nl.gjorgdy.solute.mixins.ender_pearl;

import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import nl.gjorgdy.solute.interfaces.EnderPearlEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Inject(method = "tick", at = @At("TAIL"))
    void onTick(CallbackInfo ci) {
        if (solute$getTimeToLive(true) <= 0 && pearlEntity.getOwner() != null) {
            solute$setTimeToLive();
            onCollision(new EntityHitResult(pearlEntity.getOwner()));
        } else if (pearlEntity.getOwner() == null) {
            pearlEntity.discard();
        }
    }

    @Override
    public void solute$setTimeToLive() {
        this.timeToLive = TIME_TO_LIVE_DEFAULT;
    }

    @Override
    public void solute$setTimeToLive(int value) {
        this.timeToLive = value;
    }

    @Override
    public int solute$getTimeToLive(boolean decrement) {
        if (decrement) {
            --timeToLive;
        }
        return this.timeToLive;
    }

}
