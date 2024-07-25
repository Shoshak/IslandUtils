package net.asodev.islandutils.state.states;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.state.IslandState;
import net.asodev.islandutils.util.MusicUtil;
import net.asodev.islandutils.util.sounds.MCCSound;
import net.asodev.islandutils.util.sounds.SoundBuilder;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class TGTTOS implements IslandState {
    @Override
    public String name() {
        return "TGTTOS";
    }

    @Override
    public String id() {
        return "tgttos";
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public Optional<MCCSound> music() {
        IslandConfig config = IslandConfig.HANDLER.instance();
        if (!config.TGTTOSMusic) return Optional.empty();
        Identifier location;
        if (config.TGTTOSToTheDome) {
            location = MusicUtil.getMusicLocation("to_the_dome");
        } else {
            location = MusicUtil.getMusicLocation("tgttos");
        }
        float pitch;
        if (config.TGTTOSDoubleTime) {
            pitch = 1.2f;
        } else {
            pitch = 1.0f;
        }
        MCCSound music = new SoundBuilder()
                .setIdentifier(location)
                .setMusic()
                .setPitch(pitch)
                .build();
        return Optional.of(music);
    }
}
