package nl.gjorgdy.solute.mixins.bed;

import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SleepManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.ServerWorldProperties;
import nl.gjorgdy.solute.Solute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Redirect(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/SleepManager;canSkipNight(I)Z"))
    private boolean injected(SleepManager instance, int percentage) {
        // early return if module disabled
        if (!Solute.CONFIG.bedModule.enabled) return instance.canSkipNight(percentage);
        // early return if module disabled
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

                if (!doWeatherCycle) return false;

                int thunderTime = worldProperties.getThunderTime();
                if (serverWorld.isThundering() && thunderTime > 0) {
                    int _thunderTime = Math.max(0, thunderTime - (timeDelta * 2));
                    worldProperties.setThunderTime(_thunderTime);
                    if (_thunderTime == 0) worldProperties.setThundering(false);
                }

                int rainTime = worldProperties.getRainTime();
                if (serverWorld.isRaining() && rainTime > 0) {
                    int _rainTime = Math.max(0, rainTime - (timeDelta * 20));
                    worldProperties.setRainTime(_rainTime);
                    if (_rainTime == 0) worldProperties.setRaining(false);
                }
            } else {
                wakeSleepingPlayers();
            }
        }
        return false;
    }

    @Inject(method = "tick", at=@At("HEAD"))
    private void tickTime(CallbackInfo ci) {
        if (!Solute.CONFIG.bedModule.enabled) return;

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
