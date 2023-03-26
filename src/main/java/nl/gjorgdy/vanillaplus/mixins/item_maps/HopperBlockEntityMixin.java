package nl.gjorgdy.vanillaplus.mixins.item_maps;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import nl.gjorgdy.vanillaplus.modules.ItemMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    @Inject(at = @At("HEAD"), method="canInsert", cancellable = true)
    private static void canInsert(Inventory inventory, ItemStack stack, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        // Check if 'stack' can be added to 'inventory' based on filters
        if (!ItemMaps.compareItem(inventory, stack)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method="canExtract", cancellable = true)
    private static void canExtract(Inventory hopperInventory, Inventory fromInventory, ItemStack stack, int slot, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        // Check if 'stack' can be added to 'inventory' based on filters
        if (!ItemMaps.compareItem(hopperInventory, stack) || !ItemMaps.compareItem(fromInventory, stack)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}
