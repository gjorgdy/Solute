package nl.gjorgdy.solute.listeners;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Ladder;
import org.jetbrains.annotations.Nullable;

public class PlayerBlockBreakListener implements PlayerBlockBreakEvents.After {

    @Override
    public void afterBlockBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
        // Ladders
        if (Solute.CONFIG.ladderModule.enabled && blockState.isOf(Blocks.LADDER)) {
            // update ladders this ladder might have been supporting
            Ladder.updateLadder(world, blockPos.down());
        }
    }
}
