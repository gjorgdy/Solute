package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerEntity;
import nl.gjorgdy.vanillaplus.callbacks.PlayerEntityCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;jump()V"), method = "jump")
    private void onJump(CallbackInfo ci) {
        PlayerEntityCallback.JUMP_EVENT.invoker().interactJump((PlayerEntity) (Object) this);
    }

}
