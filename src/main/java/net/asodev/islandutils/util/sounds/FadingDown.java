package net.asodev.islandutils.util.sounds;

public class FadingDown implements Fading {
    private final float totalVolume;
    private final float totalFadeTicks;

    private float currentTick;

    public FadingDown(float totalVolume, float totalFadeTicks) {
        this.totalVolume = totalVolume;
        this.totalFadeTicks = totalFadeTicks;
    }

    @Override
    public boolean isDone() {
        return currentTick <= 0;
    }

    @Override
    public float currentVolume() {
        return totalVolume * (currentTick / totalFadeTicks);
    }

    @Override
    public void tick() {
        if (isDone()) return;
        currentTick--;
    }
}
