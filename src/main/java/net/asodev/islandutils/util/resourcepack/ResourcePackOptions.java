package net.asodev.islandutils.util.resourcepack;

import net.asodev.islandutils.IslandConstants;
import net.asodev.islandutils.util.Utils;
import net.asodev.islandutils.util.resourcepack.schema.ResourcePack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ResourcePackOptions {
    public static final Path packDataFile = IslandConstants.islandFolder.resolve("pack.json");
    public static final Path packZip = IslandConstants.islandFolder.resolve("island_utils.zip");

    public static ResourcePack data;

    public static void save() throws IOException {
        Utils.assertIslandFolder();
        Utils.writeFile(packDataFile.toFile(), data.toJson());
    }

    public static ResourcePack get() throws Exception {
        File packData = packDataFile.toFile();
        String json = Utils.readFile(packData);
        if (json == null) return null;

        data = ResourcePack.fromJson(json);
        return data;
    }

}
