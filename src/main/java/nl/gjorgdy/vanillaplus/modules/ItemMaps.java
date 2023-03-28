package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nl.gjorgdy.vanillaplus.functions.ItemFunctions;

import java.util.ArrayList;
import java.util.List;

public class ItemMaps {

    public static final String ITEM_INDEX = "vp$item_map";
    public static final String CUSTOM_INDEX = "vp$custom_item";
    private static final MutableText NAME = Text.translatable("entity.minecraft.item")
            .append(Text.literal(" "))
            .append(Text.translatable("item.minecraft.filled_map"))
            .setStyle(Style.EMPTY);

    public static boolean checkForItemMap(Inventory inventory, ItemStack inputStack) {
        // If item is a filter, immediately return false, as they can't move
        if (isItemMap(inputStack)) {
            return false;
        }
        boolean containsItemMap = false;
        for (int i = 0; i < inventory.size(); i++) {
            // Get itemStack
            ItemStack slotStack = inventory.getStack(i);
            // If looped item is an item map
            if (isItemMap(slotStack)) {
                // If inventory contains item map, deny all other items
                containsItemMap = true;
                ItemStack mappedStack = ItemStack.fromNbt(slotStack.getNbt().getCompound(ITEM_INDEX));
                if (inputStack.isOf(mappedStack.getItem())) {
                    return true;
                }
            }
        }
        return !containsItemMap;
    }

    // Return if the ItemStack is a filter item
    public static boolean isItemMap(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(ITEM_INDEX);
    }

    public static ItemStack createItemMap(ItemStack itemStack) {
    // Create display attributes for stack
        // Create name Text
        MutableText nameText = NAME.setStyle(Style.EMPTY.withItalic(false));
        // Create lore list
        List<MutableText> loreList = new ArrayList<>();
        loreList.add(Text.translatable(itemStack.getTranslationKey()));
        // If item has tooltip, add it
        if (itemStack.getItem() instanceof MusicDiscItem || itemStack.getItem() instanceof BannerPatternItem || itemStack.getItem() instanceof GoatHornItem) {
            loreList.add(Text.translatable(itemStack.getTranslationKey() + ".desc"));
        }
    // Create a new paper item
        ItemStack itemMapStack = Items.PAPER.getDefaultStack();
        ItemFunctions.setDisplay(itemMapStack, nameText, loreList, Formatting.GRAY);
        // Assign item as a custom item
        itemMapStack.setSubNbt(CUSTOM_INDEX, NbtByte.of(true));
        // Add mapped item in nbt
        itemMapStack.setSubNbt(ITEM_INDEX, itemStack.writeNbt(new NbtCompound()));
        // Return paper item
        return itemMapStack;
    }

}
