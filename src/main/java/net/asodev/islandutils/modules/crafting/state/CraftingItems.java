package net.asodev.islandutils.modules.crafting.state;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.asodev.islandutils.modules.crafting.CraftingMenuType;
import net.asodev.islandutils.util.ChatUtils;
import net.asodev.islandutils.util.Utils;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static net.asodev.islandutils.IslandConstants.ISLAND_FOLDER;

public class CraftingItems {
    private final Logger logger = LoggerFactory.getLogger(CraftingItems.class);
    private final Path file = ISLAND_FOLDER.resolve("crafting.json");
    private final List<CraftingItem> items = new ArrayList<>();

    /**
     * Adds an item into the items
     *
     * @param item The CraftingItem to add
     */
    public void addItem(CraftingItem item) {
        removeSlot(item.getCraftingMenuType(), item.getSlot());
        items.add(item);

        save();
    }

    /**
     * Remove items in the same menu & slot
     *
     * @param type The menu type (Forge or Assembler)
     * @param slot The slot you wish to remove
     */
    public void removeSlot(CraftingMenuType type, int slot) {
        boolean wasRemoved = items.removeIf(i -> i.getCraftingMenuType() == type && i.getSlot() == slot);
        if (wasRemoved) save();
    }

    public List<CraftingItem> getItems() {
        return items;
    }

    public void load() throws IOException {
        String string = Files.readString(file);
        JsonObject object = new Gson().fromJson(string, JsonObject.class);
        JsonArray array = object.get("items").getAsJsonArray();
        array.forEach(element -> items.add(CraftingItem.fromJson(element)));
    }

    public void save() throws IOException {
        Utils.assertIslandFolder();
        JsonArray array = new JsonArray();
        for (CraftingItem item : getItems()) {
            array.add(item.toJson());
        }
        JsonObject object = new JsonObject();
        object.add("items", array);
        object.addProperty("savedAt", System.currentTimeMillis());
        object.addProperty("version", 1);
        Files.writeString(file, object.toString());
    }

    // DEBUG
    public void addDebugItem(String color, Integer slot, Integer delay) {
        CraftingItem item = new CraftingItem();
        item.setTitle(
                Text.literal("Refined Quest Spirit")
                        .setStyle(Style.EMPTY.withColor(ChatUtils.parseColor(color)))
        );
        // Common = #FFFFFF
        // Uncommon = #1EFF00
        // Rare = #0070DD
        // Epic = #A335EE
        // Legendary = #FF8000
        // Mythic = #F94242

        item.setCustomModelData(7924);
        item.setFinishesCrafting(System.currentTimeMillis() + delay * 1000);
        item.setHasSentNotification(false);
        item.setSlot(slot);
        item.setCraftingMenuType(CraftingMenuType.ASSEMBLER);
        item.setType(Items.POPPED_CHORUS_FRUIT);

        this.addItem(item);
    }

}
