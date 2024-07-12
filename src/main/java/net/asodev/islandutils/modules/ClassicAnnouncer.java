package net.asodev.islandutils.modules;

import net.asodev.islandutils.options.IslandOptions;
import net.asodev.islandutils.util.MusicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.network.chat.Component.literal;

public class ClassicAnnouncer {
    private long lastTrapTimestamp;

    private final Style trapStyle;

    public ClassicAnnouncer(TextColor trapColor) {
        this.trapStyle = Style.EMPTY.withColor(trapColor);
    }

    public void handleTrap(ClientboundSetSubtitleTextPacket clientboundSetSubtitleTextPacket, CallbackInfo ci) {
        if (!IslandOptions.getClassicHITW().isClassicHITW()) return;
        Component subtitle = clientboundSetSubtitleTextPacket.text();

        boolean isTrap = subtitle
                .toFlatList()
                .stream()
                .map(Component::getStyle)
                .anyMatch(style -> {
                    TextColor color = style.getColor();
                    if (color == null) return false;
                    return color.equals(trapStyle.getColor());
                });
        if (!isTrap) return;

        String trapName = subtitle.getString();

        String changedTrapName = changeName(trapName);
        if (!(changedTrapName.equals(trapName))) {
            Minecraft.getInstance().gui.setSubtitle(literal(changedTrapName).withStyle(trapStyle));
            ci.cancel(); // Cancel minecraft executing further
        }

        long timestamp = System.currentTimeMillis(); // This just ensures we don't play the sound twice
        if ((timestamp - lastTrapTimestamp) < 50) return; // 50ms delay
        lastTrapTimestamp = timestamp;

        trapName = trapName.replaceAll("([ \\-!])","").toLowerCase();
        ResourceLocation soundLocation = new ResourceLocation("island", "announcer." + trapName);
        Minecraft.getInstance().getSoundManager().play(MusicUtil.createSoundInstance(soundLocation));
    }

    private static String changeName(String originalTrap) {
        return switch (originalTrap) {
            case "Feeling Hot" -> "What in the Blazes";
            case "Hot Coals" -> "Feeling Hot";
            case "Blast-Off" -> "Kaboom";
            case "Pillagers" -> "So Lonely";
            case "Leg Day" -> "Molasses";
            case "Snowball Fight" -> "Jack Frost";
            default -> originalTrap;
        };
    }

}
