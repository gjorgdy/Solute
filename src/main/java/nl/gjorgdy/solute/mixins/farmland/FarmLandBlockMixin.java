package nl.gjorgdy.solute.mixins.farmland;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(
    conflict = @Condition("farmtweaks")
)
@Mixin(FarmlandBlock.class)
public abstract class FarmLandBlockMixin {

    @Shadow
    public static void setToDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {}

    @Redirect(
        method = "onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;D)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V")
    )
    public void onLandedUpon(Entity entity, BlockState state, World world, BlockPos pos, @Local(argsOnly = true) double fallDistance) {
        // early return if module disabled
        if (!Solute.CONFIG.farmlandModule.enabled) return;
        // early return if module disabled
        if (fallDistance > 8.0f) setToDirt(entity, state, world, pos);
    }

}
