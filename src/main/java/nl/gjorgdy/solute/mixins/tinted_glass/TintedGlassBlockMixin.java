package nl.gjorgdy.solute.mixins.tinted_glass;

import net.minecraft.block.TintedGlassBlock;
import net.minecraft.block.TransparentBlock;
import nl.gjorgdy.solute.Solute;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TintedGlassBlock.class)
public class TintedGlassBlockMixin extends TransparentBlock {

    public TintedGlassBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public float getBlastResistance() {
        return Solute.CONFIG.tintedModule.enabled ? 1200.0F : super.getBlastResistance();
    }
}
