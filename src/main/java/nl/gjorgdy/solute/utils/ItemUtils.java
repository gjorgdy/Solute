package nl.gjorgdy.solute.utils;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import nl.gjorgdy.solute.interfaces.ConcretePowderBlockInterface;

public class ItemUtils {

    public static ItemStack hardenConcretePowder(final ItemStack stack) {
        if (Block.getBlockFromItem(stack.getItem()) instanceof ConcretePowderBlockInterface concretePowderBlockInterface) {
            return concretePowderBlockInterface.solute$getHardenedStateDefaultItemStack();
        }
        return stack;
    }

    public static boolean canBecomeMud(final ItemStack stack) {
        return stack.isOf(Items.DIRT) || stack.isOf(Items.COARSE_DIRT)  || stack.isOf(Items.ROOTED_DIRT);
    }

}
