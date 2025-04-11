package nl.gjorgdy.solute.mixins.bed;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SleepManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Unique
    ServerWorld serverWorld = (ServerWorld) (Object) this;

    @Shadow
    @Final
    private SleepManager sleepManager;
    @Shadow
    @Final
    private MinecraftServer server;
    @Shadow
    @Final
    private ServerWorldProperties worldProperties;

    @Shadow
    protected abstract void wakeSleepingPlayers();

    @Shadow public abstract void playSound(@Nullable Entity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed);

    @Shadow @Final private List<ServerPlayerEntity> players;

    @Redirect(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/SleepManager;canSkipNight(I)Z"))
    private boolean injected(SleepManager instance, int percentage) {
        boolean doDayLightCycle = serverWorld.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE);
        boolean doWeatherCycle = serverWorld.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE);

        int playersInWorld = serverWorld.getPlayers().size();
        int playersSleeping = instance.getSleeping();
        float playersSleepingPercentage = (float) playersSleeping / playersInWorld;

        long worldTime = serverWorld.getTimeOfDay();

        if (playersSleeping >= 1) {
            if (doDayLightCycle && sleepManager.canSkipNight((int) playersSleepingPercentage)
                    || (doWeatherCycle && serverWorld.isThundering())) {
                int timeDelta = getTimeDelta(playersSleepingPercentage);

                new Thread(() -> {
                    for (int i = 0; i < 10; i++) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ignore) {
                        }
                        setTime(worldTime + ((long) timeDelta * i));
                    }
                }).start();

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

    @Inject(method = "tick", at=@At("HEAD"))
    private void tickTime(CallbackInfo ci) {
        long dayTime = serverWorld.getTimeOfDay() % 24000L;
        if (dayTime > 23600) {
            int day = (int) (serverWorld.getTime() / 24000L);
            String dayString = "-- Day " + day + " --";
            int i = (int) ((dayTime - 23600) / 5);
            if (dayTime % 5 == 0 && i <= dayString.length()) {
                server.getPlayerManager().getPlayerList().forEach(player -> {
                    serverWorld.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.AMBIENT, i, i);
                    player.sendMessage(
                        Text.of(dayString.substring(0, i)),
                        true
                    );
                });
            }
        }
    }

    @Unique
    private int getTimeDelta(float sleepingPercentage) {
        return Math.max(5, Math.round(10 * sleepingPercentage));
    }

    @Unique
    private void setTime(long newTime) {
        serverWorld.setTimeOfDay(newTime);
        server.getPlayerManager().sendToDimension(
                new WorldTimeUpdateS2CPacket(worldProperties.getTime(), worldProperties.getTimeOfDay(), true
                ), serverWorld.getRegistryKey()
        );
    }

}
