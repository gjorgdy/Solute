package nl.gjorgdy.vanillaplus.listeners;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.modules.LoweringLadders;

public class UseBlockCallbackListener implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        BlockState block = world.getBlockState(hitResult.getBlockPos());
        if (player.getMainHandStack().isOf(Items.LADDER) && block.isOf(Blocks.LADDER)) {
            LoweringLadders.lower(player, player.getMainHandStack(), world, hitResult.getBlockPos());
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

}
