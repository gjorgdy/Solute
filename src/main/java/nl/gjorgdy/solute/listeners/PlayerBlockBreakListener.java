package nl.gjorgdy.solute.listeners;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Enchantment;
import nl.gjorgdy.solute.modules.Ladder;
import org.jetbrains.annotations.Nullable;

public class PlayerBlockBreakListener implements PlayerBlockBreakEvents.Before {

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        // Ladders
        if (world.getBlockState(pos).isOf(Blocks.LADDER))
            Ladder.updateLadder(world, pos);
        // Enchantments
        ItemStack playerTool = player.getMainHandStack();
        int excavation = EnchantmentHelper.getLevel(Solute.ENCHANTMENTS.EXCAVATION, playerTool);
        if (excavation > 0) Enchantment.excavate(world, player, pos, state);
        return true;
    }

}
