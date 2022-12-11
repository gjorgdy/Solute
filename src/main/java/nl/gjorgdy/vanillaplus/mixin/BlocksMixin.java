package nl.gjorgdy.vanillaplus.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import nl.gjorgdy.vanillaplus.VanillaPlus;
import nl.gjorgdy.vanillaplus.blocks.PurpurBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    @Redirect(
        slice = @Slice(
            from = @At(
                value = "CONSTANT",
                args= {
                        "stringValue=purpur_block"
                },
                ordinal = 0
            )
        ),
        at = @At(
            value = "NEW",
            target = "Lnet/minecraft/block/Block;*",
            ordinal = 0
        ),
        method = "<clinit>"
    )
    private static Block purpur(AbstractBlock.Settings settings) {
        return new PurpurBlock(settings);
    }

}

