package net.asodev.islandutils.state.states;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.state.IslandState;
import net.asodev.islandutils.util.MusicUtil;
import net.asodev.islandutils.util.sounds.MCCSound;
import net.asodev.islandutils.util.sounds.SoundBuilder;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class HITW implements IslandState {
    @Override
    public String name() {
        return "Hole in the Wall";
    }

    @Override
    public String id() {
        return "hole_in_the_wall";
    }

    // TODO
    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public Optional<MCCSound> music() {
        IslandConfig config = IslandConfig.HANDLER.instance();
        if (!config.HITWMusic) return Optional.empty();

        Identifier location;
        if (config.classicHITWMusic) {
            location = MusicUtil.getMusicLocation("classic_hitw");
        } else {
            location = MusicUtil.getMusicLocation("hitw");
        }

        MCCSound music = new SoundBuilder()
                .setIdentifier(location)
                .setMusic()
                .build();
        return Optional.of(music);
    }
}
