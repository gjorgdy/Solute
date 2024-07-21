package nl.gjorgdy.solute.interfaces;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ConcretePowderBlockInterface {

    Block solute$getHardenedState();
    Item solute$getHardenedStateItem();
    ItemStack solute$getHardenedStateDefaultItemStack();

}
