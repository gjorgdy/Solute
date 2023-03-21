package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import nl.gjorgdy.vanillaplus.modules.CustomGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {

    // Replace stone block generation
    @ModifyArgs(method = "flow", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0))
    private void generateStone(Args args, WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        args.set(1, CustomGenerator.replaceStone((World) world, pos));
    }

}
