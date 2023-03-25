package nl.gjorgdy.vanillaplus.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetherPortal.class)
public class NetherPortalMixin {

    @Shadow
    @Final
    private static final AbstractBlock.ContextPredicate IS_VALID_FRAME_BLOCK =
        (state, world, pos) -> (state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.CRYING_OBSIDIAN));
}
