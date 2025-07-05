package nl.gjorgdy.solute.mixins.farmland;

import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Farmland;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public class HoeItemMixin {

    @Inject(at = @At("HEAD"), method="useOnBlock", cancellable = true)
    public void onUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.farmlandModule.enabled) return;
        // early return if module disabled
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof CropBlock && Farmland.farmArea(context)) {
            PlayerEntity player = context.getPlayer();
            if (player != null) {
                player.swingHand(context.getHand(), true);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

}
