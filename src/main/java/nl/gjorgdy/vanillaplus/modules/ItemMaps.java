package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nl.gjorgdy.vanillaplus.functions.NbtText;

public class ItemMaps {

    private static final String nbtIndex = "vp_filter";
    private static final MutableText name = Text.translatable("entity.minecraft.item")
            .append(Text.literal(" "))
            .append(Text.translatable("item.minecraft.filled_map"))
            .setStyle(Style.EMPTY);

    public static boolean compareItem(Inventory inventory, ItemStack itemStack) {
        // If item is a filter, immediately return false, as they can't move
        if (isItemMap(itemStack)) {
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
                if (_item.getNbt().getString(nbtIndex).equals(itemStack.getTranslationKey())) {
                    return true;
                }
            }
        }
        return !containsItemMap;
    }

    // Return if the ItemStack is a filter item
    public static boolean isItemMap(ItemStack stack) {
        if (stack.hasNbt()) {
            return stack.getNbt().contains(nbtIndex);
        } else {
            return false;
        }
    }

    public static ItemStack createItemMap(Item item) {
        // Create a new paper item
        ItemStack filter = Items.PAPER.getDefaultStack();
        // Create a new nbt compound for the display tags
        NbtCompound nbtDisplay = new NbtCompound();
        // Lore list
        NbtList nbtLore = new NbtList();
        nbtLore.add(
                NbtText.of(Text.translatable(item.getTranslationKey()), Formatting.GRAY)
        );
        // If item has tooltip, add it
        if (item instanceof MusicDiscItem | item instanceof BannerPatternItem | item instanceof GoatHornItem) {
            nbtLore.add(
                    NbtText.of(Text.translatable(item.getTranslationKey() + ".desc"), Formatting.GRAY)
            );
        }
        // Add elements to display nbt
        nbtDisplay.put("Lore", nbtLore);
        nbtDisplay.put("Name",
            NbtString.of(
                Text.Serializer.toJson(name.setStyle(Style.EMPTY.withItalic(false)))
            )
        );
        // General nbt compound
        NbtCompound nbt = new NbtCompound();
        nbt.put("display", nbtDisplay);
        nbt.putBoolean("no_ingredient", true);
        // Add an inventory filter
        nbt.putString(nbtIndex, item.getTranslationKey());
        // Add NBT to paper item
        filter.setNbt(nbt);
        // Return paper item
        return filter;
    }

}
