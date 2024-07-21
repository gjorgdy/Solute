package nl.gjorgdy.solute.mixins.ender_pearl;

import net.minecraft.item.ChorusFruitItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChorusFruitItem.class)
public class ChorusFruitItemMixin {

// no chorus teleport in the end
//    @Inject(method = "finishUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;finishUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
//    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
//        if (world.getRegistryKey() == World.END) {
//            cir.setReturnValue(stack);
//        }
//    }

}
