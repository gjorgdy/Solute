package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import nl.gjorgdy.vanillaplus.modules.EnderElevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Unique
    Entity entity = (Entity) (Object) this;

    @Inject(at = @At("TAIL"), method = "setSneaking")
    private void onToggleSneak(boolean state, CallbackInfo ci) {
        // Start sneaking
        if (state && entity instanceof PlayerEntity) {
            EnderElevator.onSneak((PlayerEntity) entity);
        }
    }

}
