package nl.gjorgdy.vanillaplus.mixins.faster_farming;

import net.minecraft.block.CropBlock;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import nl.gjorgdy.vanillaplus.modules.FasterFarming;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public class HoeItemMixin {

    @Inject(at = @At("HEAD"), method="useOnBlock", cancellable = true)
    public void onUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof CropBlock) {
            FasterFarming.farmArea(context.getWorld(), context.getBlockPos(), context.getPlayer(), context.getStack());
            cir.setReturnValue(ActionResult.PASS);
        }
    }

}
