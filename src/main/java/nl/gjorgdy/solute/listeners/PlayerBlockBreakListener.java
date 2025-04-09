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

public class PlayerBlockBreakListener implements PlayerBlockBreakEvents.Before, PlayerBlockBreakEvents.After {

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        // Ladders
        if (world.getBlockState(pos).isOf(Blocks.LADDER))
            Ladder.updateLadder(world, pos);
        // Enchantments
        ItemStack playerTool = player.getMainHandStack();
        // Excavation
        int excavation = EnchantmentHelper.getLevel(Solute.ENCHANTMENTS.EXCAVATION, playerTool);
        if (excavation > 0) Enchantment.excavate(world, player, pos, state);
        // Drilling
        int drilling = EnchantmentHelper.getLevel(Solute.ENCHANTMENTS.DRILLING, playerTool);
        if (drilling > 0) {
            int depth = switch (drilling) {
                case 1 -> 2;
                case 2 -> 4;
                case 3 -> 5;
                default -> 0;
            };
            Enchantment.drill(world, player, pos, state, depth);
        }
        return true;
    }

    @Override
    public void afterBlockBreak(World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {

    }
}
