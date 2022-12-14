package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerEntity;
import nl.gjorgdy.vanillaplus.callbacks.PlayerJumpCallback;
import nl.gjorgdy.vanillaplus.callbacks.PlayerSneakCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    private boolean releasedSneak = true;
    private PlayerEntity player = (PlayerEntity) (Object) this;

    @Inject(at = @At("HEAD"), method = "jump")
    private void onJump(CallbackInfo ci) {
        PlayerJumpCallback.EVENT.invoker().interactJump((PlayerEntity) (Object) this);
    }

    /**
     * Run elevator downwards when player sneaks
     * @param ci
     */
    @Inject(at = @At("TAIL"), method = "tick")
    private void onSneak(CallbackInfo ci) {
        if (player.isSneaking() && releasedSneak) {
            releasedSneak = false;
            PlayerSneakCallback.EVENT.invoker().interactJump((PlayerEntity) (Object) this);
        } else {
            releasedSneak = true;
        }
    }

}
