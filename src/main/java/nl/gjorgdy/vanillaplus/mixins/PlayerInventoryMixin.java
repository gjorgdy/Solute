package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(at=@At("HEAD"), method = "addStack(ILnet/minecraft/item/ItemStack;)I")
    private void onInsertStack(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        //VanillaPlus.LOGGER.info("Inserted " + stack);
    }

}
