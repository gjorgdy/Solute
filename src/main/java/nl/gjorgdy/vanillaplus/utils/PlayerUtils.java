package nl.gjorgdy.vanillaplus.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PlayerUtils {

    public static void sendError(ServerPlayerEntity player, MutableText text) {
        sendActionBar(player, text.formatted(Formatting.RED));
    }

    public static void sendError(ServerPlayerEntity player, String string) {
        sendActionBar(player, Text.literal(string).formatted(Formatting.RED));
    }

    public static void sendActionBar(ServerPlayerEntity player, String string, Formatting color) {
        sendActionBar(player, Text.literal(string).formatted(color));
    }

    public static void sendActionBar(ServerPlayerEntity player, Text text) {
        player.sendMessage(text, true);
    }

}
