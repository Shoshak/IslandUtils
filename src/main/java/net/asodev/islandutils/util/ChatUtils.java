package net.asodev.islandutils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Optional;

import static net.asodev.islandutils.IslandConstants.CHAT_PREFIX;

public class ChatUtils {
    public static String translate(String s) {
        return s.replaceAll("&", "§");
    }

    public static void send(String s) {
        send(Text.literal(translate(CHAT_PREFIX + " " + s)));
    }

    public static void send(Text component) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            throw new IllegalStateException("Player is null, could not send message");
        }
        client.player.sendMessage(component);
    }

    public static TextColor parseColor(String hex) {
        Optional<TextColor> result = TextColor.parse(hex).result();
        return result.orElse(null);
    }

}
