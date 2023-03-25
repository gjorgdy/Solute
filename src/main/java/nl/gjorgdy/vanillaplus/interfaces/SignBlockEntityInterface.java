package nl.gjorgdy.vanillaplus.interfaces;

import net.minecraft.item.ItemStack;

public interface SignBlockEntityInterface {

    default void vanillaPlus$setShop() {}
    default boolean vanillaPlus$isShop() {
        return false;
    }

    default void vanillaPlus$setProduct(ItemStack itemStack) {}
    default ItemStack vanillaPlus$getProduct() {
        return ItemStack.EMPTY;
    }

    default void vanillaPlus$setPrice(ItemStack itemStack) {}
    default ItemStack vanillaPlus$getPrice() {
        return ItemStack.EMPTY;
    }

}
