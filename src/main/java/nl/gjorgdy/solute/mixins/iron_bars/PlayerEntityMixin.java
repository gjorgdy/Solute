package nl.gjorgdy.solute.mixins.iron_bars;

import net.minecraft.entity.player.PlayerEntity;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Bars;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Unique
    private final PlayerEntity player = (PlayerEntity) (Object) this;

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        // early return if module disabled
        if (!Solute.CONFIG.ironBarsModule.enabled) return;
        // early return if module disabled
        Bars.tick(player);
    }

}
