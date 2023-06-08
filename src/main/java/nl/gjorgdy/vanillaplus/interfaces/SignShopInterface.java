package nl.gjorgdy.vanillaplus.interfaces;

import net.minecraft.item.ItemStack;

public interface SignShopInterface {

    default void vp$setShop() {}
    default void vp$setShop(boolean val) {}
    default boolean vp$isShop() {
        return false;
    }

    default void vp$setProduct(ItemStack itemStack) {}
    default ItemStack vp$getProduct() {
        return ItemStack.EMPTY;
    }

    default void vp$setPrice(ItemStack itemStack) {}
    default ItemStack vp$getPrice() {
        return ItemStack.EMPTY;
    }

}
