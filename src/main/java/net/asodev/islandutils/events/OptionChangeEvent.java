package net.asodev.islandutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface OptionChangeEvent {
    Event<OptionChangeEvent> EVENT = EventFactory.createArrayBacked(OptionChangeEvent.class,
            (listeners) -> () -> {
                for (OptionChangeEvent listener : listeners) {
                    listener.change();
                }
            });

    void change();
}
