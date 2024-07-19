package net.asodev.islandutils.discord;

import net.asodev.islandutils.IslandConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static net.asodev.islandutils.util.Utils.assertIslandFolder;

public class NativeLibrary {
    private final Logger logger = LoggerFactory.getLogger(NativeLibrary.class);

    private final String os = System.getProperty("os.name").toLowerCase();
    ;
    private final String architecture = System
            .getProperty("os.arch")
            .toLowerCase()
            .replaceAll("amd64", "x86_64");
    private final String extension;

    public NativeLibrary() {
        if (os.contains("windows")) this.extension = ".dll";
        else if (os.contains("linux")) this.extension = ".so";
        else if (os.contains("mac")) this.extension = ".dylib";
        else throw new IllegalArgumentException("Unknown os");
        assertIslandFolder();
    }

    public File getDiscordNative() throws Exception {
        String name = "discord_game_sdk";
        String libIndex = "native/lib/" + architecture + "/" + name + extension;
        return getNatives(libIndex);
    }

    public File getDiscordJNI() throws Exception {
        String name = "discord_game_sdk_jni";
        String libIndex = "native/" + os + "/" + architecture + "/" + name + extension;
        return getNatives(libIndex);
    }

    private File getNatives(String libIndex) throws Exception {
        File outFile = IslandConstants.ISLAND_FOLDER.resolve(libIndex).toFile();
        if (!outFile.exists()) {
            logger.info("Extracting Discord Natives.");
            InputStream stream = NativeLibrary.class.getResourceAsStream("/" + libIndex);
            if (stream == null)
                throw new FileNotFoundException(String.format("Natives couldn't be found in the resources. (%s)", libIndex));

            boolean mkdirResult = outFile.mkdirs();
            if (!mkdirResult) throw new Exception("Some of the directories were not created");
            Files.copy(stream, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        return outFile;
    }

}
