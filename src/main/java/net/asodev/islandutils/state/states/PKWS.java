package net.asodev.islandutils.state.states;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.state.IslandState;
import net.asodev.islandutils.util.MusicUtil;
import net.asodev.islandutils.util.sounds.MCCSound;
import net.asodev.islandutils.util.sounds.SoundBuilder;

import java.util.Optional;

public class PKWS implements IslandState {
    private final boolean skipOvertime;

    public PKWS(boolean skipOvertime) {
        this.skipOvertime = skipOvertime;
    }

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
        if (skipOvertime) return Optional.empty();
        if (!IslandConfig.HANDLER.instance().PKWSMusic) return Optional.empty();
        return Optional.of(new SoundBuilder()
                .setIdentifier(MusicUtil.getMusicLocation("parkour_warrior"))
                .setMusic()
                .build());
    }
}
