package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import nl.gjorgdy.vanillaplus.functions.EnderElevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Shadow public abstract void startFallFlying();

    private boolean releasedSneak = true;
    private PlayerEntity player = (PlayerEntity) (Object) this;

    /**
     * Run elevator upwards when player jumps
     * @param ci
     */
    @Inject(at = @At("TAIL"), method = "jump", cancellable = true)
    private void onJump(CallbackInfo ci) {
        EnderElevator.moveVertical(player, true);
    }

    /**
     * Run elevator downwards when player sneaks
     * @param ci
     */
    @Inject(at = @At("TAIL"), method = "tick")
    private void onSneak(CallbackInfo ci) {
        if (player.isSneaking() && releasedSneak) {
            releasedSneak = false;
            EnderElevator.moveVertical(player, false);
        } else {
            releasedSneak = true;
        }
    }

}
