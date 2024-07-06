package nl.gjorgdy.vanillaplus.mixins.ender_elevator;

import net.minecraft.server.network.ServerPlayerEntity;
import nl.gjorgdy.vanillaplus.interfaces.ServerPlayerEntityInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerEntityInterface {

    @Unique
    private int elevatorCooldown = 0;

    @Override
    public void VanillaPlus$setElevatorCooldown() {
        elevatorCooldown = 5;
    }

    @Override
    public boolean VanillaPlus$isOnElevatorCooldown() {
        return elevatorCooldown != 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (elevatorCooldown > 0) elevatorCooldown--;
    }

}
