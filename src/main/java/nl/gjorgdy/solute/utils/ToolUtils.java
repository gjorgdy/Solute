package nl.gjorgdy.solute.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntryList;

import java.util.ArrayList;
import java.util.List;

public class ToolUtils {

    public static boolean isPickaxe(ItemStack item) {
        return
            item.isOf(Items.WOODEN_PICKAXE)
            || item.isOf(Items.STONE_PICKAXE)
            || item.isOf(Items.IRON_PICKAXE)
            || item.isOf(Items.GOLDEN_PICKAXE)
            || item.isOf(Items.DIAMOND_PICKAXE)
            || item.isOf(Items.NETHERITE_PICKAXE);
    }

    public static class ToolHandler {

        private final List<RegistryEntryList<Block>> blockList = new ArrayList<>();
        private boolean correctForDrops;

        public ToolHandler(ItemStack item) {
            var tool = item.get(DataComponentTypes.TOOL);
            if (tool == null) return;
            var rules = tool.rules();
            for (var rule : rules) {
                if (rule.correctForDrops().isPresent()) {
                    blockList.add(rule.blocks());
                    correctForDrops = rule.correctForDrops().get();
                }
            }
        }

        public boolean test(BlockState state) {
            return blockList.stream().anyMatch(state::isIn) && correctForDrops;
        }

    }

}
