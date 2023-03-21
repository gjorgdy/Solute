package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.CraftingScreenHandler;
import nl.gjorgdy.vanillaplus.modules.ItemMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

    // On getting the result of a crafting recipe, check if it's of the type "item_map"
    //  if it is, return an item map based on the item in the middle of the crafting grid
    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/CraftingRecipe;craft(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getRecipe(CraftingRecipe recipe, Inventory inventory, DynamicRegistryManager dynamicRegistryManager) {
        if (!hasValidIngredients((CraftingInventory) inventory)) {
          return ItemStack.EMPTY;
        } else if (recipe.getId().getPath().equals("item_map")) {
            return ItemMaps.createItemMap(inventory.getStack(4).getItem());
        } else {
            return recipe.craft((CraftingInventory) inventory, dynamicRegistryManager);
        }
    }

    // Check if all items in the crafting grid can be used as ingredients
    private static boolean hasValidIngredients(CraftingInventory inventory) {
        for (int i=0; i<inventory.size(); i++) {
            ItemStack _item = inventory.getStack(i);
            if (_item.hasNbt()) {
                if (_item.getNbt().contains("no_ingredient")) {
                    return false;
                }
            }
        }
        return true;
    }

    //@ModifyVariable(method = "updateResult", at= @At(value = "STORE"), ordinal = 1)
    //private static ItemStack updateResult(ItemStack value) {
    //    if (value.getItem() == Items.KNOWLEDGE_BOOK) {
    //        return InventoryFilter.createFilter(Items.APPLE);
    //    } else {
    //        return value;
    //    }
    //}

}
