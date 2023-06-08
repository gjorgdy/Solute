package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.*;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.functions.BlockFunctions;
import nl.gjorgdy.vanillaplus.functions.InventoryFunctions;
import nl.gjorgdy.vanillaplus.functions.PlayerFunctions;
import nl.gjorgdy.vanillaplus.interfaces.SignShopInterface;
import nl.gjorgdy.vanillaplus.mixins.display_entities.ItemDisplayEntityMixin;
import nl.gjorgdy.vanillaplus.mixins.display_entities.TextDisplayEntityMixin;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;


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
        //    // Set first line to 'Shop'
        //    sign.setTextOnRow(0, Text.literal("Trade").formatted(Formatting.ITALIC).formatted(Formatting.BOLD));
        //    // Set second to line to '{add product}' to indicate next step in creation
        //    sign.setTextOnRow(1, Text.literal("{add product}"));
        //    // Set third line to empty
        //    sign.setTextOnRow(2, Text.literal(""));
        //    // Set fourth line to empty
        //    sign.setTextOnRow(3, Text.literal("0").formatted(Formatting.ITALIC));
            // Make the sign a shop
            ((SignShopInterface) sign).vp$setShop();
            // Update sign block for players
            BlockFunctions.updateBlock(sign.getWorld(), sign.getPos(), true);
        }
    }

    public static void removeShop(SignBlockEntity sign) {
        SignShopInterface signShop = (SignShopInterface) sign;
        // Disable shop
        signShop.vp$setShop(false);
    //    // Set sign to invalid shop
    //    sign.setTextOnRow(0, Text.literal("Trade").formatted(Formatting.BOLD).formatted(Formatting.RED));
    //    sign.setTextOnRow(1, Text.literal(""));
    //    sign.setTextOnRow(2, Text.literal(""));
    //    sign.setTextOnRow(3, Text.literal(""));
        // Update sign
        BlockFunctions.updateBlock(sign.getWorld(), sign.getPos(), true);

    }

    public static boolean setProduct(SignBlockEntity sign, ItemStack product) {
        // Get an instance of the sign based on the custom interface
        SignShopInterface signShop = (SignShopInterface) sign;
        // If item is not air
        if (!product.isEmpty() && signShop.vp$getProduct() == null) {
            // Store the product to the sign
            signShop.vp$setProduct(product);
            // Set third line to indicate next step in creation
            writeItemToSign(sign, 1, product, Formatting.GREEN);
            // Set third line to add price
        //    sign.setTextOnRow(2, Text.literal("{add price}"));
            // Update sign block for players
            BlockFunctions.updateBlock(sign.getWorld(), sign.getPos(), true);
            // Summon item

            BlockPos wallBlockPos = getWallBlockPos(sign.getWorld(), sign.getPos());


            DisplayEntity.ItemDisplayEntity displayEntity = EntityType.ITEM_DISPLAY.create(sign.getWorld());

            AffineTransformation transformation;
            if (product.getItem() instanceof BlockItem) {
                transformation = new AffineTransformation(
                        new Vector3f(0f, 0.5f, 0f),
                        new Quaternionf(0.5, 0.5, 0, 1),
                        new Vector3f(0.2f, 0.2f, 0.2f),
                        null
                );
            } else {
                transformation = new AffineTransformation(
                        new Vector3f(0f, 0.5f, 0f),
                        null,
                        new Vector3f(0.5f, 0.5f, 0.5f),
                        null
                );
            }
            // Format the displayEntity
            ItemDisplayEntityMixin mutableItemDisplayEntity = (ItemDisplayEntityMixin) displayEntity;
            mutableItemDisplayEntity.callSetTransformation(transformation);
            mutableItemDisplayEntity.callSetDIsplayWidth(1f);
            mutableItemDisplayEntity.callSetDisplayHeight(1f);
            mutableItemDisplayEntity.callSetBrightness(Brightness.FULL);
            mutableItemDisplayEntity.callSetBillboardMode(DisplayEntity.BillboardMode.VERTICAL);
            mutableItemDisplayEntity.callSetViewRange(0.2f);
            mutableItemDisplayEntity.callSetTransformationMode(ModelTransformationMode.GUI);
            // Set position
            displayEntity.setPosition(wallBlockPos.getX() + 0.5, wallBlockPos.getY() + 1, wallBlockPos.getZ() + 0.5);
            // Set item
            ((ItemDisplayEntityMixin) displayEntity).getStackReference().set(product);
            // Summon the displayEntity
            sign.getWorld().spawnEntity(displayEntity);

            // Item count
            if (product.getCount() > 1) {
                DisplayEntity.TextDisplayEntity textEntity = EntityType.TEXT_DISPLAY.create(sign.getWorld());
                AffineTransformation textTransformation = new AffineTransformation(
                        new Vector3f(0.2f, 0.3f, 0.2f),
                        null,
                        new Vector3f(0.4f),
                        null
                );
                TextDisplayEntityMixin mutableTextDisplayEntity = (TextDisplayEntityMixin) textEntity;
                mutableTextDisplayEntity.callSetTransformation(textTransformation);
                mutableTextDisplayEntity.callSetText(Text.literal(String.valueOf(product.getCount())));
                mutableTextDisplayEntity.callSetBackground(0x00000000);
                mutableTextDisplayEntity.callSetBillboardMode(DisplayEntity.BillboardMode.VERTICAL);
                mutableTextDisplayEntity.callSetBrightness(Brightness.FULL);
                mutableTextDisplayEntity.callSetViewRange(0.2f);
                // Set position
                textEntity.setPosition(wallBlockPos.getX() + 0.5, wallBlockPos.getY() + 1, wallBlockPos.getZ() + 0.5);
                // Summon the textEntity
                sign.getWorld().spawnEntity(textEntity);
            }

            // Item Name
            MutableText text = Text.literal("                                \n")
                    .append(getStackText(product).formatted(Formatting.GREEN))
                    .append(Text.literal("\n⇄ 1 Diamond(s)").formatted(Formatting.RED, Formatting.ITALIC));

            DisplayEntity.TextDisplayEntity textEntity2 = EntityType.TEXT_DISPLAY.create(sign.getWorld());
            AffineTransformation textTransformation2 = new AffineTransformation(
                    new Vector3f(0f, 0.05f, 0.5f),
                    null,
                    new Vector3f(0.4f),
                    null
            );
            TextDisplayEntityMixin mutableTextDisplayEntity2 = (TextDisplayEntityMixin) textEntity2;
            mutableTextDisplayEntity2.callSetTransformation(textTransformation2);
            mutableTextDisplayEntity2.callSetText(text);
            mutableTextDisplayEntity2.callSetBackground(0x00000000);
            //mutableTextDisplayEntity2.callSetBillboardMode(DisplayEntity.BillboardMode.VERTICAL);
            mutableTextDisplayEntity2.callSetDisplayFlags((byte) 8);
            mutableTextDisplayEntity2.callSetBrightness(Brightness.FULL);
            mutableTextDisplayEntity2.callSetViewRange(0.1f);
            mutableTextDisplayEntity2.callSetLineWidth(88);
            // Set position
            textEntity2.setPosition(wallBlockPos.getX() + 0.5, wallBlockPos.getY() + 1, wallBlockPos.getZ() + 0.5);
            // Summon the textEntity
            sign.getWorld().spawnEntity(textEntity2);

            // Return succession
            return true;
        }
        return false;
    }

    public static boolean setPrice(SignBlockEntity sign, ItemStack price) {
    //    sign.setEditable(true);
        // Get an instance of the sign based on the custom interface
        SignShopInterface signShop = (SignShopInterface) sign;
        // If item is not air and not equal to product
        if (!price.isEmpty() && signShop.vp$getPrice() == null && (!price.isOf(signShop.vp$getProduct().getItem()))) {
            // Store the product to the sign
            signShop.vp$setPrice(price);
            // Write to sign
            writeItemToSign(sign, 2, price, Formatting.RED);
            // Update sign block for players
            BlockFunctions.updateBlock(sign.getWorld(), sign.getPos(), true);
            // Return succession
            return true;
        }
        return false;
    }

    public static void writeItemToSign(SignBlockEntity sign, int line, @NotNull ItemStack itemStack, Formatting color) {
    //   sign.setEditable(true);
        // Get amount of items
        int amount = itemStack.getCount();
        // Create a compound of the amount and name of the item
        MutableText text = itemStack.getMaxCount() == 1 ? Text.literal("") : Text.literal(itemStack.getCount() + "x ");
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            // Get first enchantment
            String enchantmentKey = ((Enchantment) EnchantmentHelper.get(itemStack).keySet().toArray()[0]).getTranslationKey();
            text.append(Text.translatable(enchantmentKey)).formatted(color);
            text.append(Text.literal(" "));
            text.append(Text.translatable("item.minecraft.book"));
        } else {
            // Get the name of the product
            String translationKey = itemStack.getItem().getTranslationKey();
            text.append(Text.translatable(translationKey)).formatted(color);
        }
        // Write text
    //    sign.setTextOnRow(line, text);
    }

    public static void updateStock(SignBlockEntity sign) {
        // Get an instance of the sign based on the custom interface
        SignShopInterface signShop = (SignShopInterface) sign;
        Inventory shopInventory = getShopInventory(sign);
        ItemStack product = signShop.vp$getProduct();
        int totalAmount = shopInventory.count(product.getItem());
        int itemAmount = product.getCount();
        int stock;
        if (totalAmount == 0 || itemAmount == 0) {
            stock = 0;
        } else {
            stock = totalAmount / itemAmount;
        }

        Text textCompound = Text.literal(String.valueOf(stock)).formatted(Formatting.ITALIC);
    //    sign.setTextOnRow(3, textCompound);
    }

    public static void sendShopInformation(PlayerEntity player, SignShopInterface signShop) {
        // Create a text for the product
        ItemStack productStack = signShop.vp$getProduct();
        MutableText productText = getStackText(productStack);

        // Create a text for the price
        ItemStack priceStack = signShop.vp$getPrice();
        MutableText priceText = getStackText(priceStack);

        player.sendMessage(
                Text.literal("\n ← ").formatted(Formatting.GRAY)
                .append(productText.formatted(Formatting.GREEN))
                .append(Text.literal("\n → "))
                .append(priceText.formatted(Formatting.RED))
                .append(Text.literal("\n "))
        );
    }

    public static MutableText getStackText(ItemStack stack) {
        MutableText text = Text.literal(""); //stack.getMaxCount() == 1 ? Text.literal("") : Text.literal(stack.getCount() + "x ");
        text.append(Text.translatable(stack.getTranslationKey()));
        if (stack.hasEnchantments() || stack.isOf(Items.ENCHANTED_BOOK)) {
            for (Map.Entry<Enchantment, Integer> e : EnchantmentHelper.get(stack).entrySet()) {
                MutableText enchantmentText = Text.literal("\n");
                enchantmentText.append(Text.translatable(e.getKey().getTranslationKey()));
                enchantmentText.append(Text.literal(" "));
                if (e.getKey().getMaxLevel() > 1) {
                    enchantmentText.append(Text.translatable("enchantment.level." + e.getValue()));
                }
                enchantmentText.formatted(Formatting.GRAY);
                text.append(enchantmentText);
            }
        }
        if (stack.getItem() instanceof MusicDiscItem || stack.getItem() instanceof BannerPatternItem || stack.getItem() instanceof GoatHornItem) {
            text.append(Text.literal("\n"));
            text.append(Text.translatable(stack.getTranslationKey() + ".desc").formatted(Formatting.GRAY));
        }

        return text;
    }

    public static Inventory getShopInventory(SignBlockEntity sign) {
        // Get location and world of sign
        BlockPos pos = sign.getPos();
        World world = sign.getWorld();
        // Cancel if not in a valid world
        if (world == null) {
            return null;
        }
        // Return the inventory of the container
        return InventoryFunctions.get(world, getWallBlockPos(world, pos));
    }

    public static BlockPos getWallBlockPos(World world, BlockPos pos) {
        BlockState signBlock = world.getBlockState(pos);
        Direction facing = signBlock.get(WallSignBlock.FACING).getOpposite();
        return pos.offset(facing);
    }

    public static boolean makePurchase(SignBlockEntity sign, ServerPlayerEntity player) {
        // Get shop instance
        SignShopInterface signShop = (SignShopInterface) sign;
        // Get item in players hand
        ItemStack playerHand = player.getMainHandStack();

    // Get basic info
        // Get inventory of shop
        Inventory shopInventory = getShopInventory(sign);
        // Get inventory of player
        PlayerInventory playerInventory = player.getInventory();
        // Get shop product
        ItemStack shopProduct = signShop.vp$getProduct();
        Item shopProductItem = shopProduct.getItem();
        int shopProductAmount = shopProduct.getCount();
        // Get shop price
        ItemStack shopPrice = signShop.vp$getPrice();
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
            List<ItemStack> productList = InventoryFunctions.takeItems(shopInventory, shopProduct);
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
