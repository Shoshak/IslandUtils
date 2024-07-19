package net.asodev.islandutils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static net.asodev.islandutils.IslandConstants.CHAT_PREFIX;

public class ChatUtils {
    private final Logger LOGGER = LoggerFactory.getLogger("IslandUtils");

    public static String translate(String s) {
        return s.replaceAll("&", "§");
    }

    public static void send(String s) {
        send(Text.literal(translate(CHAT_PREFIX + " " + s)));
    }

    public static void debug(String s, Object... args) {
        debug(String.format(s, args));
    }

    public static void debug(String s) {
        if (!IslandOptions.getMisc().isDebugMode()) {
            LOGGER.info("[DEBUG] {}", s);
            return;
        }
        send(Component.literal("[IslandUtils] " + s).withStyle(ChatFormatting.GRAY));
    }

    public static void send(Text component) {
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        client.player.sendMessage(component);
    }

    public static TextColor parseColor(String hex) {
        Optional<TextColor> result = TextColor.parse(hex).result();
        return result.orElse(null);
    }

}
