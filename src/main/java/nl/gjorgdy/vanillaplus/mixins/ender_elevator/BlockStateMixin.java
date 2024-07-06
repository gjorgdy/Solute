package nl.gjorgdy.vanillaplus.mixins.ender_elevator;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.EnderElevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateMixin {

    @Unique
    private boolean isPowered = false;

    @Shadow
    public abstract Block getBlock();

    @Inject(method = "neighborUpdate", at = @At("RETURN"))
    public void onNeighborUpdate(World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo ci) {
        if (!EnderElevator.isElevatorBlock(sourceBlock) && EnderElevator.isElevatorBlock(getBlock())) {
            if (world.getEmittedRedstonePower(pos, Direction.NORTH) > 0) {
                if (!isPowered) {
                    new EnderElevator(world, pos).activate();
                }
                isPowered = true;
            } else {
                isPowered = false;
            }
        }
    }

}
