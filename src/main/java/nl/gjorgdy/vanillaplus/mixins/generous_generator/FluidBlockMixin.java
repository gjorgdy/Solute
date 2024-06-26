package nl.gjorgdy.vanillaplus.mixins.generous_generator;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.GenerousGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(FluidBlock.class)
public class FluidBlockMixin {

    @ModifyArgs(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    private void generateCobble(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
        args.set(1, GenerousGenerator.replaceCobblestone(world, pos, args.get(1)));
    }

}
