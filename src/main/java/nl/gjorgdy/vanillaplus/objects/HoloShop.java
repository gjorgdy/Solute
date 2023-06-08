package nl.gjorgdy.vanillaplus.objects;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.text.Text;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import nl.gjorgdy.vanillaplus.functions.EntityFunctions;
import nl.gjorgdy.vanillaplus.functions.InventoryFunctions;
import nl.gjorgdy.vanillaplus.interfaces.HoloShopInterface;
import nl.gjorgdy.vanillaplus.mixins.display_entities.ItemDisplayEntityMixin;
import nl.gjorgdy.vanillaplus.mixins.display_entities.TextDisplayEntityMixin;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public class HoloShop {

    private static String SHOP_NBT_KEY = "vp$shop";
    private final ItemStack outStack;
    private final ItemStack inStack;
    private final Inventory inventory;
    private final Entity[] entities;


    public static HoloShop create(World world, BlockPos pos, ItemStack outStack, ItemStack inStack) {
        // Create a list of entities for the shop
        Entity[] entities = new Entity[5];
        // Create itemDisplay
        entities[0] = createItemDisplay(world, pos, outStack);
        // Create itemCountDisplay
        entities[1] = createItemCountDisplay(world, pos, outStack);
        // Create textDisplay
        //entities[2]
        // Create interaction
        //entities[3]

        // Create and return the shop instance itself
        return new HoloShop(
                InventoryFunctions.get(world, pos),
                outStack,
                inStack,
                entities
        );
    }

    public boolean remove() {
        for (Entity e : entities) {
            if (e != null) {
                e.remove(Entity.RemovalReason.KILLED);
            }
        }
        return true;
    }

    public static HoloShop fromNbt(NbtCompound containerNbt, World world, BlockPos pos) {
        NbtCompound shopNbt = containerNbt.getCompound(SHOP_NBT_KEY);
        return new HoloShop(
            InventoryFunctions.get(world, pos),
            ItemStack.fromNbt(shopNbt.getCompound("out_stack")),
            ItemStack.fromNbt(shopNbt.getCompound("in_stack")),
            EntityFunctions.fromIdList(world, shopNbt.getIntArray("entities"))
        );
    }

    public NbtCompound writeNbt(NbtCompound containerNbt) {
        NbtCompound shopNbt = new NbtCompound();
        shopNbt.put("out_Stack", this.outStack.writeNbt(new NbtCompound()));
        shopNbt.put("in_Stack", this.inStack.writeNbt(new NbtCompound()));
        shopNbt.put("entities", new NbtIntArray(EntityFunctions.toIdList(this.entities)));
        containerNbt.put(SHOP_NBT_KEY, shopNbt);

        return containerNbt;
    }

    private HoloShop(Inventory inventory, ItemStack outStack, ItemStack inStack, Entity[] entities) {
        this.outStack = outStack;
        this.inStack = inStack;
        this.inventory = inventory;
        this.entities = entities;
    }

    public static Entity createItemDisplay(World world, BlockPos pos, ItemStack outStack) {
        Vec3d vecPos = Vec3d.of(pos);
        vecPos = vecPos.add(0.5, 1, 0.5);
    // outStack display entity
        DisplayEntity.ItemDisplayEntity itemDisplayEntity = EntityType.ITEM_DISPLAY.create(world);
        // Set position
        itemDisplayEntity.setPosition(vecPos);
        // Create a variable using the mixin as an instance to make the display entity mutable
        ItemDisplayEntityMixin mutableItemDisplayEntity = (ItemDisplayEntityMixin) itemDisplayEntity;
        // Set item
        mutableItemDisplayEntity.getStackReference().set(outStack);
        // Set transformation
        AffineTransformation transformation = new AffineTransformation(
                new Vector3f(0f, 0.5f, 0f),
                null,
                new Vector3f(0.5f, 0.5f, 0.5f),
                null
        );
        mutableItemDisplayEntity.callSetTransformation(transformation);
        // Set item to full brightness
        //mutableItemDisplayEntity.callSetBrightness(Brightness.FULL);
        // Set item to be fixed (so it can rotate)
        mutableItemDisplayEntity.callSetBillboardMode(DisplayEntity.BillboardMode.FIXED);
        // Set view range to ~80 blocks
        mutableItemDisplayEntity.callSetViewRange(1f);
        // Set item to look like it would as a dropped item
        mutableItemDisplayEntity.callSetTransformationMode(ModelTransformationMode.GUI);
        // Spawn
        world.spawnEntity(itemDisplayEntity);
        // Return the entity
        return itemDisplayEntity;
    }

    public static Entity createItemCountDisplay(World world, BlockPos pos, ItemStack outStack) {
        Vec3d vecPos = Vec3d.of(pos);
        vecPos = vecPos.add(0.5, 1, 0.5);
        // outStack display entity
        if (outStack.getCount() > 1) {
            DisplayEntity.TextDisplayEntity textEntity = EntityType.TEXT_DISPLAY.create(world);
            // Set position
            textEntity.setPosition(vecPos);
            // Create a variable using the mixin as an instance to make the display entity mutable
            TextDisplayEntityMixin mutableTextDisplayEntity = (TextDisplayEntityMixin) textEntity;
            // Set transformation
            AffineTransformation textTransformation = new AffineTransformation(
                    new Vector3f(0.2f, 0.3f, 0.2f),
                    null,
                    new Vector3f(0.4f),
                    null
            );
            mutableTextDisplayEntity.callSetTransformation(textTransformation);
            // Set the text to the amount of the item
            mutableTextDisplayEntity.callSetText(Text.literal(String.valueOf(outStack.getCount())));
            // Set the background to be completely transparent
            mutableTextDisplayEntity.callSetBackground(0x00000000);
            // Set the text to be static on the x-axis
            mutableTextDisplayEntity.callSetBillboardMode(DisplayEntity.BillboardMode.VERTICAL);
            // Enable full brightness
            //mutableTextDisplayEntity.callSetBrightness(Brightness.FULL);
            mutableTextDisplayEntity.callSetViewRange(1f);
            // Summon the textEntity
            world.spawnEntity(textEntity);
            // Return the entity
            return textEntity;
        } else {
            return null;
        }
    }

}
