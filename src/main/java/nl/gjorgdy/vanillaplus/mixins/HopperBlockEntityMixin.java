package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import nl.gjorgdy.vanillaplus.functions.ItemMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    //@Shadow private DefaultedList<ItemStack> inventory;
    //public List<String> mappings = new ArrayList<>();

    //@Inject(at = @At("TAIL"), method="readNbt")
    //private void onLoad(NbtCompound nbt, CallbackInfo ci) {
    //    // On loading of a hopper inventory, cache the filters
    //    mappings = ItemMaps.cacheInventory(inventory);
    //}

    @Inject(at = @At("HEAD"), method="canInsert", cancellable = true)
    private static void canInsert(Inventory inventory, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        // Check if 'stack' can be added to 'inventory' based on filters
        if (!ItemMaps.compareItem(inventory, stack)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method="canExtract", cancellable = true)
    private static void canExtract(Inventory inventory, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        // Check if 'stack' can be added to 'inventory' based on filters
        if (!ItemMaps.compareItem(inventory, stack)) {
            cir.setReturnValue(false);
        }
    }

}
