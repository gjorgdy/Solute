package nl.gjorgdy.solute.mixins.creeper;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import nl.gjorgdy.solute.Solute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "canExplosionDestroyBlock", at = @At("RETURN"), cancellable = true)
    public void canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower, CallbackInfoReturnable<Boolean> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.creeperModule.enabled) return;
        // early return if module disabled
        if (getSelf() instanceof CreeperEntity
                || getSelf() instanceof FireballEntity fireball && fireball.getOwner() instanceof GhastEntity
                || getSelf() instanceof EndCrystalEntity) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    public Entity getSelf() {
        return (Entity) (Object) this;
    }

}
