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

    private static final String ITEM_INDEX = "vp$item_map";
    private static final String CUSTOM_INDEX = "vp$custom_item";
    private static final MutableText NAME = Text.translatable("entity.minecraft.item")
            .append(Text.literal(" "))
            .append(Text.translatable("item.minecraft.filled_map"))
            .setStyle(Style.EMPTY);

    public static boolean compareItem(Inventory inventory, ItemStack inputStack) {
        // If item is a filter, immediately return false, as they can't move
        if (isItemMap(inputStack)) {
            return false;
        }
        boolean containsItemMap = false;
        for (int i = 0; i < inventory.size(); i++) {
            // Get itemStack
            ItemStack _item = inventory.getStack(i);
            // If looped item is an item map
            if (isItemMap(_item)) {
                // If inventory contains item map, deny all other items
                containsItemMap = true;
                if (_item.getNbt().getString(ITEM_INDEX).equals(inputStack.getTranslationKey())) {
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

    public static ItemStack createItemMap(Item item) {
    // Create display attributes for stack
        // Create name Text
        MutableText nameText = NAME.setStyle(Style.EMPTY.withItalic(false));
        // Create lore list
        List<MutableText> loreList = new ArrayList<>();
        loreList.add(Text.translatable(item.getTranslationKey()));
        // If item has tooltip, add it
        if (item instanceof MusicDiscItem | item instanceof BannerPatternItem | item instanceof GoatHornItem) {
            loreList.add(Text.translatable(item.getTranslationKey() + ".desc"));
        }
    // Create a new paper item
        ItemStack itemMapStack = Items.PAPER.getDefaultStack();
        ItemFunctions.setDisplay(itemMapStack, nameText, loreList, Formatting.GRAY);
        // Add functional NBT data
        itemMapStack.setSubNbt(CUSTOM_INDEX, NbtByte.of(true));
        itemMapStack.setSubNbt(ITEM_INDEX, NbtString.of(item.getTranslationKey()));
        // Return paper item
        return itemMapStack;
    }

}
