package net.asodev.islandutils.util;

import net.asodev.islandutils.events.PlaySoundEvent;
import net.asodev.islandutils.events.StateChangeEvent;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.state.IslandState;
import net.asodev.islandutils.util.sounds.FadingDown;
import net.asodev.islandutils.util.sounds.MCCSound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MusicUtil {
    private final Logger logger = LoggerFactory.getLogger(MusicUtil.class);
    private static final List<String> whereMusicEnds = List.of(
            "games.parkour_warrior.mode_swap",
            "games.parkour_warrior.restart_course",
            "games.global.timer.round_end"
    );

    private MCCSound currentlyPlayingSound;
    private IslandState currentGame;

    public MusicUtil() {
        StateChangeEvent.EVENT.register(state -> {
            currentGame = state;
            stopMusicInstant();
        });
        PlaySoundEvent.EVENT.register(this::onSound);
    }

    private void onSound(SoundEvent sound) {
        String path = sound.getId().getPath();
        if (whereMusicEnds.contains(path)) {
            stopMusicFade();
        }
    }

    private void startMusic() {
        currentGame.music().ifPresent(music -> {
            IslandConfig config = IslandConfig.HANDLER.instance();
            if (config.classicHITWMusic) { // i guess i'll leave this here lmao
                ChatUtils.send(Text
                        .literal("Now playing: ")
                        .setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                        .append(Text
                                .literal("Spacewall - Taylor Grover")
                                .setStyle(Style.EMPTY.withColor(Formatting.AQUA))
                        )
                );
            }

            logger.debug("[MusicUtil] Starting: {}", music.getId().getPath());
            stopMusicFade();

            //        MCCSoundInstance instance = new MCCSoundInstance(
            //                SoundEvent.createVariableRangeEvent(location),
            //                IslandSoundCategories.GAME_MUSIC,
            //                clientboundCustomSoundPacket.getVolume(),
            //                pitch,
            //                RandomSource.create(clientboundCustomSoundPacket.getSeed()),
            //                false,
            //                0,
            //                SoundInstance.Attenuation.LINEAR,
            //                clientboundCustomSoundPacket.getX(),
            //                clientboundCustomSoundPacket.getY(),
            //                clientboundCustomSoundPacket.getZ(),
            //                false);
            MinecraftClient.getInstance().getSoundManager().play(music);
            currentlyPlayingSound = music;
        });
    }

    public void stopMusicInstant() {
        if (currentlyPlayingSound == null) return;
        currentlyPlayingSound.shouldStop(true);
    }

    public void stopMusicFade() {
        if (currentlyPlayingSound == null) return;
        currentlyPlayingSound.setFading(new FadingDown(100, 20));
        currentlyPlayingSound.shouldStop(true);
//        currentlyPlayingSound = null;
//        Minecraft.getInstance().getSoundManager().stop(location, IslandSoundCategories.GAME_MUSIC);
    }

    public boolean isOvertimePlaying() {
        Identifier overtimeIntroMusic = Identifier.of("music.global", "overtime_intro_music");
        Identifier overtimeLoopMusic = Identifier.of("music.global", "overtime_loop_music");
        if (overtimeIntroMusic == null || overtimeLoopMusic == null) {
            throw new IllegalStateException("Could not find overtime music");
        }
        return isPlayingSound(overtimeIntroMusic) || isPlayingSound(overtimeLoopMusic);
    }

    public boolean isPlayingSound(Identifier sound) {
        SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        return soundManager
                .getKeys()
                .stream()
                .anyMatch(s -> s.equals(sound));
    }

    public static Identifier getMusicLocation(String name) {
        return new Identifier("island", "island.music." + name);
    }
}
