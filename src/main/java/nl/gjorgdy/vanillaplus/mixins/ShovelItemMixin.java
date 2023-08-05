package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "useOnBlock", at = @At(value = "RETURN"))
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue() == ActionResult.PASS && context.getSide() != Direction.DOWN) {
            World w = context.getWorld();
            BlockPos bp = context.getBlockPos();
            BlockState bs = w.getBlockState(bp);
            if (bs.isOf(Blocks.DIRT_PATH)) {
                PlayerEntity p = context.getPlayer();
                BlockState dirt = Blocks.DIRT.getDefaultState();
                w.setBlockState(bp, dirt, 11);
                w.emitGameEvent(GameEvent.BLOCK_CHANGE, bp, GameEvent.Emitter.of(p, dirt));
                if (p != null) {
                    context.getStack().damage(1, p, (e) -> {
                        e.sendToolBreakStatus(context.getHand());
                    });
                }
            }
        }
    }

}
