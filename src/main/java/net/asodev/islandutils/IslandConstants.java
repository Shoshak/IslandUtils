package net.asodev.islandutils;

import net.asodev.islandutils.util.ChatUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class IslandConstants {
    public static final Path ISLAND_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("islandutils_resources");
    public static final Style MCC_HUD_FONT = Style.EMPTY.withFont(new Identifier("mcc", "hud"));
    public static final Text CANT_USE_DEBUG_ERROR = Text.literal(ChatUtils.translate("You must be in debug mode to use the debug command."));
    public static final Style ICONS_FONT_STYLE = Style.EMPTY.withColor(Formatting.WHITE).withFont(new Identifier(
            "island", "icons"));
    public static final String CHAT_PREFIX = "&b[&eIslandUtils&b]";
}
