package nl.gjorgdy.vanillaplus.modules;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class FernFire {


    public static void burn(PlayerEntity player, ItemStack plant, ItemStack lighter) {
        if (!player.isCreative())
            plant.setCount(plant.getCount() - 1);
        lighter.damage(1, player.getWorld().random, (ServerPlayerEntity) player);
        player.addStatusEffect(
                new StatusEffectInstance(StatusEffects.NAUSEA, 160, 0, false, false, false)
        );
        player.addStatusEffect(
                new StatusEffectInstance(StatusEffects.SLOWNESS, 160, 1, false, false, false)
        );
        player.getWorld().sendEntityStatus(player, (byte)60);
    }

}
