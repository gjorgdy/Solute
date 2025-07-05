package nl.gjorgdy.solute.mixins.ladder;

import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Ladder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LadderBlock.class)
public class LadderBlockMixin {

    @Inject(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDefaultState()Lnet/minecraft/block/BlockState;"), cancellable = true)
    public void update(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random, CallbackInfoReturnable<BlockState> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.ladderModule.enabled) return;
        // early return if module disabled
        if (Ladder.isSupported(world, pos))
            cir.setReturnValue(state);
        else
            Ladder.updateLadder(world, pos);
    }

    @Inject(method = "canPlaceAt", at = @At("RETURN"), cancellable = true)
    public void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.ladderModule.enabled) return;
        // early return if module disabled
        if (!cir.getReturnValue()) {
            cir.setReturnValue(Ladder.canBeSupported(state, world, pos));
        }
    }

}
