package net.asodev.islandutils.util.sounds;

import net.asodev.islandutils.options.IslandSoundCategories;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class SoundBuilder {
    private Identifier identifier;
    private SoundCategory category;
    private float pitch = 1.0f;
    private boolean repeatable = false;

    public SoundBuilder setIdentifier(Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public SoundBuilder setCategory(SoundCategory category) {
        this.category = category;
        return this;
    }

    public SoundBuilder setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public SoundBuilder setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
        return this;
    }

    public SoundBuilder setMusic() {
        this.category = IslandSoundCategories.GAME_MUSIC;
        this.repeatable = true;
        return this;
    }

    public MCCSound build() {
        if (identifier == null) {
            throw new IllegalStateException("No identifier supplied");
        }
        if (category == null) {
            throw new IllegalStateException("No category supplied");
        }
        return new MCCSound(identifier, category, repeatable, pitch);
    }
}
