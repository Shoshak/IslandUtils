package net.asodev.islandutils.options;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.EnumCycler;
import dev.isxander.yacl3.config.v2.api.autogen.IntField;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.asodev.islandutils.events.OptionChangeEvent;
import net.asodev.islandutils.modules.splits.SplitType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class IslandConfig {
    public static ConfigClassHandler<IslandConfig> HANDLER = ConfigClassHandler
            .createBuilder(IslandConfig.class)
            .id(Identifier.of("islandutils", "island_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("island_config.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static Screen getScreen(Screen parent) {
        return IslandConfig
                .HANDLER
                .generateGui()
                .generateScreen(parent);
    }

    public static void save() {
        HANDLER.save();
        OptionChangeEvent.EVENT.invoker().change();
    }

    // CLASSIC
    @SerialEntry
    @AutoGen(category = Categories.CLASSIC)
    @Boolean
    public boolean classicHITW = false;

    @SerialEntry
    @AutoGen(category = Categories.CLASSIC)
    @Boolean
    public boolean classicHITWMusic = false;

    // COSMETIC
    @SerialEntry
    @AutoGen(category = Categories.COSMETIC)
    @Boolean
    public boolean showPlayerPreview = true;

    @SerialEntry
    @AutoGen(category = Categories.COSMETIC)
    @Boolean
    public boolean showOnHover = true;

    @SerialEntry
    @AutoGen(category = Categories.COSMETIC)
    @Boolean
    public boolean showOnOnlyCosmeticMenus = true;

    // CRAFTING
    @SerialEntry
    @AutoGen(category = Categories.CRAFTING)
    @Boolean
    public boolean enableCraftingNotifications = true;

    @SerialEntry
    @AutoGen(category = Categories.CRAFTING)
    @Boolean
    public boolean toastNotifications = true;

    @SerialEntry
    @AutoGen(category = Categories.CRAFTING)
    @Boolean
    public boolean chatNotifications = true;

    @SerialEntry
    @AutoGen(category = Categories.CRAFTING)
    @Boolean
    public boolean notifyServerList = true;

    // DISCORD
    @SerialEntry
    @AutoGen(category = Categories.DISCORD)
    @Boolean
    public boolean discordPresence = true;

    @SerialEntry
    @AutoGen(category = Categories.DISCORD)
    @Boolean
    public boolean showGame = true;

    @SerialEntry
    @AutoGen(category = Categories.DISCORD)
    @Boolean
    public boolean showGameInfo = true;

    // MUSIC
    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean HITWMusic = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean BBMusic = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean SBMusic = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean dynaballMusic = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean TGTTOSMusic = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean TGTTOSDoubleTime = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean TGTTOSToTheDome = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean PKWMusic = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean PKWSMusic = true;

    @SerialEntry
    @AutoGen(category = Categories.MUSIC)
    @Boolean
    public boolean RSRMusic = true;

    // SPLITS
    @SerialEntry
    @AutoGen(category = Categories.SPLITS)
    @Boolean
    public boolean enablePKWSplits = true;

    @SerialEntry
    @AutoGen(category = Categories.SPLITS)
    @Boolean
    public boolean sendSplitTime = true;

    @SerialEntry
    @AutoGen(category = Categories.SPLITS)
    @Boolean
    public boolean showTimer = true;

    @SerialEntry
    @AutoGen(category = Categories.SPLITS)
    @Boolean
    public boolean showSplitImprovements = true;

    @SerialEntry
    @AutoGen(category = Categories.SPLITS)
    @IntField
    public int showTimerImprovementAt = -3;

    @SerialEntry
    @AutoGen(category = Categories.SPLITS)
    @EnumCycler
    public SplitType saveMode = SplitType.BEST;

    // MISC
    @SerialEntry
    @AutoGen(category = Categories.MISC)
    @Boolean
    public boolean pauseConfirm = true;

    @SerialEntry
    @AutoGen(category = Categories.MISC)
    @Boolean
    public boolean showFriendsInGame = true;

    @SerialEntry
    @AutoGen(category = Categories.MISC)
    @Boolean
    public boolean showFriendsInLobby = true;

    @SerialEntry
    @AutoGen(category = Categories.MISC)
    @Boolean
    public boolean silverPreview = true;

    @SerialEntry
    @AutoGen(category = Categories.MISC)
    @Boolean
    public boolean channelSwitchers = true;

    @SerialEntry
    @AutoGen(category = Categories.MISC)
    @Boolean
    public boolean enableConfigButton = true;

    @SerialEntry
    @AutoGen(category = Categories.MISC)
    @Boolean
    public boolean debugMode = false;

}
