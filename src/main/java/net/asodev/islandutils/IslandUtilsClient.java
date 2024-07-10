package net.asodev.islandutils;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.asodev.islandutils.discord.DiscordPresenceUpdator;
import net.asodev.islandutils.modules.DisguiseKeybind;
import net.asodev.islandutils.modules.NoxesiumIntegration;
import net.asodev.islandutils.modules.plobby.PlobbyFeatures;
import net.asodev.islandutils.modules.plobby.PlobbyJoinCodeCopy;
import net.asodev.islandutils.modules.splits.LevelSplits;
import net.asodev.islandutils.modules.splits.SplitManager;
import net.asodev.islandutils.modules.splits.sob.AdvancedInfo;
import net.asodev.islandutils.modules.splits.sob.SobCalc;
import net.asodev.islandutils.modules.splits.ui.SplitUI;
import net.asodev.islandutils.state.Game;
import net.asodev.islandutils.state.MccIslandState;
import net.asodev.islandutils.util.ChatUtils;
import net.asodev.islandutils.util.IslandUtilsCommand;
import net.asodev.islandutils.util.MusicUtil;
import net.asodev.islandutils.util.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class IslandUtilsClient implements ClientModInitializer {
    public static KeyMapping openPlobbyKey;
    public static KeyMapping disguiseKeyBind;

    @Override
    public void onInitializeClient() {
        openPlobbyKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.islandutils.plobbymenu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.islandutils.keys"
        ));
        disguiseKeyBind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.islandutils.disguise",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "category.islandutils.keys"
        ));
        SplitManager.init();
        DisguiseKeybind.registerDisguiseInput();
        PlobbyFeatures.registerEvents();
        IslandUtilsCommand.register();
        DiscordPresenceUpdator.init();
        PlobbyJoinCodeCopy.register();

        if (Utils.isLunarClient()) {
            SplitUI.setupFallbackRenderer();
        }
        new NoxesiumIntegration().init();

        // MCCI Commands but better
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("showadvancedpath").executes(context -> {
            if (MccIslandState.getGame() != Game.PARKOUR_WARRIOR_DOJO) {
                context.getSource().sendFeedback(Component.literal("Not in PKWD"));
                return 0;
            }
            LevelSplits splits = SplitManager.getCourseSplits(MccIslandState.getMap());
            Optional<AdvancedInfo> advInfo = SobCalc.advancedTime(splits);
            if (advInfo.isEmpty()) {
                context.getSource().sendFeedback(Component.literal("Could not get advanced path"));
                return 0;
            }
            context.getSource().sendFeedback(Component.literal(advInfo.get().path()));
            return 1;
        }))));
    }

    public static void onJoinMCCI(boolean isProduction) {
        System.out.println("Connected to MCCI!");
        if (IslandUtils.availableUpdate != null) {
            ChatUtils.send("Hey! Update " + IslandUtils.availableUpdate.title() + " is available for Island Utils!");

            Style style = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, IslandUtils.availableUpdate.releaseUrl()));
            Component link = Component.literal(IslandUtils.availableUpdate.releaseUrl()).setStyle(style);
            Component text = Component.literal(ChatUtils.translate(ChatUtils.prefix + " Download Here: &f")).append(link);

            ChatUtils.send(text);
        } else if (IslandUtils.isPreRelease()) {
            ChatUtils.send("&cYou are using a pre-release version of IslandUtils! Expect things to be broken and buggy, and report to #test-feedback!");
        }

        DiscordPresenceUpdator.create(!isProduction);
        MccIslandState.setGame(Game.HUB);
        IslandUtilsEvents.JOIN_MCCI.invoker().onEvent();
    }

    public static class Commands {
        public static LiteralArgumentBuilder<?> resetMusic = net.minecraft.commands.Commands.literal("resetmusic").executes(ctx -> {
            MusicUtil.resetMusic(ctx);
            return 1;
        });
    }
}
