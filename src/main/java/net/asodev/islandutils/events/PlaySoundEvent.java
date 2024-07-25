package net.asodev.islandutils.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.sound.SoundEvent;

public interface PlaySoundEvent {
    Event<PlaySoundEvent> EVENT = EventFactory.createArrayBacked(PlaySoundEvent.class,
            listeners -> sound -> {
                for (PlaySoundEvent listener : listeners) {
                    listener.soundPlayed(sound);
                }
            });

    void soundPlayed(SoundEvent sound);
}
