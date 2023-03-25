package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.item.ItemStack;

public class ItemFunctions {

    /**
     * Check if stacks can be merged
     * @param first to be added to
     * @param second to be taken from
     * @return if the 2 stacks can be merged
     */
    public static boolean canMergeStacks(ItemStack first, ItemStack second) {
        return first.isOf(second.getItem())
                && ItemStack.areNbtEqual(first, second)
                && first.getDamage() == second.getDamage()
                && first.getMaxCount() > 1
                && second.getMaxCount() > 1;
    }

    /**
     * Calculate how much space a slot has left for an itemStack
     * @param first item in slot
     * @param second item to be added
     * @return amount of space
     */
    public static int getSpace(ItemStack first, ItemStack second) {
        if (first.isEmpty()) {
            return second.getMaxCount();
        } else if (canMergeStacks(first, second)) {
            return first.getMaxCount() - first.getCount();
        } else {
            return 0;
        }
    }

    /**
     * Adds the second stack to the first stack if possible
     * @param first the itemStack to add to
     * @param second the itemStack to be added
     * @return if the second stack has been completely merged
     */
    public static boolean mergeStacks(ItemStack first, ItemStack second) {
        if (canMergeStacks(first, second)) {
            // Get amount of items to be merged (Amount of space left in first stack, or count of second stack
            int mergeCount = Math.min(first.getMaxCount() - first.getCount(), second.getCount());
            // Add the amount merged to first stack
            first.increment(mergeCount);
            // Remove the amount merged from second stack
            second.decrement(mergeCount);
        }
        return second.getCount() == 0;
    }

}
