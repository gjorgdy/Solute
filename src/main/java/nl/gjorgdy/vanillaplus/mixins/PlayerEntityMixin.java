package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerEntity;
import nl.gjorgdy.vanillaplus.functions.EnderElevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    private boolean releasedSneak = true;
    private final PlayerEntity player = (PlayerEntity) (Object) this;

    /**
     * Run elevator upwards when player jumps
     * @param ci
     */
    @Inject(at = @At("HEAD"), method = "jump")
    private void onJump(CallbackInfo ci) {
        EnderElevator.moveVertical(player, true);
    }

    /**
     * Run elevator downwards when player sneaks
     * @param ci
     */
    @Inject(at = @At("TAIL"), method = "tick")
    private void onSneak(CallbackInfo ci) {
        if (player.isSneaking() & releasedSneak) {
            releasedSneak = false;
            EnderElevator.moveVertical(player, false);
        } else {
            releasedSneak = true;
        }
    }

}
