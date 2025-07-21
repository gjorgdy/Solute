package nl.gjorgdy.solute.mixins.purpur;

import net.minecraft.server.network.ServerPlayerEntity;
import nl.gjorgdy.solute.Solute;
import nl.gjorgdy.solute.interfaces.ServerPlayerEntityInterface;
import nl.gjorgdy.solute.modules.Purpur;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements ServerPlayerEntityInterface {

    @Unique
    private ServerPlayerEntity playerEntity = (ServerPlayerEntity) (Object) this;

    @Unique
    private int elevatorCooldown = 0;

    @Override
    public void solute$setElevatorCooldown() {
        elevatorCooldown = 10;
    }

    @Override
    public boolean solute$isOnElevatorCooldown() {
        return elevatorCooldown != 0;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        // early return if module disabled
        if (!Solute.CONFIG.purpurModule.enabled) return;
        // early return if module disabled
        if (elevatorCooldown > 0) elevatorCooldown--;
    }

    @Inject(method = "jump", at = @At("RETURN"))
    public void onJump(CallbackInfo ci) {
        // early return if module disabled
        if (!Solute.CONFIG.purpurModule.enabled) return;
        // early return if module disabled
        Purpur.up(playerEntity);
    }

}
