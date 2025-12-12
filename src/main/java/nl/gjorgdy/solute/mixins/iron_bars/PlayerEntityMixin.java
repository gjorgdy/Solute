package nl.gjorgdy.solute.mixins.iron_bars;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Bars;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Unique
    private final PlayerEntity player = (PlayerEntity) (Object) this;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo ci) {
        // early return if module disabled
        if (!Solute.CONFIG.ironBarsModule.enabled) return;
        // early return if module disabled
        Bars.tick(player);
    }

    @Override
    public void setSneaking(boolean sneaking) {
        // early return if module disabled
        if (!Solute.CONFIG.ironBarsModule.enabled) return;
        // early return if module disabled
        boolean set = true;
        if (sneaking) {
            set = Bars.jump(player);
        }
        // call original method
        if (set) {
            super.setSneaking(sneaking);
        }
    }

}
