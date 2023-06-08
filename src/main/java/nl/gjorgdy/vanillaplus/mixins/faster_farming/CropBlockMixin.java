package nl.gjorgdy.vanillaplus.mixins.faster_farming;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.FasterFarming;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    //@Mixin(targets = "net.minecraft.block.CropBlock")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack mainhandItem = player.getMainHandStack();
        ItemStack offhandItem = player.getOffHandStack();

        // If main hand is a hoe
        if (mainhandItem.getItem() instanceof HoeItem) {
            FasterFarming.farmArea(world, pos, mainhandItem);
        }
        // If main hand is empty
        else {
            FasterFarming.farmArea(world, pos, offhandItem);
        }
        return ActionResult.PASS;
    }



}
