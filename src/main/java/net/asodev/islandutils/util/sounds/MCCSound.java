package net.asodev.islandutils.util.sounds;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class MCCSound extends AbstractSoundInstance implements TickableSoundInstance {
    private Fading fading;
    private boolean shouldStop;

    public MCCSound(Identifier identifier, SoundCategory category, boolean repeatable, float pitch) {
        super(identifier, category, Random.create());
        this.shouldStop = false;
        this.repeat = repeatable;
        this.pitch = pitch;
    }

    @Override
    public boolean isDone() {
        return shouldStop && (fading == null || fading.isDone());
    }

    @Override
    public void tick() {
        if (fading == null) return;
        this.volume = fading.currentVolume();
        fading.tick();
    }

    public void setFading(Fading fading) {
        this.fading = fading;
    }

    public void shouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }
}
