package nl.gjorgdy.solute.mixins.grass;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.utils.BlockPosUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Unique
    private static void growGrass(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState());
    }

    @Unique
    private static void spreadGrass(World world, BlockPos pos, int depth) {
        if (world.getBlockState(pos).isOf(Blocks.DIRT) || world.getBlockState(pos).isOf(Blocks.GRASS_BLOCK)) {
            int grassNeighbours = countGrassNeighbors(world, pos);
            if (grassNeighbours > 0 && world.getRandom().nextInt(Math.max(4 - grassNeighbours, 1)) == 0) {
                growGrass(world, pos);
                if (depth > 0) BlockPosUtils.forNeighbours(
                    pos,
                    _pos -> spreadGrass(world, _pos, depth - 1)
                );
            }
        }
    }

    @Unique
    private static int countGrassNeighbors(World world, BlockPos pos) {
        AtomicInteger grassBlockCount = new AtomicInteger();
        BlockPosUtils.forDirectNeighbours(pos, _pos -> {
            if (world.getBlockState(_pos).isOf(Blocks.GRASS_BLOCK))
                grassBlockCount.getAndIncrement();
        });
        return grassBlockCount.get();
    }

    @Unique
    private static void success(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            player.swingHand(context.getHand(), true);
            context.getStack().decrementUnlessCreative(1, player);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Inject(method = "useOnBlock", at = @At(value = "RETURN"), cancellable = true)
    private void useOnGrass(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.grassModule.enabled) return;
        // early return if module disabled
        World w = context.getWorld();
        BlockPos bp = context.getBlockPos();
        if (w.getBlockState(bp).isOf(Blocks.GRASS_BLOCK)) {
            spreadGrass(w, bp, 2);
            if (cir.getReturnValue() != ActionResult.SUCCESS) success(context, cir);
        }
    }

    @Inject(method = "useOnBlock", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void useOnDirt(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // early return if module disabled
        if (!Solute.CONFIG.grassModule.enabled) return;
        // early return if module disabled
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (world.getBlockState(pos).isOf(Blocks.DIRT)) {
            spreadGrass(world, pos, 1);
            success(context, cir);
        }
    }
}
