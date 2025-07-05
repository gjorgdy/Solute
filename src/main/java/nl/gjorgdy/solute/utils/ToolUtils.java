package nl.gjorgdy.solute.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

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

}
