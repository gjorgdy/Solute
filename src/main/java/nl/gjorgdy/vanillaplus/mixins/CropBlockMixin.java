package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.ExpandedFarming;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CropBlock.class)
public class CropBlockMixin {


    //@Mixin(targets = "net.minecraft.block.CropBlock")
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack mainhandItem = player.getMainHandStack();
        ItemStack offhandItem = player.getOffHandStack();

        // If main hand is a hoe
        if (mainhandItem.getItem() instanceof HoeItem) {
            int range = ExpandedFarming.getRange(mainhandItem.getItem());
            ExpandedFarming.farmArea(world, pos, mainhandItem, range);
        }
        // If off-hand is a hoe and main hand is empty
        else if (mainhandItem.getItem() == Items.AIR & offhandItem.getItem() instanceof HoeItem) {
            int range = ExpandedFarming.getRange(offhandItem.getItem());
            ExpandedFarming.farmArea(world, pos, offhandItem, range);
        }
        // If both hands are empty
        else if (mainhandItem.getItem() == Items.AIR & offhandItem.getItem() == Items.AIR) {
            ExpandedFarming.farmArea(world, pos, offhandItem, 0);
        }
        return ActionResult.PASS;
    }



}
