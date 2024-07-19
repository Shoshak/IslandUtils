package net.asodev.islandutils.state;

import net.asodev.islandutils.events.OnlineChangeEvent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.network.ServerInfo;

public class OnlineStateHandler {
    public OnlineStateHandler() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerInfo currentServerEntry = client.getCurrentServerEntry();
            if (currentServerEntry == null) {
                return;
            }
            String address = currentServerEntry.address;
            if (address.contains("mccisland.net") || address.contains("mccisland.com")) {
                OnlineChangeEvent.EVENT.invoker().isOnline(true);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> OnlineChangeEvent.EVENT.invoker().isOnline(false));
    }
}
