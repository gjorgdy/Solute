package nl.gjorgdy.solute.mixins.ender_pearl;

import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.nbt.NbtCompound;
import nl.gjorgdy.solute.interfaces.EnderPearlEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ThrownItemEntity.class)
public class ThrownItemEntityMixin {

    @Unique
    ThrownItemEntity thrownItemEntity = (ThrownItemEntity) (Object) this;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if (thrownItemEntity instanceof EnderPearlEntityInterface) {
            nbt.putInt("vp$timeToLive", ((EnderPearlEntityInterface) thrownItemEntity).solute$getTimeToLive(false) );
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (thrownItemEntity instanceof EnderPearlEntityInterface) {
            Optional<Integer> timeToLive = nbt.getInt("vp$timeToLive");
            if (timeToLive.isEmpty()) return;
            ((EnderPearlEntityInterface) thrownItemEntity).solute$setTimeToLive(timeToLive.get());
            nbt.remove("vp$timeToLive");
        }
    }

}
