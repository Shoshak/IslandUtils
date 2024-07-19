package net.asodev.islandutils.modules.crafting.state;

import net.asodev.islandutils.events.OnlineChangeEvent;
import net.asodev.islandutils.events.OptionChangeEvent;
import net.asodev.islandutils.modules.crafting.CraftingToast;
import net.asodev.islandutils.options.IslandConfig;
import net.asodev.islandutils.util.ChatUtils;
import net.asodev.islandutils.util.SoundUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.List;

import static net.asodev.islandutils.IslandConstants.MCC_HUD_FONT;

public class CraftingNotifier implements ClientTickEvents.EndTick {
    private boolean online = false;
    private boolean enableCraftingNotifications = IslandConfig.HANDLER.instance().enableCraftingNotifications;
    private boolean toastNotifications = IslandConfig.HANDLER.instance().toastNotifications;
    private boolean chatNotifications = IslandConfig.HANDLER.instance().chatNotifications;
    private final CraftingItems craftingItems;
    private int tick = 0;

    public CraftingNotifier(CraftingItems craftingItems) {
        this.craftingItems = craftingItems;
        ClientTickEvents.END_CLIENT_TICK.register(this);
        OnlineChangeEvent.EVENT.register(online -> this.online = online);
        OptionChangeEvent.EVENT.register(() -> {
            IslandConfig config = IslandConfig.HANDLER.instance();
            this.enableCraftingNotifications = config.enableCraftingNotifications;
            this.toastNotifications = config.toastNotifications;
            this.chatNotifications = config.chatNotifications;
        });
    }

    public void update(MinecraftClient client) {
        if (!online) return;
        boolean anythingHasChanged = false;
        for (CraftingItem item : craftingItems.getItems()) {
            if (item.isComplete()) continue;
            if (item.hasSentNotification()) continue;

            sendNotification(client, item);
            anythingHasChanged = true;
        }
        if (anythingHasChanged) craftingItems.save();
    }

    public void sendNotification(MinecraftClient client, CraftingItem item) {
        item.setHasSentNotification(true);

        if (!enableCraftingNotifications) return;
        boolean shouldMakeSound = false;
        if (toastNotifications) {
            client.getToastManager().add(new CraftingToast(item));
            shouldMakeSound = true;
        }
        if (chatNotifications) {
            sendChatNotification(item);
            shouldMakeSound = true;
        }

        if (shouldMakeSound) {
            sendNotificationSound();
        }
    }

    private void sendChatNotification(CraftingItem item) {
        Style darkGreenColor = Style.EMPTY.withColor(Formatting.DARK_GREEN);
        Text component = Text.literal("(").setStyle(darkGreenColor)
                .append(item.getTypeIcon())
                .append(Text.literal(") ").setStyle(darkGreenColor))
                .append(item.getTitle())
                .append(Text.literal(" has finished crafting!").setStyle(darkGreenColor));
        ChatUtils.send(component);
    }

    private void sendNotificationSound() {
        SoundUtil.playSound(new Identifier("mcc", "ui.achievement_receive"));
    }

    public Text activeCraftsMessage() {
        Text newLine = Text.literal("\n").setStyle(Style.EMPTY);
        MutableText component = Text.literal("\nCRAFTING ITEMS:").setStyle(MCC_HUD_FONT);
        component.append(newLine);

        int i = 0;
        List<CraftingItem> itemList = craftingItems.getItems();
        for (CraftingItem item : itemList) {
            i++;

            long timeRemaining = item.getFinishesCrafting() - System.currentTimeMillis();
            String timeText;
            Formatting timeColor;
            if (timeRemaining > 0) {
                timeText = DurationFormatUtils.formatDuration(timeRemaining, "H'h' m'm' s's'");
                timeColor = Formatting.RED;
            } else {
                timeText = "Complete";
                timeColor = Formatting.DARK_GREEN;
            }

            Text itemComponent = Text.literal(" ").setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID))
                    .append(item.getTypeIcon())
                    .append(" ")
                    .append(item.getTitle())
                    .append(Text.literal(" - ").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)))
                    .append(Text.literal(timeText).setStyle(Style.EMPTY.withColor(timeColor)));

            component.append(itemComponent);
            if (i < itemList.size()) component.append(newLine);
        }
        return component;
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        tick++;
        if (tick >= 20) {
            update(client);
            tick = 0;
        }
    }

//    public static LiteralArgumentBuilder<FabricClientCommandSource> getDebugCommand() {
//        return literal("add_craft")
//                .then(argument("color", StringArgumentType.string())
//                        .then(argument("slot", IntegerArgumentType.integer())
//                                .then(argument("delay", IntegerArgumentType.integer())
//                                        .executes(ctx -> {
//                                            if (!IslandOptions.getMisc().isDebugMode()) {
//                                                ctx.getSource().sendError(cantUseDebugError);
//                                                return 0;
//                                            }
//                                            String color = ctx.getArgument("color", String.class);
//                                            Integer slot = ctx.getArgument("slot", Integer.class);
//                                            Integer delay = ctx.getArgument("delay", Integer.class);
//                                            CraftingItems.addDebugItem(color, slot, delay);
//                                            return 1;
//                                        }))));
//    }
}
