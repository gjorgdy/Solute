package nl.gjorgdy.vanillaplus.functions;

import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import nl.gjorgdy.vanillaplus.VanillaPlus;

public class Treasures {

    static int count = 1;

    public static void checkTreasure(ItemStack itemStack) {
        if (isTreasure(itemStack.getItem())) {
            NbtCompound nbt = itemStack.getOrCreateNbt();
            if (!nbt.contains("vp_treasure")) {
                identifyTreasure(itemStack);
            }
        }
    }

    synchronized static void identifyTreasure(ItemStack itemStack) {
        VanillaPlus.LOGGER.info("Found Treasure " + itemStack.getItem().getName() + " with rarity " + itemStack.getRarity());

        // Give item nbt
        NbtCompound nbtDisplay = new NbtCompound();
        NbtList nbtLore = new NbtList();
        nbtLore.add(NbtText.of(Text.literal("#" + count), Formatting.GRAY));
        // Add elements to display nbt
        nbtDisplay.put("Lore", nbtLore);
        // Add display element to item
        itemStack.setSubNbt("display", nbtDisplay);
        itemStack.setSubNbt("vp_treasure", NbtInt.of(count));

        count += 1;
    }

    public static boolean isTreasure(Item item) {
        return item instanceof ElytraItem
            || item instanceof MusicDiscItem
            || item instanceof HorseArmorItem
            || item instanceof GoatHornItem
            || item instanceof TridentItem;
    }

}
