package net.asodev.islandutils.state;

import de.jcm.discordgamesdk.activity.Activity;

public interface IslandState {
    String name();
    String id();
    Activity getActivity();
}
