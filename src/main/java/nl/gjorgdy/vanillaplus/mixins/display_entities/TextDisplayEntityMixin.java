package nl.gjorgdy.vanillaplus.mixins.display_entities;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DisplayEntity.TextDisplayEntity.class)
public interface TextDisplayEntityMixin extends DisplayEntityMixin {

    @Invoker
    void callSetText(Text text);

    @Invoker
    void callSetBackground(int background);

    @Invoker
    void callSetDisplayFlags(byte flags);

    @Invoker
    void callSetLineWidth(int lineWidth);

}
