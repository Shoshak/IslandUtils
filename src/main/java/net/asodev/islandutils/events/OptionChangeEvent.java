package net.asodev.islandutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface OptionChangeEvent {
    Event<OptionChangeEvent> EVENT = EventFactory.createArrayBacked(OptionChangeEvent.class,
            (listeners) -> () -> {
                for (OptionChangeEvent listener : listeners) {
                    ActionResult change = listener.change();

                    if (change != ActionResult.PASS) {
                        return change;
                    }
                }
                return ActionResult.PASS;
            });

    ActionResult change();
}
