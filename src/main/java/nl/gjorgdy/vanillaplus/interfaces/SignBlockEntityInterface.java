package nl.gjorgdy.vanillaplus.interfaces;

import net.minecraft.item.ItemStack;

public interface SignBlockEntityInterface {

    default void setShop() {}
    default boolean isShop() {
        return false;
    }

    default void setProduct(ItemStack itemStack) {}
    default ItemStack getProduct() {
        return ItemStack.EMPTY;
    }

    default void setPrice(ItemStack itemStack) {}
    default ItemStack getPrice() {
        return ItemStack.EMPTY;
    }

}
