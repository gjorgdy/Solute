package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import nl.gjorgdy.vanillaplus.blocks.IndestructibleBlock;
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
                        "stringValue=lodestone"
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
    private static Block lodestone(AbstractBlock.Settings settings) {
        return new IndestructibleBlock(settings);
    }

}

