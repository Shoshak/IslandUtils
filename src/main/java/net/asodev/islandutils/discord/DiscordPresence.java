package net.asodev.islandutils.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordPresence {
    private static volatile DiscordPresence instance;
    private final Logger logger = LoggerFactory.getLogger(DiscordPresence.class);

    private volatile Core core;

    private DiscordPresence() {
//        NativeLibrary nativeLibrary = new NativeLibrary();

//        File discordLibrary = nativeLibrary.getDiscordNative();
//        File discordJNI = nativeLibrary.getDiscordJNI();
//        System.load(discordLibrary.getAbsolutePath());
//        System.load(discordJNI.getAbsolutePath());

        Core.initFromClasspath();

    }

    public static DiscordPresence getInstance() {
        DiscordPresence discordPresence = instance;
        if (discordPresence != null) return discordPresence;
        synchronized (DiscordPresence.class) {
            if (instance == null) {
                instance = new DiscordPresence();
            }
            return instance;
        }
    }

    public void closeCore() {
        try {
            core.close();
            logger.info("Core has closed. Core: {}", core);
        }
        catch (Exception e) {
            logger.error("{}", "Could not close discord core", e);
        }
    }

    public Core getCoreInstance() {
        if (!core.isOpen()) {
            try (CreateParams params = new CreateParams()) {
                params.setClientID(1027930697417101344L);
                params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
                Core core = new Core(params);
                this.core = core;
                new Thread(() -> {
                    while (core.isOpen()) {
                        core.runCallbacks();
                        try {
                            // We can't do anything about busy waiting
                            // Because the library we're using does not offer polls or futures
                            // noinspection BusyWait
                            Thread.sleep(16);
                        }
                        catch (Exception e) {
                            logger.error("{}", "Could not sleep discord thread", e);
                            break;
                        }
                    }
                }, "IslandUtils - Discord Callbacks").start();
            }
        }
        return core;
    }
}
