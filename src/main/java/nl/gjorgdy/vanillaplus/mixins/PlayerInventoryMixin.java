package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import nl.gjorgdy.vanillaplus.modules.Treasures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    //@Inject(at=@At("HEAD"), method = "addStack(ILnet/minecraft/item/ItemStack;)I")
    //private void onAddStack(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
    //    VanillaPlus.LOGGER.info("add");
    //    Treasures.checkTreasure(stack);
    //}

    @Inject(at=@At("HEAD"), method = "setStack")
    private void onSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        //VanillaPlus.LOGGER.info("set " + stack.getName() + " slot " + slot);
        Treasures.checkTreasure(stack);
    }

}
