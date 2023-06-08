package nl.gjorgdy.vanillaplus.mixins.sign_shops;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import nl.gjorgdy.vanillaplus.functions.PlayerFunctions;
import nl.gjorgdy.vanillaplus.interfaces.SignShopInterface;
import nl.gjorgdy.vanillaplus.modules.SignShops;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin implements SignShopInterface {

    public boolean shop;
    public ItemStack product;
    public ItemStack price;

    private final SignBlockEntity sign = (SignBlockEntity) (Object) this;

    // On click on sign
    //@Inject(method = "onActivate", at=@At("HEAD"), cancellable = true)
    public void onActivate(ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        SignShopInterface signShop = (SignShopInterface) sign;
        if (signShop.vp$isShop()) {
            boolean result = false;
            // If a product has not been set yet
            if (signShop.vp$getProduct() == null) {
                ItemStack playerHand = player.getMainHandStack();
                result = SignShops.setProduct(sign, playerHand);
                // If a price has not been set yet
            } else if (signShop.vp$getPrice() == null) {
                ItemStack playerHand = player.getMainHandStack();
                result = SignShops.setPrice(sign, playerHand);
                // Shop is valid and player is sneaking
            } else if (player.isSneaking()) {
                SignShops.sendShopInformation(player, signShop);
                // Shop is valid and player not sneaking
            } else {
                // Make a purchase
                PlayerFunctions.sendError(player, "Purchase");
                result = SignShops.makePurchase(sign, player);
            }
            // Update the stock counter of the shop
            SignShops.updateStock(sign);
            // Return result so click animation plays if true
            cir.setReturnValue(result);
            // Cancel event
            cir.cancel();

        }
    }

    // Save shop data
    @Inject(method = "writeNbt", at=@At("HEAD"))
    public void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        // Store if the sign is a shop
        nbt.putBoolean("shop", vp$isShop());
        // If it's a shop, store more
        if (vp$isShop()) {
            // Store the product
            NbtCompound productNBT = new NbtCompound();
            ItemStack product = vp$getProduct();
            product = product == null ? ItemStack.EMPTY : product;
            product.writeNbt(productNBT);
            nbt.put("product", productNBT);
            // Store the price
            NbtCompound priceNBT = new NbtCompound();
            ItemStack price = vp$getPrice();
            price = price == null ? ItemStack.EMPTY : price;
            price.writeNbt(priceNBT);
            nbt.put("price", priceNBT);
        }
    }

    // read shop data
    @Inject(method = "readNbt", at=@At("HEAD"))
    public void readNBT(NbtCompound nbt, CallbackInfo ci) {
        // Read if the sign is a shop
        if (nbt.getBoolean("shop")) {
            // Store that sign is shop
            vp$setShop();
            // Read product
            ItemStack product = ItemStack.fromNbt(nbt.getCompound("product"));
            // Read price
            ItemStack price = ItemStack.fromNbt(nbt.getCompound("price"));
            // Set to signShop object if valid
            //  remove shop if isn't
            if (price.isEmpty() || product.isEmpty()) {
                SignShops.removeShop(sign);
            } else {
                vp$setPrice(price);
                vp$setProduct(product);
            }
        }
    }

    // Boolean check if the sign is a shop
    @Override
    public void vp$setShop() {
        shop = true;
    }
    @Override
    public void vp$setShop(boolean val) {
        shop = val;
    }
    @Override
    public boolean vp$isShop() {
        return shop;
    }

    // ItemStack product
    @Override
    public void vp$setProduct(ItemStack itemStack) {
        if (product == null) {
            product = itemStack;
        }
    }
    @Override
    public ItemStack vp$getProduct() {
        return product == null ? null : product.copy();
    }

    // ItemStack price per one instance of the ItemStack product
    @Override
    public void vp$setPrice(ItemStack itemStack) {
        VanillaPlus.LOGGER.info("Set ");
        if (price == null) {
            price = itemStack;
        }
    }
    @Override
    public ItemStack vp$getPrice() {
        return price == null ? null : price.copy();
    }

}
