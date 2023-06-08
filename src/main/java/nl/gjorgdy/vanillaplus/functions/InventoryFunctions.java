package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class InventoryFunctions {

    public static boolean isSpaceForItem(Inventory inv, ItemStack input) {
        int space = 0;
        // Loop through inventory
        for (int i = inv.size()-1; i >= 0; i--) {
            space += ItemFunctions.getSpace(inv.getStack(i), input);
            if (space >= input.getCount()) {
                return true;
            }
        }
        return false;
    }

    public static void addStack(Inventory inv, ItemStack input) {
        int size = inv.size();
        // Loop through inventory
        for (int i = 0; i < size; i++) {
            // Merge stacks until the input is completely merged
            if (ItemFunctions.mergeStacks(inv.getStack(i), input)) {
                return;
            }
        }
        // Put into first empty slot
        for (int i = 0; i < size; i++) {
            // If there is an empty slot, place the itemStack in there
            if (inv.getStack(i).isEmpty()) {
                inv.setStack(i, input.copy());
                return;
            }
        }
    }

    public static List<ItemStack> takeItems(Inventory inv, ItemStack itemStack) {
        List<ItemStack> taken = new ArrayList<>();
        int amount = itemStack.getCount();
        int size = inv.size();
        // Loop through inventory
        for (int i = 0; i < size; i++) {
            ItemStack _itemStack = inv.getStack(i);
            if (ItemFunctions.areEqual(itemStack, _itemStack)) {
                // Calculate amount to be taken from slot
                //  amount left to take, or count of stack in slot
                int amountToTake = Math.min(amount, _itemStack.getCount());
                // Store a copy of the taken items in the 'taken' list
                taken.add(_itemStack.copyWithCount(amountToTake));
                // Take the amount from the slot
                _itemStack.decrement(amountToTake);
                // Contract amount of taken items from the amount var
                amount -= amountToTake;
            }
            if (amount == 0) {
                return taken;
            }
        }
        return taken;
    }

    public static Inventory get(World world, BlockPos pos) {
        return HopperBlockEntity.getInventoryAt(world, pos);
    }

}
