package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChorusFruitItem.class)
public class ChorusFruitItemMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"), cancellable = true)
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (world.getRegistryKey() == World.END) {
            cir.setReturnValue(stack);
            cir.cancel();
        }
    }

}
