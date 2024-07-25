package net.asodev.islandutils.modules.crafting;

import net.asodev.islandutils.modules.crafting.state.CraftingItem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static net.asodev.islandutils.IslandConstants.MCC_HUD_FONT;

public class CraftingToast implements Toast {
    private final Identifier ISLAND_TOASTS_TEXTURE = Identifier.of("island", "textures/gui/toasts.png");

    ItemStack itemStack;
    Text displayName;
    final Text description = Text
            .literal("CRAFTING COMPLETE!")
            .setStyle(MCC_HUD_FONT.withColor(Formatting.WHITE));

    public CraftingToast(CraftingItem craftingItem) {
        itemStack = craftingItem.getStack();
        displayName = craftingItem.getTitle();
    }

    @Override
    public Visibility draw(DrawContext context, ToastManager manager, long startTime) {
        TextRenderer textRenderer = manager.getClient().textRenderer;
        context.drawTexture(ISLAND_TOASTS_TEXTURE, 0, 0, 0, 0, getWidth(), getHeight());
        context.drawText(textRenderer, description, 30, 7, -16777216, false);
        context.drawText(textRenderer, displayName, 30, 16, -11534256, false);
        context.drawItemWithoutEntity(itemStack, 8, 8);
        return (double) startTime >= 5000.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE :
                Visibility.SHOW;
    }

    @Override
    public Object getType() {
        return Toast.super.getType();
    }

    @Override
    public int getWidth() {
        return Toast.super.getWidth();
    }

    @Override
    public int getHeight() {
        return Toast.super.getHeight();
    }

    @Override
    public int getRequiredSpaceCount() {
        return Toast.super.getRequiredSpaceCount();
    }
}
