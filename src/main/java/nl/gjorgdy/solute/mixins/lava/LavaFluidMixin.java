package nl.gjorgdy.solute.mixins.lava;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Cobblestone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {

    // Replace stone block generation
    @ModifyArgs(method = "flow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0))
    private void generateStone(Args args, WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        // early return if module disabled
        if (!Solute.CONFIG.lavaModule.enabled) return;
        // early return if module disabled
        args.set(1, Cobblestone.replaceStone((World) world, pos));
    }

}
