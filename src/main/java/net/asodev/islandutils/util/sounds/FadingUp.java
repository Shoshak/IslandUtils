package net.asodev.islandutils.util.sounds;

public class FadingUp implements Fading {
    private final float totalVolume;
    private final float totalFadeTicks;

    private float currentTick;

    public FadingUp(float totalVolume, float totalFadeTicks) {
        this.totalVolume = totalVolume;
        this.totalFadeTicks = totalFadeTicks;
    }

    @Override
    public float currentVolume() {
        return totalVolume * (currentTick / totalFadeTicks);
    }

    @Override
    public boolean isDone() {
        return currentTick == totalFadeTicks;
    }

    @Override
    public void tick() {
        if (isDone()) return;
        currentTick++;
    }
}
