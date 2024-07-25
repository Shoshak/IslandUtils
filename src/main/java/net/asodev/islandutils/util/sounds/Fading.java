package net.asodev.islandutils.util.sounds;

public interface Fading {
    float currentVolume();

    boolean isDone();

    void tick();
}
