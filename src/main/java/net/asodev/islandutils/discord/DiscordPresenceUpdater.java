package net.asodev.islandutils.discord;

import de.jcm.discordgamesdk.activity.Activity;
import net.asodev.islandutils.IslandUtilsEvents;
import net.asodev.islandutils.options.IslandOptions;
import net.asodev.islandutils.options.categories.DiscordOptions;
import net.asodev.islandutils.state.IslandState;
import net.asodev.islandutils.state.IslandStateHolder;
import net.asodev.islandutils.state.StateChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DiscordPresenceUpdater {
    // EVERYTHING in this file that interacts with discord is in a try/catch
    // Discord GameSDK sometimes likes to not work whenever you ask it to do something
    // So we have to be sure we don't crash when that happens.

    // dear contributors:
    // i am sorry.
    private final Logger logger = LoggerFactory.getLogger(DiscordPresence.class);

    private UUID timeLeftBossBar;
    private Instant started;


    public DiscordPresenceUpdater(IslandStateHolder stateHolder) {
        if (!IslandOptions.getDiscord().discordPresence) return;
        this.started = Instant.now();

        StateChangeEvent.EVENT.register(this::updateActivity);
    }

    public void updateTimeLeft(Long endTimestamp) {
        if (activity == null) return;
        if (!IslandOptions.getDiscord().showTimeRemaining || !IslandOptions.getDiscord().showGame) return;

        try {
            if (endTimestamp != null) activity.timestamps().setEnd(Instant.ofEpochMilli(endTimestamp));
            else activity.timestamps().setEnd(Instant.ofEpochSecond(0));
        } catch (Exception e) { e.printStackTrace(); }
        updateActivity();
    }

//    public void updatePlace(Game game) {
//        if (activity == null) return;
//        if (!IslandOptions.getDiscord().showGame) return;
//
//        try {
//            activity.assets().setLargeImage(game.name().toLowerCase());
//            activity.assets().setLargeText(game.getName());
//            activity.assets().setSmallImage("mcci");
//
//            if (game != Game.HUB)
//                activity.setDetails("Playing " + game.getName());
//            else {
//                activity.setDetails("In the Hub");
//                REMAIN_STATE = null;
//                ROUND_STATE = null;
//                activity.setState("");
//                activity.timestamps().setEnd(Instant.ofEpochSecond(0));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (roundGames.contains(game))
//            if (ROUND_STATE != null) roundScoreboardUpdate(ROUND_STATE, false);
//        if (remainGames.contains(game))
//            if (REMAIN_STATE != null) remainScoreboardUpdate(REMAIN_STATE, false);
//        if (courseGames.contains(game))
//            if (COURSE_STATE != null) courseScoreboardUpdate(COURSE_STATE, false);
//        if (leapGames.contains(game))
//            if (LEAP_STATE != null) leapScoreboardUpdate(LEAP_STATE, false);
//        if (game == Game.HUB) {
//            activity.setState("");
//        }
//
//        updateActivity();
//    }
//
//    public void remainScoreboardUpdate(String value, Boolean set) {
//        if (activity == null) return;
//        if (!IslandOptions.getDiscord().showGameInfo || !IslandOptions.getDiscord().showGame) return;
//
//        if (set) REMAIN_STATE = "Remaining: " + value;
//        if (!remainGames.contains(MccIslandState.getGame())) return;
//
//        try { activity.setState(REMAIN_STATE); }
//        catch (Exception e) { e.printStackTrace(); }
//
//        updateActivity();
//    }
//
//    public void roundScoreboardUpdate(String value, Boolean set) {
//        if (activity == null) return;
//        if (!IslandOptions.getDiscord().showGameInfo || !IslandOptions.getDiscord().showGame) return;
//
//        if (set) ROUND_STATE = "Round: " + value;
//        if (!roundGames.contains(MccIslandState.getGame())) return;
//
//        try { activity.setState(ROUND_STATE); }
//        catch (Exception e) { e.printStackTrace(); }
//
//        updateActivity();
//    }
//
//    public void courseScoreboardUpdate(String value, Boolean set) {
//        if (activity == null) return;
//        if (!IslandOptions.getDiscord().showGameInfo || !IslandOptions.getDiscord().showGame) return;
//
//        if (set) COURSE_STATE = value;
//        if (!courseGames.contains(MccIslandState.getGame())) return;
//
//        try { activity.setState(COURSE_STATE); }
//        catch (Exception e) { e.printStackTrace(); }
//
//        updateActivity();
//    }
//
//    public void leapScoreboardUpdate(String value, Boolean set) {
//        if (activity == null) return;
//        if (!IslandOptions.getDiscord().showGameInfo || !IslandOptions.getDiscord().showGame) return;
//
//        if (set) LEAP_STATE = "Leap: " + value;
//        if (!leapGames.contains(MccIslandState.getGame())) return;
//
//        try { activity.setState(LEAP_STATE); }
//        catch (Exception e) { e.printStackTrace(); }
//
//        updateActivity();
//    }

    public void updateActivity(IslandState state) {
        if (!IslandOptions.getDiscord().discordPresence) return;
        Activity activity = state.getActivity();
        if (IslandOptions.getDiscord().showTimeElapsed)
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

    public void off() {
        try {
            DiscordPresence.getInstance().closeCore();
        } catch (Exception e) {
            logger.error("{}", "Could not clear discord presence", e);
        }
    }

    public void updateFromConfig(DiscordOptions options) {
        try {
            if (!MccIslandState.isOnline()) { clear(); return; }

            if (options.discordPresence) create(bigRatMode);
            else { clear(); return; }

            if (activity == null) activity = new Activity();

            if (!options.showTimeElapsed) activity.timestamps().setStart(Instant.MAX);
            else {
                if (started == null) started = Instant.now();
                activity.timestamps().setStart(started);
            }

            if (!options.showTimeRemaining) activity.timestamps().setEnd(Instant.ofEpochSecond(0));

            if (!options.showGame) {
                activity.assets().setSmallImage("");
                activity.assets().setSmallText("");

                activity.assets().setLargeImage("mcci");
                activity.assets().setLargeText("play.mccisland.net");

                activity.setDetails("");
                activity.setState("");
                activity.timestamps().setEnd(Instant.ofEpochSecond(0));
            } else updatePlace(MccIslandState.getGame());

            if (!options.showGameInfo) activity.setState("");
            else updatePlace(MccIslandState.getGame());

            if (!options.discordPresence) clear();
        } catch (Exception e) { e.printStackTrace(); }
    }

}
