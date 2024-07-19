package net.asodev.islandutils.util;

import net.asodev.islandutils.IslandConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    public static final Style MCC_HUD_FONT = Style.EMPTY.withFont(new ResourceLocation("mcc", "hud"));
    public static final String BLANK_ITEM_ID = "island_interface.generic.blank";
    private static final List<String> NON_PROD_IP_HASHES = List.of(
            "e927084bb931f83eece6780afd9046f121a798bf3ff3c78a9399b08c1dfb1aec", // bigrat.mccisland.net easteregg/test ip
            "0c932ffaa687c756c4616a24eb49389213519ea8d18e0d9bdfd2d335771c35c7",
            "7f0d15bbb2ffaee1bbf0d23e5746afb753333d590f71ff8a5a186d86c3e79dda",
            "09445264a9c515c83fc5a0159bda82e25d70d499f80df4a2d1c2f7e2ae6af997"
    );

    public static List<Component> getLores(ItemStack item) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return null;
        return item.getTooltipLines(Item.TooltipContext.EMPTY, player, TooltipFlag.Default.NORMAL);
    }

    public static String readFile(File file) throws Exception {
        if (!file.exists()) return null;

        FileInputStream in = new FileInputStream(file);
        String json = new String(in.readAllBytes());
        in.close();
        return json;
    }

    public static void writeFile(File file, String data) throws IOException {
        if (!file.exists()) file.createNewFile();

        FileOutputStream out = new FileOutputStream(file);
        out.write(data.getBytes());
        out.close();
    }

    public static void assertIslandFolder() {
        File folder = IslandConstants.islandFolder.toFile();
        if (!folder.exists()) folder.mkdir();
    }

    public static int customModelData(ItemStack item) {
        CustomModelData customModelData = item.get(DataComponents.CUSTOM_MODEL_DATA);
        return customModelData == null ? 0 : customModelData.value();
    }

    public static ResourceLocation getCustomItemID(ItemStack item) {
        CustomData customDataComponent = item.get(DataComponents.CUSTOM_DATA);
        if (customDataComponent == null) return null;
        CompoundTag tag = customDataComponent.getUnsafe();
        CompoundTag publicBukkitValues = tag.getCompound("PublicBukkitValues");
        String customItemId = publicBukkitValues.getString("mcc:custom_item_id");
        if (customItemId.isEmpty()) return null;
        return new ResourceLocation(customItemId);
    }

    public static boolean isProdMCCI(String hostname) {
        String hostnameHash = Utils.calculateSha256(hostname);
        return !NON_PROD_IP_HASHES.contains(hostnameHash);
    }

    public static String calculateSha256(String input) {
        String output = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(input.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            output = String.format("%064x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to calculate SHA-256 for " + input);
        }
        return output;
    }

    public static boolean isLunarClient() {
        return FabricLoader.getInstance().isModLoaded("ichor");
    }
}
