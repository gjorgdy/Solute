package nl.gjorgdy.solute.mixins.cauldron;

import net.minecraft.block.Block;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nl.gjorgdy.solute.interfaces.ConcretePowderBlockInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ConcretePowderBlock.class)
public class ConcretePowderBlockMixin implements ConcretePowderBlockInterface {

    @Shadow @Final private Block hardenedState;

    @Override
    public Block solute$getHardenedState() {
        return this.hardenedState;
    }

    @Override
    public Item solute$getHardenedStateItem() {
        return solute$getHardenedState().asItem();
    }

    @Override
    public ItemStack solute$getHardenedStateDefaultItemStack() {
        return solute$getHardenedStateItem().getDefaultStack();
    }

}
