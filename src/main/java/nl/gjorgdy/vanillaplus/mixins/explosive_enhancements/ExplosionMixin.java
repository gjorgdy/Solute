package nl.gjorgdy.vanillaplus.mixins.explosive_enhancements;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;

@Mixin(Explosion.class)
public class ExplosionMixin {

    private final Random random = new Random();

    @Redirect(method = "affectWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getDroppedStacks(Lnet/minecraft/loot/context/LootContextParameterSet$Builder;)Ljava/util/List;"))
    private List affectWorld(BlockState instance, LootContextParameterSet.Builder builder) {
        return instance.getDroppedStacks(builder).stream().map((stack) -> getDrop(instance, stack)).toList();
    }

    public ItemStack getDrop(BlockState block, ItemStack drop) {
        if (block.isOf(Blocks.COBBLESTONE) && drop.isOf(Items.COBBLESTONE)) {
            int count = random.nextInt(5) - 2;
            if (count > 0) {
                return new ItemStack(Items.GRAVEL, count * drop.getCount());
            }
        }
        else if (block.isOf(Blocks.SANDSTONE) && drop.isOf(Items.SANDSTONE)) {
            int count = random.nextInt(9) - 4;
            if (count > 0) {
                return new ItemStack(Items.SAND, drop.getCount() * count);
            }
        }
        else if (block.isOf(Blocks.RED_SANDSTONE) && drop.isOf(Items.RED_SANDSTONE)) {
            int count = random.nextInt(9) - 4;
            if (count > 0) {
                return new ItemStack(Items.RED_SAND, drop.getCount() * count);
            }
        }
        return drop;
    }

}
