package net.asodev.islandutils.state;

import net.asodev.islandutils.IslandUtils;
import net.asodev.islandutils.modules.crafting.state.CraftingItem;
import net.asodev.islandutils.modules.crafting.state.CraftingItems;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.util.resourcepack.ResourcePackUpdater;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class MccIslandNotifications {
    private static final Text completedCrafts = Text
            .literal("Completed Crafts:")
            .setStyle(Style.EMPTY.withBold(true).withColor(Formatting.WHITE));

    public static List<Text> getNotificationLines() {
        List<Text> components = new ArrayList<>();

        MccIslandNotifications.addCraftingNotifications(components);
        MccIslandNotifications.addPackDownloadNotifications(components);

        return components;
    }

    private static void addCraftingNotifications(List<Text> components) {
        List<Text> craftingLists = new ArrayList<>();
        boolean anycomplete = false;
        IslandConfig config = IslandConfig.HANDLER.instance();
        if (config.enableCraftingNotifications && config.notifyServerList) { // "i'm a never-nester"
            for (CraftingItem item : CraftingItems.getItems()) {
                if (!item.isComplete()) continue;
                craftingLists.add(Text.literal("  ").append(item.getTitle()));
                anycomplete = true;
            }
        }
        if (!anycomplete) return;

        components.add(completedCrafts);
        components.addAll(craftingLists);
    }

    private static void addPackDownloadNotifications(List<Text> components) {
        ResourcePackUpdater.PackDownloadListener currentDownload = IslandUtils.packUpdater.currentDownload;
        if (currentDownload == null) return;

        MutableText downloadTitle = Text.literal("Downloading Music:")
                .setStyle(Style.EMPTY.withBold(true));

        String bytesText = SizeUnit.humanReadableSize(currentDownload.getBytesDownloaded(), SizeUnit.B);
        long size = currentDownload.getSize();
        String maxSize = size != -1 ? " / " + SizeUnit.humanReadableSize(size, SizeUnit.B) : "";
        Text downloadProgress = Text.literal(" " + bytesText + maxSize);

        components.add(downloadTitle);
        components.add(downloadProgress);
    }

}
