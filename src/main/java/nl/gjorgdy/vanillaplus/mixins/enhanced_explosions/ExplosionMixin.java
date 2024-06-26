package nl.gjorgdy.vanillaplus.mixins.enhanced_explosions;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Unique
    private static final Random random = new Random();

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<?> affectWorld(List<Pair<ItemStack, BlockPos>> list) {
        return list.stream().map(this::getDrop).iterator();
    }

    @Unique
    private Pair<ItemStack, BlockPos> getDrop(Pair<ItemStack, BlockPos> pair) {

        ItemStack drop = pair.getFirst();

        if (drop.isOf(Items.COBBLESTONE)) {
            int count = random.nextInt(5) - 2;
            if (count > 0) {
                drop = new ItemStack(Items.GRAVEL, count * drop.getCount());
            }
        } else if (drop.isOf(Items.SANDSTONE)) {
            int count = random.nextInt(9) - 5;
            if (count > 0) {
                drop = new ItemStack(Items.SAND, drop.getCount() * count);
            }
        } else if (drop.isOf(Items.RED_SANDSTONE)) {
            int count = random.nextInt(9) - 5;
            if (count > 0) {
                drop = new ItemStack(Items.RED_SAND, drop.getCount() * count);
            }
        }
        return new Pair<>(drop, pair.getSecond());
    }

}
