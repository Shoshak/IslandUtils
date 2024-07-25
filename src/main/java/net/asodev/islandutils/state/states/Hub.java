package net.asodev.islandutils.state.states;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.state.IslandState;
import net.asodev.islandutils.util.sounds.MCCSound;

import java.util.Optional;

public class Hub implements IslandState {
    private static final String name = "Hub";
    private static final String details = "In the hub";

    @Override
    public String name() {
        return "Hub";
    }

    @Override
    public String id() {
        return "";
    }

    @Override
    public Activity getActivity() {
        Activity activity = new Activity();
        activity.setDetails(details);
        activity.assets().setLargeImage(name.toLowerCase());
        activity.assets().setLargeText(name);
        return activity;
    }

    @Override
    public Optional<MCCSound> music() {
        return Optional.empty();
    }
}
