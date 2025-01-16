package nl.gjorgdy.solute.mixins.tnt;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.util.Random;

@Mixin(ExplosionImpl.class)
public class ExplosionMixin {

    @Unique
    private static final Random random = new Random();

    @ModifyArgs(method = "destroyBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V"))
    private void affectWorld(Args args) {
        args.set(2, getDrop(args.get(2)));
    }

    @Unique
    private static ItemStack getDrop(ItemStack drop) {

        if (drop.isOf(Items.COBBLESTONE)) {
            int count = random.nextInt(7) - 2;
            if (count > 0) {
                drop = new ItemStack(Items.GRAVEL, count * drop.getCount());
            }
        } else if (drop.isOf(Items.SANDSTONE)) {
            int count = random.nextInt(11) - 5;
            if (count > 0) {
                drop = new ItemStack(Items.SAND, drop.getCount() * count);
            }
        } else if (drop.isOf(Items.RED_SANDSTONE)) {
            int count = random.nextInt(11) - 5;
            if (count > 0) {
                drop = new ItemStack(Items.RED_SAND, drop.getCount() * count);
            }
        }
        return drop;
    }

}
