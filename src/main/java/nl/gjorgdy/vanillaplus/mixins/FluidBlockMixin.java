package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.callbacks.FluidBlockCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(FluidBlock.class)
public class FluidBlockMixin {

    //@ModifyArgs(method = "receiveNeighborFluids", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z", ordinal = 0))
    //private void generateCobble(Args args, World world, BlockPos pos, BlockState fluidBlockState) {
    //    CobblestoneGenerateCallback.EVENT.invoker().interact(world, pos);
    //}
    @Redirect(
            method = "receiveNeighborFluids",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
                    ordinal = 0
            )
    )
    private boolean generateCobble(World world, BlockPos pos, BlockState fluidBlockState) {
        FluidBlockCallback.EVENT.invoker().interact(world, pos);
        return true;
    }
}
