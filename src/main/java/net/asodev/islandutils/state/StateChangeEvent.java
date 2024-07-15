package net.asodev.islandutils.state;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface StateChangeEvent {
    Event<StateChangeEvent> EVENT = EventFactory.createArrayBacked(StateChangeEvent.class,
            (listeners) -> () -> {

            });

    ActionResult change(IslandState state);
}
