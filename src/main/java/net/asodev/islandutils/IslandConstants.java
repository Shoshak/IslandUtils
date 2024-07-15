package net.asodev.islandutils;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class IslandConstants {
    public static final Path islandFolder = FabricLoader.getInstance().getConfigDir().resolve("islandutils_resources");
}
