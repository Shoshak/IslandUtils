package net.asodev.islandutils.state.states;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.state.IslandState;
import net.asodev.islandutils.util.MusicUtil;
import net.asodev.islandutils.util.sounds.MCCSound;
import net.asodev.islandutils.util.sounds.SoundBuilder;

import java.util.Optional;

public class PKWD implements IslandState {
    @Override
    public String name() {
        return "";
    }

    @Override
    public String id() {
        return "";
    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public Optional<MCCSound> music() {
        if (!IslandConfig.HANDLER.instance().PKWMusic) return Optional.empty();
        return Optional.of(new SoundBuilder()
                .setIdentifier(MusicUtil.getMusicLocation("parkour_warrior"))
                .setMusic()
                .build());
    }
}
