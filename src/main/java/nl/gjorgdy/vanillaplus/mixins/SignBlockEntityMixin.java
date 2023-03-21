package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import nl.gjorgdy.vanillaplus.interfaces.SignBlockEntityInterface;
import nl.gjorgdy.vanillaplus.modules.SignShops;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin implements SignBlockEntityInterface {

    public boolean shop;
    public ItemStack product;
    public ItemStack price;

    private final SignBlockEntity sign = (SignBlockEntity) (Object) this;

    // On creation of sign
    @Inject(method = "setTextOnRow(ILnet/minecraft/text/Text;Lnet/minecraft/text/Text;)V", at=@At("HEAD"))
    public void setTextOnRow(int row, Text text, Text filteredText, CallbackInfo ci) {
        // After last line is set, check the sign
        if (row == 3 && sign.getTextOnRow(0, false).equals(Text.literal("[shop]"))) {
            // Create a shop for this sign is possible
            SignShops.create(sign);
        }
    }

    // On click on sign
    @Inject(method = "onActivate", at=@At("HEAD"), cancellable = true)
    public void onActivate(ServerPlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        SignBlockEntityInterface shopSign = (SignBlockEntityInterface) sign;
        if (shopSign.isShop()) {
            // If a product has not been set yet
            if (shopSign.getProduct().getItem() == Items.AIR) {
                ItemStack playerHand = player.getMainHandStack();
                SignShops.setProduct(sign, playerHand);
            // If a price has not been set yet
            } else if (shopSign.getPrice().getItem() == Items.AIR) {
                ItemStack playerHand = player.getMainHandStack();
                SignShops.setPrice(sign, playerHand);
            // Shop exists
            } else {
                VanillaPlus.LOGGER.info("This should buy stuff");
                VanillaPlus.LOGGER.info("Product; " + getProduct().getName());
                VanillaPlus.LOGGER.info("Price; " + getPrice().getName());
                SignShops.makePurchase(sign, player);
            }
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    // Save shop data
    @Inject(method = "writeNbt", at=@At("HEAD"))
    public void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        // Store if the sign is a shop
        nbt.putBoolean("shop", isShop());
        // If it's a shop, store more
        if (isShop()) {
            // Store the product
            NbtCompound productNBT = new NbtCompound();
            getProduct().writeNbt(productNBT);
            nbt.put("product", productNBT);
            // Store the price
            NbtCompound priceNBT = new NbtCompound();
            getPrice().writeNbt(priceNBT);
            nbt.put("price", priceNBT);
        }
    }

    // read shop data
    @Inject(method = "readNbt", at=@At("HEAD"))
    public void readNBT(NbtCompound nbt, CallbackInfo ci) {
        // Read if the sign is a shop
        if (nbt.getBoolean("shop")) {
            // Store that sign is shop
            setShop();
            // Read product
            setProduct(ItemStack.fromNbt(nbt.getCompound("product")));
            // Read price
            setPrice(ItemStack.fromNbt(nbt.getCompound("price")));
        }
    }

    // Boolean check if the sign is a shop
    @Override
    public void setShop() {
        shop = true;
    }
    @Override
    public boolean isShop() {
        return shop;
    }

    // ItemStack product
    @Override
    public void setProduct(ItemStack itemStack) {
        product = itemStack;
    }
    @Override
    public ItemStack getProduct() {
        if (product == null) {
            return ItemStack.EMPTY;
        } else {
            return product;
        }
    }

    // ItemStack price per one instance of the ItemStack product
    @Override
    public void setPrice(ItemStack itemStack) {
        price = itemStack;
    }
    @Override
    public ItemStack getPrice() {
        if (price == null) {
            return ItemStack.EMPTY;
        } else {
            return price;
        }
    }

}
