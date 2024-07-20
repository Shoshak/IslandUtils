package net.asodev.islandutils.mixins.network;

import net.asodev.islandutils.IslandUtilsEvents;
import net.asodev.islandutils.discord.DiscordPresenceUpdater;
import net.asodev.islandutils.modules.ClassicAnnouncer;
import net.asodev.islandutils.modules.cosmetics.CosmeticSlot;
import net.asodev.islandutils.modules.cosmetics.CosmeticState;
import net.asodev.islandutils.modules.splits.LevelTimer;
import net.asodev.islandutils.options.IslandSoundCategories;
import net.asodev.islandutils.util.ChatUtils;
import net.asodev.islandutils.util.MusicUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class PacketListenerMixin implements ClientCommonPacketListener {

    @Unique
    private final ClassicAnnouncer announcer = new ClassicAnnouncer(ChatUtils.parseColor("#FFA800"));

    @Unique
    private final Logger logger = LoggerFactory.getLogger(PacketListenerMixin.class);

    // Patterns for the Map & Modifier options on scoreboard
    final Map<String, Pattern> scoreboardPatterns = Map.of(
            "MAP", Pattern.compile("MAP: (?<map>\\w+(?:,? \\w+)*)"),
            "MODIFIER", Pattern.compile("MODIFIER: (?<modifier>\\w+(?:,? \\w+)*)"),
            "COURSE", Pattern.compile("COURSE: (?<course>.*)"),
            "LEAP", Pattern.compile("LEAP \\[(?<leap>.*/.*)]")
    );

    @Inject(method = "onTeam", at = @At("TAIL")) // Scoreboard lines!
    public void handleSetPlayerTeamPacket(TeamS2CPacket packet, CallbackInfo ci) {
        if (!MccIslandState.isOnline()) return;
        Optional<ClientboundSetPlayerTeamPacket.Parameters> optional = clientboundSetPlayerTeamPacket.getParameters();
        if (optional.isEmpty()) return;

        ClientboundSetPlayerTeamPacket.Parameters parameters = optional.get(); // Get the team parameters
        try { // We do a little exceptioning
            Component prefixComponent = parameters.getPlayerPrefix(); // Get the prefix of this team
            String playerPrefix = prefixComponent.getString(); // Turn it uppercase

            for (Map.Entry<String, Pattern> entry : scoreboardPatterns.entrySet()) { // Loop over our scoreboard reg-exes
                Matcher matcher = entry.getValue().matcher(playerPrefix); // Match the prefix against the regex
                if (!matcher.find()) continue; // If we don't have a subsequence, then go to the next
                String value = matcher.group(1); // WE HAVE A MATCH, Get the first Regex group

                switch (entry.getKey()) {
                    case "MAP" -> MccIslandState.setMap(value); // Set our MAP
                    case "MODIFIER" -> MccIslandState.setModifier(value); // Set our MODIFIER
                    case "COURSE" -> {
                        DiscordPresenceUpdater.courseScoreboardUpdate(value, true);
                        MccIslandState.setMap(value);
                    }
                    case "LEAP" -> DiscordPresenceUpdater.leapScoreboardUpdate(value, true);
                }

                logger.debug("ScoreboardUpdate - Current {}: \"{}\"", entry.getKey(), value);
            }
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
    public void handleCustomSoundEvent(PlaySoundS2CPacket packet, CallbackInfo ci) {
        PacketUtils.ensureRunningOnSameThread(clientboundCustomSoundPacket, (ClientPacketListener) (Object) this, this.minecraft);
        if (!MccIslandState.isOnline()) return;

        // Get the location of the new sound
        // Previously this was obfuscated by noxcrew, but not anymore yayyyyy :D
        ResourceLocation soundLoc = clientboundCustomSoundPacket.getSound().value().getLocation();

        if (MccIslandState.getGame() == Game.PARKOUR_WARRIOR_DOJO) {
            LevelTimer.onSound(clientboundCustomSoundPacket);
        }

        // If we aren't in a game, don't play music
        if (MccIslandState.getGame() != Game.HUB) {
            // Use the sound files above to determine what just happened in the game
            if (MccIslandState.getGame() != Game.BATTLE_BOX) {

                // Stop the music if you restart the course or switch game mode in Parkour Warrior
                if (soundLoc.getPath().contains("games.parkour_warrior.mode_swap") || soundLoc.getPath().contains("games.parkour_warrior.restart_course")) {
                    MusicUtil.stopMusic(false);
                }
                if (MccIslandState.getGame() == Game.PARKOUR_WARRIOR_SURVIVOR && Objects.equals(soundLoc.getPath(), "games.global.early_elimination")) {
                    MusicUtil.startMusic(clientboundCustomSoundPacket, true); // The game started. Start the music!!
                } else if (Objects.equals(soundLoc.getPath(), "games.global.countdown.go") && !MusicUtil.isMusicPlaying()) {
                    MusicUtil.startMusic(clientboundCustomSoundPacket); // The game started. Start the music!!
                }
            } else {
                // We're playing battlebox, and the music needs to start early.
                if (Objects.equals(soundLoc.getPath(), "music.global.gameintro")) {
                    MusicUtil.startMusic(clientboundCustomSoundPacket); // Start the music!!
                    logger.debug("[PacketListener] Canceling gameintro");
                    ci.cancel(); // Stop minecraft from minecrafting
                    return;
                }
            }
            if (Objects.equals(soundLoc.getPath(), "games.global.timer.round_end")
                    || soundLoc.getPath().startsWith("music.global.")) {
                // The game ended or is about to end. Stop the music!!
                MusicUtil.stopMusic();
            }
        }

        SoundInstance instance;
        // If our sound is music, play in the Core Music slider
        if (soundLoc.getPath().contains("music.global")) {
            instance = MusicUtil.createSoundInstance(clientboundCustomSoundPacket, IslandSoundCategories.CORE_MUSIC); // Create the sound
            Minecraft.getInstance().getSoundManager().play(instance); // Play the sound
            ci.cancel(); // Stop minecraft from trying.
        } else if (soundLoc.getNamespace().equals("mcc")) { // If it's a MCC sound, we'll just play it in sound effects.
            instance = MusicUtil.createSoundInstance(clientboundCustomSoundPacket, IslandSoundCategories.SOUND_EFFECTS); // Make the sound
            Minecraft.getInstance().getSoundManager().play(instance); // Play the sound
            ci.cancel(); // Minecraft no
        }
    }

    @Inject(method = "onInventory", at = @At("TAIL"))
    // Cosmetic previews, whenever we get our cosmetics back after closing menu
    private void containerContent(InventoryS2CPacket packet, CallbackInfo ci) {
        if (!MccIslandState.isOnline()) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return; // If no player, stop
        if (clientboundContainerSetContentPacket.getContainerId() != 0) return; // If this is a chest, stop

        CosmeticState.hatSlot.setOriginal(new CosmeticSlot(player.getInventory().armor.get(3)));
        CosmeticState.accessorySlot.setOriginal(new CosmeticSlot(player.getInventory().offhand.get(0)));
    }

    @Inject(method = "onPlayerRespawn", at = @At("HEAD")) // Whenever we change worlds
    private void handleRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        ClientLevel clientLevel = this.minecraft.level; // Get our player
        if (clientLevel == null) return; // minecraft is a good game.

        ResourceKey<Level> resourceKey = clientboundRespawnPacket.commonPlayerSpawnInfo().dimension(); // Get the key of this world
        if (resourceKey != clientLevel.dimension()) { // If we have changed worlds...
            MusicUtil.stopMusic(); // ...stop the music
        }
    }

    private static Pattern timerPattern = Pattern.compile("(\\d+:\\d+)");

    @Inject(method = "onBossBar", at = @At("HEAD")) // Discord presence, time left
    private void handleBossUpdate(BossBarS2CPacket packet, CallbackInfo ci) {
        if (!MccIslandState.isOnline()) return;
        // Create a handler for the bossbar
        packet.accept(new BossBarS2CPacket.Consumer() {
            @Override
            public void updateName(UUID uuid, Text name) {
                String text = component.getString();
                Matcher matcher = timerPattern.matcher(text);
                if (!matcher.find()) return;
                String timer = matcher.group(1);
                String[] split = timer.split(":"); // Split by the timer
                String minsText = split[0]; // Get the left side
                String secsText = split[1]; // Get the right side

                int mins = Integer.parseInt(minsText.substring(Math.max(minsText.length() - 2, 0))); // Get the last 2 character of the left side
                int secs = Integer.parseInt(secsText.substring(0, 2)); // Get the first 2 on the right side

                long secondsLeft = ((mins * 60L) + secs + 1);
                long finalUnix = System.currentTimeMillis() + (secondsLeft * 1000); // Get the timestamp when the game will end

                DiscordPresenceUpdater.timeLeftBossbar = uUID; // why do i do this again?
                DiscordPresenceUpdater.updateTimeLeft(finalUnix); // Update our time left!!
                BossBarS2CPacket.Consumer.super.updateName(uuid, name);
            }
        });
    }

//    @Inject(method = "handleCommandSuggestions", cancellable = true, at = @At("HEAD")) // "Friends in this game: "
//    private void commandSuggestionsResponse(ClientboundCommandSuggestionsPacket clientboundCommandSuggestionsPacket, CallbackInfo ci) {
//        if (clientboundCommandSuggestionsPacket.id() == TRANSACTION_ID) { // If we get back suggestions from our previous request
//            ci.cancel(); // Stop minecraft... please.
//            List<String> friends = clientboundCommandSuggestionsPacket // our friends suggestions
//                    .suggestions() // the suggestions
//                    .stream().map(ClientboundCommandSuggestionsPacket.Entry::text) // the text of the suggestions
//                    .collect(Collectors.toList()); // a list of the suggestions
//            FriendsInGame.setFriends(friends); // Set our friends!
//        }
//    }

    @Inject(method = "onSubtitle", at = @At("HEAD"), cancellable = true)
    private void titleText(SubtitleS2CPacket packet, CallbackInfo ci) {
        if (MccIslandState.getGame() == Game.HITW) {
            announcer.handleTrap(clientboundSetSubtitleTextPacket, ci);
        } else if (MccIslandState.getGame() == Game.PARKOUR_WARRIOR_DOJO) {
            LevelTimer instance = LevelTimer.getInstance();
            if (instance == null) return;
            instance.handleSubtitle(clientboundSetSubtitleTextPacket, ci);
        }
    }

    @Inject(method = "onTitle", at = @At("HEAD")) // Game Over Sound Effect
    private void gameOver(TitleS2CPacket packet, CallbackInfo ci) {
        if (MccIslandState.getGame() != Game.HITW) return; // Make sure we're playing HITW
        if (!IslandOptions.getClassicHITW().isClassicHITW()) return; // Requires isClassicHITW
        String title = clientboundSetTitleTextPacket.text().getString().toUpperCase(); // Get the title in upper case

        if (title.contains("GAME OVER")) { // If we got game over title, play sound
            ResourceLocation sound = new ResourceLocation("island", "announcer.gameover");
            Minecraft.getInstance().getSoundManager().play(MusicUtil.createSoundInstance(sound)); // Play the sound!!
        }
    }

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void onChat(ChatMessageS2CPacket packet, CallbackInfo ci) {
        if (clientboundSystemChatPacket.overlay() || !MccIslandState.isOnline()) return;

        IslandUtilsEvents.Modifier<Component> modifier = new IslandUtilsEvents.Modifier<>();
        IslandUtilsEvents.CHAT_MESSAGE.invoker().onChatMessage(clientboundSystemChatPacket, modifier);

        modifier.withCancel(value -> ci.cancel());
        modifier.withReplacement(replacement -> {
            ci.cancel();
            this.minecraft.getChatListener().handleSystemMessage(replacement, false);
        });
    }

//    // MCCI Commands
//    @Inject(method = "", at = @At("TAIL"))
//    private void handleCommands(ClientboundCommandsPacket clientboundCommandsPacket, CallbackInfo ci) {
//        if (!MccIslandState.isOnline()) return;
//        this.commands.register((LiteralArgumentBuilder<CommandSourceStack>)IslandUtilsClient.Commands.resetMusic);
//    }
//
//    @Inject(method = "sendUnsignedCommand", at = @At("HEAD"), cancellable = true)
//    private void sendUnsignedCommand(String string, CallbackInfoReturnable<Boolean> cir) {
//        if (!MccIslandState.isOnline()) return;
//        if (string.startsWith("resetmusic")) {
//            executeCommand();
//            cir.setReturnValue(true);
//        }
//    }
//    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
//    private void sendCommand(String string, CallbackInfo ci) {
//        if (!MccIslandState.isOnline()) return;
//        if (string.startsWith("resetmusic")) {
//            executeCommand();
//            ci.cancel();
//        }
//    }
//
//    private static void executeCommand() {
//        try { IslandUtilsClient.Commands.resetMusic.getCommand().run(null); }
//        catch (CommandSyntaxException e) { e.printStackTrace(); }
//    }

}
