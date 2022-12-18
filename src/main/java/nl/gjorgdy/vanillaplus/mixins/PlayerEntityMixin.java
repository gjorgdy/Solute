package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerEntity;
import nl.gjorgdy.vanillaplus.functions.Elevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    private boolean releasedSneak = true;
    private PlayerEntity player = (PlayerEntity) (Object) this;

    @Inject(at = @At("TAIL"), method = "jump", cancellable = true)
    private void onJump(CallbackInfo ci) {
        Elevator.moveVertical(player, true);
    }

    /**
     * Run elevator downwards when player sneaks
     * @param ci
     */
    @Inject(at = @At("TAIL"), method = "tick")
    private void onSneak(CallbackInfo ci) {
        if (player.isSneaking() && releasedSneak) {
            releasedSneak = false;
            Elevator.moveVertical(player, false);
        } else {
            releasedSneak = true;
        }
    }

}
