package nl.gjorgdy.solute.utils;

import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class PlayerUtils {

    public static void playDirectSound(ServerPlayerEntity player, SoundEvent soundEvent, SoundCategory soundCategory) {
        playDirectSound(player, soundEvent, soundCategory, 1.0f, 1.0f);
    }

    public static void playDirectSound(ServerPlayerEntity player, SoundEvent soundEvent, SoundCategory soundCategory, float volume, float pitch) {
        PlaySoundFromEntityS2CPacket packet = new PlaySoundFromEntityS2CPacket(
                Registries.SOUND_EVENT.getEntry(soundEvent),
                soundCategory,
                player, // The entity "emitting" the sound (can be the player themselves)
                volume,
                pitch,
                player.getRandom().nextLong() // Seed
        );
        player.networkHandler.sendPacket(packet);
    }

}
