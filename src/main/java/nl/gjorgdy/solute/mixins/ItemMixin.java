package nl.gjorgdy.solute.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "finishUsing", at = @At("RETURN"))
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.glowBerryModule.enabled) return;
        // early return if module disabled
        if (stack.isOf(Items.GLOW_BERRIES)) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 160, 0, false, true, true));
        }
    }

    @Inject(method = "use", at = @At(value = "RETURN"))
    public void use(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.slimeModule.enabled) return;
        // early return if module disabled
        if (player.getStackInHand(hand).isOf(Items.SLIME_BALL)) {
            ServerWorld serverWorld = (ServerWorld) world;
            Slime.use(serverWorld, player, hand);
        }
    }

}
