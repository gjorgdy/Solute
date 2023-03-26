package nl.gjorgdy.vanillaplus.mixins.ender_elevator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import nl.gjorgdy.vanillaplus.interfaces.PlayerEntityInterface;
import nl.gjorgdy.vanillaplus.modules.EnderElevator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityInterface {

    public int elevatorCooldown = 0;
    private final PlayerEntity player = (PlayerEntity) (Object) this;

    /**
     * Run elevator upwards when player jumps
     * @param ci
     */
    @Inject(at = @At("HEAD"), method = "jump")
    private void onJump(CallbackInfo ci) {
        if (player.getWorld().getRegistryKey() != World.END) {
            EnderElevator.moveVertical(player, true);
        }
    }

    /**
     * Run elevator downwards when player sneaks
     * @param ci
     */
    @Inject(at = @At("TAIL"), method = "tick")
    private void onSneak(CallbackInfo ci) {
        // Start sneaking
        if (vanillaPlus$checkCooldown() && player.isSneaking() && player.getWorld().getRegistryKey() != World.END) {
            EnderElevator.moveVertical(player, false);
            vanillaPlus$resetCooldown();
        }
    }

    @Override
    synchronized public boolean vanillaPlus$checkCooldown() {
        if (elevatorCooldown <= 0) {
            return true;
        } else {
            elevatorCooldown -= 1;
            return false;
        }
    }

    @Override
    synchronized public void vanillaPlus$resetCooldown() {
        elevatorCooldown = 10;
    }
}
