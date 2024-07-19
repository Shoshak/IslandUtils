package net.asodev.islandutils.modules.crafting.state;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.asodev.islandutils.modules.crafting.CraftingMenuType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static net.asodev.islandutils.util.ChatUtils.iconsFontStyle;

public class CraftingItem {
    private Text title;
    private Item type;
    private int customModelData;

    private long finishesCrafting;
    private CraftingMenuType craftingMenuType;
    private boolean hasSentNotification = false;
    private int slot;

    public String toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("title", Text.Serialization.toJsonString(title, BuiltinRegistries.createWrapperLookup()));

        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(type);
        if (key.isEmpty()) throw new IllegalStateException("Item key not found in registries");
        object.addProperty("type", key.get().getValue().toString());

        object.addProperty("customModelData", customModelData);
        object.addProperty("finishesCrafting", finishesCrafting);
        object.addProperty("craftingMenuType", craftingMenuType.name());
        object.addProperty("hasSentNotification", hasSentNotification);
        object.addProperty("slot", slot);
        return object.getAsString();
    }

    public static CraftingItem fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        CraftingItem item = new CraftingItem();

        String jsonTitle = object.get("title").getAsString();
        item.setTitle(Text.Serialization.fromJson(jsonTitle, BuiltinRegistries.createWrapperLookup()));

        JsonElement type = object.get("type");
        Identifier identifier = new Identifier(type.getAsString());
        Optional<Item> registryItem = Registries.ITEM.getOrEmpty(identifier);
        if (registryItem.isEmpty()) throw new IllegalStateException("Item not found in registries");
        item.setType(registryItem.get());
        item.setCustomModelData(object.get("customModelData").getAsInt());

        String craftingTypeString = object.get("craftingMenuType").getAsString();
        item.setCraftingMenuType(CraftingMenuType.valueOf(craftingTypeString.toUpperCase()));

        item.setFinishesCrafting(object.get("finishesCrafting").getAsLong());
        item.setHasSentNotification(object.get("hasSentNotif").getAsBoolean());
        item.setSlot(object.get("slot").getAsInt());

        return item;
    }

    public ItemStack getStack() {
        ItemStack stack = new ItemStack(type);
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(customModelData));
        return stack;
    }

    public boolean isComplete() {
        return System.currentTimeMillis() < this.getFinishesCrafting();
    }

    public Text getTitle() {
        return title;
    }

    public Text getTypeIcon() {
        String icon = this.getCraftingMenuType() == CraftingMenuType.FORGE ? "\ue006" : "\ue007";
        return Text.literal(icon).setStyle(iconsFontStyle);
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Item getType() {
        return type;
    }

    public void setType(Item type) {
        this.type = type;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public long getFinishesCrafting() {
        return finishesCrafting;
    }

    public void setFinishesCrafting(long finishesCrafting) {
        this.finishesCrafting = finishesCrafting;
    }

    public CraftingMenuType getCraftingMenuType() {
        return craftingMenuType;
    }

    public void setCraftingMenuType(CraftingMenuType craftingMenuType) {
        this.craftingMenuType = craftingMenuType;
    }

    public boolean hasSentNotification() {
        return hasSentNotification;
    }

    public void setHasSentNotification(boolean hasSentNotification) {
        this.hasSentNotification = hasSentNotification;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public String toString() {
        return "CraftingItem{" +
                "title=" + title +
                ", type=" + type +
                ", customModelData=" + customModelData +
                ", finishesCrafting=" + finishesCrafting +
                ", craftingMenuType=" + craftingMenuType +
                ", hasSentNotif=" + hasSentNotification +
                ", slot=" + slot +
                '}';
    }
}
