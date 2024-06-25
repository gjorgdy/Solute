package nl.gjorgdy.vanillaplus.mixins.ender_elevator;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.EnderElevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateMixin {

    @Unique
    private boolean isPowered = false;

    @Shadow
    public abstract boolean isOf(Block block);

    @Shadow protected abstract BlockState asBlockState();

    @Shadow public abstract Block getBlock();

    @Inject(method = "neighborUpdate", at = @At("HEAD"))
    public void onNeighborUpdate(World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify, CallbackInfo ci) {
        if (EnderElevator.isElevatorBlock(getBlock())) {
            if (world.getEmittedRedstonePower(pos, Direction.NORTH) > 0) {
                if (!isPowered) {
                    new EnderElevator(world, pos).start();
                }
                isPowered = true;
            } else {
                isPowered = false;
            }
        }
    }

    @Unique
    private AbstractBlock.AbstractBlockState getSelf() {
        return (AbstractBlock.AbstractBlockState) (Object) this;
    }

}
