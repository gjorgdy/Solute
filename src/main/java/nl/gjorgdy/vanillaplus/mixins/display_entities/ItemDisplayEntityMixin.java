package nl.gjorgdy.vanillaplus.mixins.display_entities;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.inventory.StackReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DisplayEntity.ItemDisplayEntity.class)
public interface ItemDisplayEntityMixin extends DisplayEntityMixin {

    @Accessor
    StackReference getStackReference();

    @Invoker
    void callSetTransformationMode(ModelTransformationMode transformationMode);

}
