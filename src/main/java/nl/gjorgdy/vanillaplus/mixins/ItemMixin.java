package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.FernFire;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "finishUsing", at = @At("RETURN"))
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (stack.isOf(Items.GLOW_BERRIES)) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 160, 0, false, true, true));
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;pass(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;"))
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack mainHand = user.getMainHandStack();
        ItemStack offHand = user.getOffHandStack();
        if (mainHand.isOf(Items.FERN) && offHand.isOf(Items.FLINT_AND_STEEL)) {
            FernFire.burn(user, mainHand, offHand);
        } else if (offHand.isOf(Items.FERN) && mainHand.isOf(Items.FLINT_AND_STEEL)) {
            FernFire.burn(user, offHand, mainHand);
        }
    }

}
