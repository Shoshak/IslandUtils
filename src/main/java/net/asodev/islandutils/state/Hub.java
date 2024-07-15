package net.asodev.islandutils.state;

import de.jcm.discordgamesdk.activity.Activity;

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
//        activity.setState("");
//        activity.timestamps().setEnd(Instant.ofEpochSecond(0));
        return activity;
    }
}
