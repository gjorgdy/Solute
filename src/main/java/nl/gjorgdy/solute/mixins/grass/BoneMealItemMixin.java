package nl.gjorgdy.solute.mixins.grass;

import net.minecraft.block.Blocks;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {

    @Unique
    private static final List<Vec3i> offsets = List.of(
            //top
            new Vec3i(0, 1, -1),
            new Vec3i(-1, 1, 0),
            new Vec3i(0, 1, 0),
            new Vec3i(1, 1, 0),
            new Vec3i(0, 1, 1),
            //middle
            new Vec3i(-1, 0, -1),
            new Vec3i(0, 0, -1),
            new Vec3i(1, 0, -1),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 0),
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 1),
            new Vec3i(0, 0, 1),
            new Vec3i(1, 0, 1),
            //bottom
            new Vec3i(0, -1, -1),
            new Vec3i(-1, -1, 0),
            new Vec3i(0, -1, 0),
            new Vec3i(1, -1, 0),
            new Vec3i(0, -1, 1)
    );

    @Unique
    private static void spreadGrass(World world, BlockPos pos, int radius) {
        world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState());
        if (radius == 0) return;
        offsets.forEach(os -> {
            BlockPos _pos = pos.add(os);
            int r = Math.abs(os.getX()) + Math.abs(os.getY()) + Math.abs(os.getZ()) + 1;
            if (world.getBlockState(_pos).isOf(Blocks.DIRT) && world.getRandom().nextInt(r) == 0) {
                spreadGrass(world, _pos, radius - 1);
            }
        });
    }

    @Inject(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;success(Z)Lnet/minecraft/util/ActionResult;", ordinal = 0))
    private void useOnGrass(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World w = context.getWorld();
        BlockPos bp = context.getBlockPos();
        if (w.getBlockState(bp).isOf(Blocks.GRASS_BLOCK))
            spreadGrass(w, bp, 3);
    }

    @Inject(method = "useOnBlock", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void useOnDirt(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World w = context.getWorld();
        BlockPos bp = context.getBlockPos();
        if (w.getBlockState(bp).isOf(Blocks.DIRT)) {
            spreadGrass(w, bp, 1);
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
