package net.asodev.islandutils.util.resourcepack;

import net.asodev.islandutils.IslandConstants;
import net.asodev.islandutils.util.Utils;
import net.asodev.islandutils.util.resourcepack.schema.ResourcePack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourcePackOptions {
    public static final Path packDataFile = IslandConstants.ISLAND_FOLDER.resolve("pack.json");
    public static final Path packZip = IslandConstants.ISLAND_FOLDER.resolve("island_utils.zip");

    public static ResourcePack data;

    public static void save() throws IOException {
        Utils.assertIslandFolder();
        Files.writeString(packDataFile, data.toJson());
    }

    public static ResourcePack get() throws IOException {
        String json = Files.readString(packDataFile);

        data = ResourcePack.fromJson(json);
        return data;
    }

}
