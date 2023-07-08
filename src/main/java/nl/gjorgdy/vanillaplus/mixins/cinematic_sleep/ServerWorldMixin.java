package nl.gjorgdy.vanillaplus.mixins.cinematic_sleep;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SleepManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    @Final
    private ServerWorldProperties worldProperties;
    ServerWorld serverWorld = (ServerWorld) (Object) this;

    @Redirect(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/SleepManager;canSkipNight(I)Z"))
    private boolean injected(SleepManager instance, int percentage) {
        boolean doDayLightCycle = server.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE);
        boolean doWeatherCycle = serverWorld.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE);

        int playersInWorld = serverWorld.getPlayers().size();
        int playersSleeping = instance.getSleeping();
        float playersSleepingPercentage = (float) playersSleeping / playersInWorld;

        long worldTime = serverWorld.getTimeOfDay();
        long dayTime = worldTime % 24000L;

        if (playersSleeping >= 1) {
            if ((doDayLightCycle && dayTime >= 12500L) || (doWeatherCycle && serverWorld.isThundering())) {
                int timeDelta = getTimeDelta(playersSleepingPercentage);
                setTime(worldTime + timeDelta);
                if (serverWorld.isThundering()) {
                    int thunderTime = Math.max(0, worldProperties.getThunderTime() - (timeDelta * 2));
                    worldProperties.setThunderTime(thunderTime);
                    if (thunderTime == 0) resetWeather();
                }
            } else {
                wakeSleepingPlayers();
                if (doWeatherCycle && serverWorld.isRaining()) {
                    resetWeather();
                }
            }
        }
        return false;
    }

    private int getTimeDelta(float sleepingPercentage) {
        return Math.max(25, Math.round(50 * sleepingPercentage));
    }

    private void setTime(long newTime) {
        serverWorld.setTimeOfDay(newTime);
        server.getPlayerManager().sendToDimension(
                new WorldTimeUpdateS2CPacket(worldProperties.getTime(), worldProperties.getTimeOfDay(), true
                ), serverWorld.getRegistryKey()
        );
    }

    @Shadow
    protected abstract void resetWeather();
    @Shadow
    protected abstract void wakeSleepingPlayers();
}
