package nl.gjorgdy.solute.mixins.enchantment;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import nl.gjorgdy.solute.utils.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Unique
    PlayerEntity playerEntity = (PlayerEntity) (Object) this;
    @Unique
    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) (Object) this;

    @Inject(method = "getBlockBreakingSpeed", at= @At(value = "RETURN"), cancellable = true)
    public void getBlockBreakingSpeed(CallbackInfoReturnable<Float> cir, @Local float f) {

    }

}
