package net.asodev.islandutils.state;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.util.sounds.MCCSound;

import java.util.Optional;

public interface IslandState {
    String name();

    String id();

    Activity getActivity();

    Optional<MCCSound> music();
}
