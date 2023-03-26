package nl.gjorgdy.vanillaplus.mixins.sign_shops;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import nl.gjorgdy.vanillaplus.functions.BlockFunctions;
import nl.gjorgdy.vanillaplus.functions.PlayerFunctions;
import nl.gjorgdy.vanillaplus.interfaces.SignBlockEntityInterface;
import nl.gjorgdy.vanillaplus.modules.SignShops;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin implements SignBlockEntityInterface {

    @Shadow public abstract boolean isGlowingText();

    public boolean shop;
    public ItemStack product;
    public ItemStack price;

    private final SignBlockEntity sign = (SignBlockEntity) (Object) this;

    // On creation of sign
    @Inject(method = "setTextOnRow(ILnet/minecraft/text/Text;Lnet/minecraft/text/Text;)V", at=@At("HEAD"))
    public void setTextOnRow(int row, Text text, Text filteredText, CallbackInfo ci) {
        // After last line is set, check the sign
        if (row == 3
                && (sign.getTextOnRow(0, false).equals(Text.literal("[shop]"))
                || sign.getTextOnRow(0, false).equals(Text.literal("[trade]")))
        ) {
            // Create a shop for this sign is possible
            SignShops.create(sign);
        }
    }

    // On click on sign
    @Inject(method = "onActivate", at=@At("HEAD"), cancellable = true)
    public void onActivate(ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        SignBlockEntityInterface shopSign = (SignBlockEntityInterface) sign;
        if (shopSign.vanillaPlus$isShop()) {
            boolean result = false;
            // If a product has not been set yet
            if (shopSign.vanillaPlus$getProduct() == null) {
                ItemStack playerHand = player.getMainHandStack();
                result = SignShops.setProduct(sign, playerHand);
            // If a price has not been set yet
            } else if (shopSign.vanillaPlus$getPrice() == null) {
                ItemStack playerHand = player.getMainHandStack();
                result = SignShops.setPrice(sign, playerHand);
            // Shop is valid
            } else {
                // Make a purchase
                PlayerFunctions.sendError(player, "Purchase");
                result = SignShops.makePurchase(sign, player);
            }
            // Update the stock counter of the shop
            SignShops.updateStock(sign);
            // Update sign block for players
            BlockFunctions.updateBlock(sign.getWorld(), sign.getPos(), true);
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
        nbt.putBoolean("shop", vanillaPlus$isShop());
        // If it's a shop, store more
        if (vanillaPlus$isShop()) {
            // Store the product
            NbtCompound productNBT = new NbtCompound();
            ItemStack product = vanillaPlus$getProduct();
            product = product == null ? ItemStack.EMPTY : product;
            product.writeNbt(productNBT);
            nbt.put("product", productNBT);
            // Store the price
            NbtCompound priceNBT = new NbtCompound();
            ItemStack price = vanillaPlus$getPrice();
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
            vanillaPlus$setShop();
            // Read product
            ItemStack product = ItemStack.fromNbt(nbt.getCompound("product"));
            product = product.isEmpty() ? null : product;
            vanillaPlus$setProduct(product);
            // Read price
            ItemStack price = ItemStack.fromNbt(nbt.getCompound("price"));
            price = price.isEmpty() ? null : price;
            vanillaPlus$setPrice(price);
        }
    }

    // Boolean check if the sign is a shop
    @Override
    public void vanillaPlus$setShop() {
        shop = true;
    }
    @Override
    public boolean vanillaPlus$isShop() {
        return shop;
    }

    // ItemStack product
    @Override
    public void vanillaPlus$setProduct(ItemStack itemStack) {
        if (product == null) {
            product = itemStack;
        }
    }
    @Override
    public ItemStack vanillaPlus$getProduct() {
        return product == null ? null : product.copy();
    }

    // ItemStack price per one instance of the ItemStack product
    @Override
    public void vanillaPlus$setPrice(ItemStack itemStack) {
        VanillaPlus.LOGGER.info("Set ");
        if (price == null) {
            price = itemStack;
        }
    }
    @Override
    public ItemStack vanillaPlus$getPrice() {
        return price == null ? null : price.copy();
    }

}
