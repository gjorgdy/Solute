package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.interfaces.SignBlockEntityInterface;

import java.util.List;


public class SignShops {

    private static List<Block> storageBlocks = List.of(
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

    public static boolean create(SignBlockEntity sign) {
        // Get location and world of sign
        BlockPos pos = sign.getPos();
        World world = sign.getWorld();
        // Cancel if not in a valid world
        if (world == null) {
            return false;
        }
        // Get BlockState of sign
        BlockState signBlock = world.getBlockState(pos);
        // Cancel if not a wall sign
        if (!(signBlock.getBlock() instanceof WallSignBlock)) {
            return false;
        }
        Direction facing = signBlock.get(WallSignBlock.FACING).getOpposite();
        BlockState wallBlock = world.getBlockState(pos.offset(facing));
        // If block the sign is placed against is valid
        if (storageBlocks.contains(wallBlock.getBlock())) {
            // Set first line to 'Shop'
            sign.setTextOnRow(0, Text.literal("Shop").formatted(Formatting.ITALIC).formatted(Formatting.BOLD));
            // Set second to line to '{add product}' to indicate next step in creation
            sign.setTextOnRow(1, Text.literal("{add product}"));
            // Set third line to empty
            sign.setTextOnRow(2, Text.literal(""));
            // Set fourth line to empty
            sign.setTextOnRow(3, Text.literal(""));
            // Make the sign a shop
            ((SignBlockEntityInterface) sign).setShop();
            // Update the block to surrounding players
            updateBlock(sign);
            return true;
        } else {
            return false;
        }
    }

    public static void setProduct(SignBlockEntity sign, ItemStack product) {
        // Get an instance of the sign based on the custom interface
        SignBlockEntityInterface signShop = (SignBlockEntityInterface) sign;
        // If item is not air
        if (product.getItem() != Items.AIR) {
            // Store the product to the sign
            signShop.setProduct(product);
            // Get amount of items
            int amount = product.getCount();
            // Get the name of the product
            String translationKey = product.getItem().getTranslationKey();
            // Create a compound of the amount and name of the item
            Text textCompound = Text.literal(amount + "x ").append(Text.translatable(translationKey)).formatted(Formatting.GREEN);
            sign.setTextOnRow(1, textCompound);
            // Set third line to indicate next step in creation
            sign.setTextOnRow(2, Text.literal("{add price}"));
            updateBlock(sign);
        }
    }

    public static void setPrice(SignBlockEntity sign, ItemStack price) {
        // Get an instance of the sign based on the custom interface
        SignBlockEntityInterface signShop = (SignBlockEntityInterface) sign;
        // If item is not air
        if (price.getItem() != Items.AIR) {
            // Store the product to the sign
            signShop.setPrice(price);
            // Get amount of items
            int amount = price.getCount();
            // Get the name of the product
            String translationKey = price.getItem().getTranslationKey();
            // Create a compound of the amount and name of the item
            Text textCompound = Text.literal(amount + "x ").append(Text.translatable(translationKey)).formatted(Formatting.RED);
            sign.setTextOnRow(2, textCompound);
            //updateBlock(sign);
            updateAvailability(sign);
        }
    }

    public static void updateBlock(SignBlockEntity sign) {
        // Force saving sign
        sign.markDirty();
        // Force updates to players
        sign.getWorld().updateListeners(sign.getPos(), sign.getWorld().getBlockState(sign.getPos()), sign.getWorld().getBlockState(sign.getPos()), 3);
    }

    public static void updateAvailability(SignBlockEntity sign) {
        // Get an instance of the sign based on the custom interface
        SignBlockEntityInterface signShop = (SignBlockEntityInterface) sign;
        Inventory shopInventory = getShopInventory(sign);
        ItemStack product = signShop.getProduct();
        int totalAmount = shopInventory.count(product.getItem());
        int itemAmount = product.getCount();
        int purchaseAmount = totalAmount / itemAmount;

        Text textCompound = Text.literal(String.valueOf(purchaseAmount)).formatted(Formatting.DARK_GRAY);
        sign.setTextOnRow(3, textCompound);
        updateBlock(sign);
    }

    public static Inventory getShopInventory(SignBlockEntity sign) {
        // Get location and world of sign
        BlockPos pos = sign.getPos();
        World world = sign.getWorld();
        // Cancel if not in a valid world
        if (world == null) {
            return null;
        }
        // Get shopInventory
        BlockState signBlock = world.getBlockState(pos);
        Direction facing = signBlock.get(WallSignBlock.FACING).getOpposite();
        BlockEntity shopBlockEntity = world.getBlockEntity(pos.offset(facing));
        return (Inventory) shopBlockEntity;
    }

    public static void makePurchase(SignBlockEntity sign, ServerPlayerEntity player) {
        Inventory shopInventory = getShopInventory(sign);
        // Get playerInventory
        Inventory playerInventory = player.getInventory();

        updateAvailability(sign);
    }

}
