package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerEntity;
import nl.gjorgdy.vanillaplus.modules.EnderElevator;
import nl.gjorgdy.vanillaplus.modules.BracingBars;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Unique
    private final PlayerEntity player = (PlayerEntity) (Object) this;

    /**
     * Run elevator upwards when player jumps
     * @param ci -
     */
    @Inject(at = @At("HEAD"), method = "jump")
    private void onJump(CallbackInfo ci) {
        EnderElevator.onJump(player);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        BracingBars.tick(player);
    }




}
