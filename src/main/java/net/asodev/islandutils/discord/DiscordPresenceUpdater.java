package net.asodev.islandutils.discord;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.events.OptionChangeEvent;
import net.asodev.islandutils.events.StateChangeEvent;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.state.IslandState;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class DiscordPresenceUpdater {
    private final Logger logger = LoggerFactory.getLogger(DiscordPresence.class);

    private final Instant started = Instant.now();
    private boolean discordPresence;

    public DiscordPresenceUpdater() {
        this.discordPresence = IslandConfig.HANDLER.instance().discordPresence;

        StateChangeEvent.EVENT.register(state -> {
            updateActivity(state);
            return ActionResult.PASS;
        });

        OptionChangeEvent.EVENT.register(() -> {
            this.discordPresence = IslandConfig.HANDLER.instance().discordPresence;
            return ActionResult.PASS;
        });
    }

    public void updateActivity(IslandState state) {
        if (!discordPresence) return;
        Activity activity = state.getActivity();
        activity.timestamps().setStart(started);
        try {
            DiscordPresence
                    .getInstance()
                    .getCoreInstance()
                    .activityManager()
                    .updateActivity(activity);
        } catch (Exception e) {
            logger.error("{}", "Could not update activity", e);
        }
    }
}
