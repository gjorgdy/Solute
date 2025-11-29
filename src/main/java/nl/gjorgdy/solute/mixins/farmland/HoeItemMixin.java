package nl.gjorgdy.solute.mixins.farmland;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.modules.Farmland;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        conflict = @Condition("farmtweaks")
)
@Mixin(HoeItem.class)
public class HoeItemMixin {

    @Inject(at = @At("HEAD"), method="useOnBlock", cancellable = true)
    public void onUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.farmlandModule.enabled) return;
        // farm area if crops
        var world = context.getWorld();
        var pos = context.getBlockPos();
        var player = context.getPlayer();
        if (world.getBlockState(pos).getBlock() instanceof CropBlock && Farmland.farmArea(context)) {
            if (player != null) {
                player.swingHand(context.getHand(), true);
            }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
        // return farmland to dirt if empty
        else if (world.getBlockState(pos).isOf(Blocks.FARMLAND)) {
            if (world.getBlockState(pos.up()).isAir()) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1f, 0.5f);
                if (player != null) {
                    context.getStack().damage(1, player, context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    player.swingHand(context.getHand(), true);
                }
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

}
