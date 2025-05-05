package nl.gjorgdy.solute.mixins.purpur;

import net.minecraft.entity.Entity;
import nl.gjorgdy.solute.modules.Purpur;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Unique
    private Entity entity = (Entity) (Object) this;

    @Inject(method = "setSneaking", at = @At("HEAD"))
    public void setSneaking(boolean sneaking, CallbackInfo ci) {
        Purpur.down(entity);
    }

}
