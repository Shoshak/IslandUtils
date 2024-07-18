package net.asodev.islandutils.state;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface StateChangeEvent {
    Event<StateChangeEvent> EVENT = EventFactory.createArrayBacked(StateChangeEvent.class,
            (listeners) -> (state) -> {
                for (StateChangeEvent listener : listeners) {
                    ActionResult result = listener.change(state);
                    if (result != ActionResult.PASS) return result;
                }
                return ActionResult.PASS;
            });

    ActionResult change(IslandState state);
}
