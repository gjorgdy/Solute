package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockMixin {

    @Inject(method = "isSideSolidFullSquare", at = @At("HEAD"), cancellable = true)
    public void isSideSolidFullSquare(BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (world.getBlockState(pos).isOf(Blocks.SLIME_BLOCK)) {
            cir.setReturnValue(false);
        }
    }

}
