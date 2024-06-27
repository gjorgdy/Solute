package nl.gjorgdy.vanillaplus.mixins.stop_stasis;

import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.nbt.NbtCompound;
import nl.gjorgdy.vanillaplus.interfaces.EnderPearlEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownItemEntity.class)
public class ThrownItemEntityMixin {

    @Unique
    ThrownItemEntity thrownItemEntity = (ThrownItemEntity) (Object) this;

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if (thrownItemEntity instanceof EnderPearlEntityInterface) {
            nbt.putInt("vp$timeToLive", ((EnderPearlEntityInterface) thrownItemEntity).vanillaPlus$getTimeToLive(false) );
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    void readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (thrownItemEntity instanceof EnderPearlEntityInterface) {
            ((EnderPearlEntityInterface) thrownItemEntity).vanillaPlus$setTimeToLive(nbt.getInt("vp$timeToLive"));
            nbt.remove("vp$timeToLive");
        }
    }

}
