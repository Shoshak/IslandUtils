package net.asodev.islandutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface OnlineChangeEvent {
    Event<OnlineChangeEvent> EVENT = EventFactory.createArrayBacked(OnlineChangeEvent.class,
            (listeners) -> (isOnline) -> {
                for (OnlineChangeEvent listener : listeners) {
                    listener.isOnline(isOnline);
                }
            });

    void isOnline(boolean online);
}
