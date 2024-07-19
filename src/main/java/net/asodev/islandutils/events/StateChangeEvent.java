package net.asodev.islandutils.events;

import net.asodev.islandutils.state.IslandState;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface StateChangeEvent {
    Event<StateChangeEvent> EVENT = EventFactory.createArrayBacked(StateChangeEvent.class,
            (listeners) -> (state) -> {
                for (StateChangeEvent listener : listeners) {
                    listener.change(state);
                }
            });

    void change(IslandState state);
}
