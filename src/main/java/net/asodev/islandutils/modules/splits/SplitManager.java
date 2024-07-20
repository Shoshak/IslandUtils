package net.asodev.islandutils.modules.splits;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.asodev.islandutils.IslandUtilsEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static net.asodev.islandutils.IslandConstants.ISLAND_FOLDER;

public class SplitManager {
    private final Logger logger = LoggerFactory.getLogger(SplitManager.class);
    private final Path file = ISLAND_FOLDER.resolve("splits.json");

    private final Map<String, LevelSplits> courseSplits = new HashMap<>();
    private long currentCourseExpiry;

    public SplitManager() {
        IslandUtilsEvents.GAME_CHANGE.register((game) -> {
            if (game != Game.PARKOUR_WARRIOR_DOJO) LevelTimer.setInstance(null);
        });
    }

    public LevelSplits getCourseSplits(String courseName) {
        String name = courseName.toLowerCase().contains("daily challenge") ? "daily" : courseName;
        LevelSplits levelSplits = courseSplits.get(name);

        if (levelSplits == null || (levelSplits.getExpires() != null && System.currentTimeMillis() >= levelSplits.getExpires())) {
            levelSplits = new LevelSplits(name);
            levelSplits.setExpires(currentCourseExpiry);
            courseSplits.put(name, levelSplits);
            logger.debug("LevelTimer - Created splits for: {}", name);
        } else {
            logger.debug("SplitManager - Found splits for: {}", name);
        }
        return levelSplits;
    }

    public void clearSplits() {
        courseSplits.clear();
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() throws IOException {
        JsonObject object = new JsonObject();
        JsonArray array = new JsonArray();
        for (Map.Entry<String, LevelSplits> split : courseSplits.entrySet()) {
            array.add(split.getValue().toJson());
        }

        object.add("splits", array);
        object.addProperty("savedAt", System.currentTimeMillis());
        object.addProperty("version", 1);
        Files.writeString(file, object.toString());
    }

    private void load() throws IOException {
        String string = Files.readString(file);
        JsonObject object = new Gson().fromJson(string, JsonObject.class);
        for (JsonElement element : object.getAsJsonArray("splits").asList()) {
            LevelSplits splits = new LevelSplits(element.getAsJsonObject());
            courseSplits.put(splits.getName(), splits);
        }
    }

    public long getCurrentCourseExpiry() {
        return currentCourseExpiry;
    }

    public void setCurrentCourseExpiry(long currentCourseExpiry) {
        this.currentCourseExpiry = currentCourseExpiry;
    }
}
