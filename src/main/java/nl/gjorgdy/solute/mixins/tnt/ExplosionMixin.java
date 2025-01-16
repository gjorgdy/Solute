package nl.gjorgdy.solute.mixins.tnt;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.List;
import java.util.Random;

@Mixin(ExplosionImpl.class)
public class ExplosionMixin {

    @Unique
    private static final Random random = new Random();

    @SuppressWarnings("InjectedReferences")
    @Redirect(method = "destroyBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"))
    private void affectWorld(World world, BlockPos pos, ItemStack stack) {
        Collection<ItemStack> drops = getDrops(stack);
        if (drops == null) Block.dropStack(world, pos, stack);
        else drops.forEach(_stack -> Block.dropStack(world, pos, _stack));
    }

    @Unique
    private static Collection<ItemStack> getDrops(ItemStack drop) {
        int wholeDropCount = 0;
        Item shatteredDrop = null;
        int shatteredDropCount = 0;

        for (int i = 0; i < drop.getCount(); i++) {
            if (drop.isOf(Items.COBBLESTONE)) {
                int count = random.nextInt(7) - 2;
                if (count > 0) shatteredDropCount += count;
                else wholeDropCount++;
                shatteredDrop = Items.GRAVEL;
            } else if (drop.isOf(Items.SANDSTONE)) {
                int count = random.nextInt(10) - 5;
                if (count > 0) shatteredDropCount += count;
                else wholeDropCount++;
                shatteredDrop = Items.SAND;
            } else if (drop.isOf(Items.RED_SANDSTONE)) {
                int count = random.nextInt(10) - 5;
                if (count > 0) shatteredDropCount += count;
                else wholeDropCount++;
                shatteredDrop = Items.RED_SAND;
            } else {
                return null;
            }
        }
        if (shatteredDrop == null) return null;
        return List.of(drop.copyWithCount(wholeDropCount), new ItemStack(shatteredDrop, shatteredDropCount));
    }

}
