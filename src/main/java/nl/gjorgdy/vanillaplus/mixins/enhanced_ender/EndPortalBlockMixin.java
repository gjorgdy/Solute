package nl.gjorgdy.vanillaplus.mixins.enhanced_ender;

import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {

    @Redirect(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;moveToWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;"))
    public Entity teleportVehicle(Entity instance, ServerWorld destination) {
        // Get all passengers
        List<Entity> passengers = instance.getPassengerList();
        // Dismount and teleport passengers
        for (Entity passenger : passengers) {
            passenger.dismountVehicle();
            passenger.moveToWorld(destination);
        }
        // Teleport vehicle
        instance.moveToWorld(destination);

        return instance;
    }

}
