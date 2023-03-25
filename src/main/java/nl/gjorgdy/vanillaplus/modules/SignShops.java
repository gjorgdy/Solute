package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.*;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.functions.InventoryFunctions;
import nl.gjorgdy.vanillaplus.functions.PlayerFunctions;
import nl.gjorgdy.vanillaplus.interfaces.SignBlockEntityInterface;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SignShops {

    private static final List<Block> storageBlocks = List.of(
            Blocks.CHEST,
            Blocks.BARREL,
            Blocks.DROPPER,
            Blocks.DISPENSER,
            Blocks.SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.LIGHT_GRAY_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.WHITE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX
    );

    public static void create(SignBlockEntity sign) {
        // Get location and world of sign
        BlockPos pos = sign.getPos();
        World world = sign.getWorld();
        // Cancel if not in a valid world
        if (world == null) {
            return;
        }
        // Get BlockState of sign
        BlockState signBlock = world.getBlockState(pos);
        // Cancel if not a wall sign
        if (!(signBlock.getBlock() instanceof WallSignBlock)) {
            return;
        }
        Direction facing = signBlock.get(WallSignBlock.FACING).getOpposite();
        BlockState wallBlock = world.getBlockState(pos.offset(facing));
        // If block the sign is placed against is valid
        if (storageBlocks.contains(wallBlock.getBlock())) {
            // Set first line to 'Shop'
            sign.setTextOnRow(0, Text.literal("⇄ Trade ⇄").formatted(Formatting.ITALIC).formatted(Formatting.BOLD));
            // Set second to line to '{add product}' to indicate next step in creation
            sign.setTextOnRow(1, Text.literal("{add product}"));
            // Set third line to empty
            sign.setTextOnRow(2, Text.literal("{add price}"));
            // Set fourth line to empty
            sign.setTextOnRow(3, Text.literal("0").formatted(Formatting.ITALIC));
            // Make the sign a shop
            ((SignBlockEntityInterface) sign).vanillaPlus$setShop();
        }
    }

    public static boolean setProduct(SignBlockEntity sign, ItemStack product) {
        // Get an instance of the sign based on the custom interface
        SignBlockEntityInterface signShop = (SignBlockEntityInterface) sign;
        // If item is not air
        if (!product.isEmpty() && signShop.vanillaPlus$getProduct() == null) {
            // Store the product to the sign
            signShop.vanillaPlus$setProduct(product);
            // Set third line to indicate next step in creation
            writeItemToSign(sign, 1, product, Formatting.GREEN);
            // Return succession
            return true;
        }
        return false;
    }

    public static boolean setPrice(SignBlockEntity sign, ItemStack price) {
        sign.setEditable(true);
        // Get an instance of the sign based on the custom interface
        SignBlockEntityInterface signShop = (SignBlockEntityInterface) sign;
        // If item is not air
        if (!price.isEmpty() && signShop.vanillaPlus$getPrice() == null) {
            // Store the product to the sign
            signShop.vanillaPlus$setPrice(price);
            // Write to sign
            writeItemToSign(sign, 2, price, Formatting.RED);
            // Return succession
            return true;
        }
        return false;
    }

    public static void writeItemToSign(SignBlockEntity sign, int line, @NotNull ItemStack itemStack, Formatting color) {
        sign.setEditable(true);
        // Get amount of items
        int amount = itemStack.getCount();
        // Get the name of the product
        String translationKey = itemStack.getItem().getTranslationKey();
        // Create a compound of the amount and name of the item
        Text textCompound = Text.literal(amount + "x ").append(Text.translatable(translationKey)).formatted(color);
        sign.setTextOnRow(line, textCompound);
    }

    public static void updateStock(SignBlockEntity sign) {
        // Get an instance of the sign based on the custom interface
        SignBlockEntityInterface signShop = (SignBlockEntityInterface) sign;
        Inventory shopInventory = getShopInventory(sign);
        ItemStack product = signShop.vanillaPlus$getProduct();
        int totalAmount = shopInventory.count(product.getItem());
        int itemAmount = product.getCount();
        int stock;
        if (totalAmount == 0 || itemAmount == 0) {
            stock = 0;
        } else {
            stock = totalAmount / itemAmount;
        }

        Text textCompound = Text.literal(String.valueOf(stock)).formatted(Formatting.ITALIC);
        sign.setTextOnRow(3, textCompound);
    }

    public static Inventory getShopInventory(SignBlockEntity sign) {
        // Get location and world of sign
        BlockPos pos = sign.getPos();
        World world = sign.getWorld();
        // Cancel if not in a valid world
        if (world == null) {
            return null;
        }
        // Get the shop container
        BlockState signBlock = world.getBlockState(pos);
        Direction facing = signBlock.get(WallSignBlock.FACING).getOpposite();
        // Return the inventory of the container
        return InventoryFunctions.get(world, pos.offset(facing));
    }

    public static boolean makePurchase(SignBlockEntity sign, ServerPlayerEntity player) {
        // Get shop instance
        SignBlockEntityInterface signShop = (SignBlockEntityInterface) sign;
        // Get item in players hand
        ItemStack playerHand = player.getMainHandStack();

    // Get basic info
        // Get inventory of shop
        Inventory shopInventory = getShopInventory(sign);
        // Get inventory of player
        PlayerInventory playerInventory = player.getInventory();
        // Get shop product
        ItemStack shopProduct = signShop.vanillaPlus$getProduct();
        Item shopProductItem = shopProduct.getItem();
        int shopProductAmount = shopProduct.getCount();
        // Get shop price
        ItemStack shopPrice = signShop.vanillaPlus$getPrice();
        Item shopPriceItem = shopPrice.getItem();
        int shopPriceAmount = shopPrice.getCount();
        // Get amount of product in shopInventory
        int shopStock = shopInventory.count(shopProduct.getItem());

    // Check all conditions
        // Payment item & amount
        if (!(playerHand.getItem() == shopPriceItem && playerHand.getCount() >= shopPriceAmount)) {
            MutableText text = Text.literal("\"" + playerHand.getCount() + "x ")
                    .append(Text.translatable(playerHand.getTranslationKey()))
                    .append(Text.literal("\" is not a valid payment"));
            PlayerFunctions.sendError(player, text);
            return false;
        }
        // Stock in shopInventory
        else if (shopStock < shopProduct.getCount()) {
            PlayerFunctions.sendError(player, "This shop does not have enough stock");
            return false;
        }
        // Payment fit in shopInventory
        else if (!InventoryFunctions.isSpaceForItem(shopInventory, shopPrice)) {
            PlayerFunctions.sendError(player, "This shop has no room for payment");
            return false;
        }
        // Product fit in players inventory
        else if (!InventoryFunctions.isSpaceForItem(playerInventory, shopProduct)) {
            PlayerFunctions.sendError(player, "You don't have inventory space for the product");
            return false;
        } else {
    // Make a purchase
            // Get the payment from the players hand
            ItemStack payment = playerHand.copyWithCount(shopPriceAmount);
            // Remove payment from players hand
            playerHand.decrement(shopPriceAmount);
            // Add payment to shopInventory
            InventoryFunctions.addStack(shopInventory, payment);
            // Take the product from shopInventory
            List<ItemStack> productList = InventoryFunctions.takeItems(shopInventory, shopProductItem, shopProductAmount);
            // Add product to players inventory
            for (ItemStack _product : productList) {
                playerInventory.insertStack(_product);
            }
            // Send Action Bar
            PlayerFunctions.sendActionBar(player,
                    Text.literal("Traded ")
                            .append(Text.literal( shopProductAmount + " "))
                            .append(Text.translatable(shopProduct.getTranslationKey()))
                            .append(Text.literal("(s) for " + shopPriceAmount + " "))
                            .append(Text.translatable(shopPrice.getTranslationKey()))
                            .formatted(Formatting.GREEN));
            return true;
        }
    }

}
