package nl.gjorgdy.solute.listeners;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.solute.modules.Ladder;
import org.jetbrains.annotations.Nullable;

public class PlayerBlockBreakListener implements PlayerBlockBreakEvents.Before{

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (world.getBlockState(pos).isOf(Blocks.LADDER))
            Ladder.updateLadder(world, pos);
        return true;
    }
}
