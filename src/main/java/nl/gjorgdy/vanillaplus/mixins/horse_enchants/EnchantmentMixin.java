package nl.gjorgdy.vanillaplus.mixins.horse_enchants;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

//    Enchantment enchantment = (Enchantment) (Object) this;
//
//    @Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
//    private void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
//        if (stack.getItem() instanceof AnimalArmorItem) cir.setReturnValue(!isHorseArmorEnchant());
//    }

//    private boolean isHorseArmorEnchant() {
//        return enchantment.getClass().equals(FrostWalkerEnchantment.class) || enchantment.getClass().equals(DepthStriderEnchantment.class);
//    }

}
