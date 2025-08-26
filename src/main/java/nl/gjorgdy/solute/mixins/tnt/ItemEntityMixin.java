package nl.gjorgdy.solute.mixins.tnt;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import nl.gjorgdy.solute.Solute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;onItemEntityDestroyed(Lnet/minecraft/entity/ItemEntity;)V"))
    public void onDamage(ItemStack stack, ItemEntity entity, @Local(argsOnly = true) DamageSource source) {
        if (Solute.CONFIG.tntModule.enabled && (source.isOf(DamageTypes.EXPLOSION) || source.isOf(DamageTypes.PLAYER_EXPLOSION))) {
            var crushed = crush(entity.getRandom(), stack);
            if (crushed.isPresent()) {
                Block.dropStack(entity.getWorld(), entity.getBlockPos(), crushed.get());
                return;
            }
        }
        stack.onItemEntityDestroyed(entity);
    }

    @Unique
    private static Optional<ItemStack> crush(Random random, ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == Items.BLACK_CONCRETE) {
            var stack = Items.BLACK_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.LIGHT_BLUE_CONCRETE) {
            var stack = Items.LIGHT_BLUE_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.BLUE_CONCRETE) {
            var stack = Items.BLUE_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.CYAN_CONCRETE) {
            var stack = Items.CYAN_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.LIME_CONCRETE) {
            var stack = Items.LIME_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.GREEN_CONCRETE) {
            var stack = Items.GREEN_CONCRETE.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.YELLOW_CONCRETE) {
            var stack = Items.YELLOW_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.ORANGE_CONCRETE) {
            var stack = Items.ORANGE_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.BROWN_CONCRETE) {
            var stack = Items.BROWN_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.RED_CONCRETE) {
            var stack = Items.RED_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.PURPLE_CONCRETE) {
            var stack = Items.PURPLE_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.MAGENTA_CONCRETE) {
            var stack = Items.MAGENTA_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.PINK_CONCRETE) {
            var stack = Items.PINK_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.WHITE_CONCRETE) {
            var stack = Items.WHITE_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.LIGHT_GRAY_CONCRETE) {
            var stack = Items.LIGHT_GRAY_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        if (item == Items.GRAY_CONCRETE) {
            var stack = Items.GRAY_CONCRETE_POWDER.getDefaultStack();
            stack.setCount(itemStack.getCount());
            return Optional.of(stack);
        }
        // convert
        if (item == Items.SANDSTONE) {
            var stack = Items.SAND.getDefaultStack();
            stack.setCount(getAmount(random, itemStack.getCount()));
            return Optional.of(stack);
        }
        if (item == Items.RED_SANDSTONE) {
            var stack = Items.RED_SAND.getDefaultStack();
            stack.setCount(getAmount(random, itemStack.getCount()));
            return Optional.of(stack);
        }
        if (item == Items.COBBLESTONE) {
            var stack = Items.GRAVEL.getDefaultStack();
            stack.setCount(getAmount(random, itemStack.getCount()));
            return Optional.of(stack);
        }
        return Optional.empty();
    }

    @Unique
    private static int getAmount(Random random, int input) {
        // calculate amount
        int amount  = 0;
        for (int i = 0; i < input; i++) {
            amount += random.nextInt(2) + 1;
        }
        return amount;
    }

}
