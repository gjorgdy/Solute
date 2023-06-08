package nl.gjorgdy.vanillaplus.mixins.display_entities;

import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.AffineTransformation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DisplayEntity.class)
public interface DisplayEntityMixin {

    @Invoker
    void callSetTransformation(AffineTransformation transformation);

    @Invoker
    void callSetDIsplayWidth(float width);

    @Invoker
    void callSetDisplayHeight(float height);

    @Invoker
    void callSetBillboardMode(DisplayEntity.BillboardMode billboardMode);

    @Invoker
    void callSetBrightness(@Nullable Brightness brightness);

    @Invoker
    void callSetViewRange(float viewRange);

}
