package nl.gjorgdy.vanillaplus.mixins.smooth_sleep;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SleepManager;
import net.minecraft.util.math.random.Random;
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
    public final Random random = Random.create();
    ServerWorld serverWorld = (ServerWorld) (Object) this;

    @Redirect(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/SleepManager;canSkipNight(I)Z"))
    private boolean injected(SleepManager instance, int percentage) {
        boolean doDayLightCycle = serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE);
        boolean doWeatherCycle = serverWorld.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE);

        int playersInWorld = serverWorld.getPlayers().size();
        int playersSleeping = instance.getSleeping();
        float playersSleepingPercentage = (float) playersSleeping / playersInWorld;

        long worldTime = serverWorld.getTimeOfDay();
        long dayTime = worldTime % 24000L;

        if (playersSleeping >= 1) {
            if ((doDayLightCycle && dayTime >= 12750L && dayTime < 23250L) || (doWeatherCycle && serverWorld.isThundering())) {
                int timeDelta = getTimeDelta(playersSleepingPercentage);
                setTime(worldTime + timeDelta);

                int thunderTime = worldProperties.getThunderTime();
                if (serverWorld.isThundering() && thunderTime > 0) {
                    int _thunderTime = Math.max(0, thunderTime - (timeDelta * 2));
                    worldProperties.setThunderTime(_thunderTime);
                    if (_thunderTime == 0) worldProperties.setThundering(false);
                }
            } else {
                wakeSleepingPlayers();
                if (doWeatherCycle && (serverWorld.isRaining() || serverWorld.isThundering())) {
                    int _rainTime = Math.min(500, worldProperties.getRainTime());
                    worldProperties.setRainTime(_rainTime);
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
