package net.asodev.islandutils.util.sounds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;

public class SoundUtil {
    public static void playSound(AbstractSoundInstance sound) {
        MinecraftClient.getInstance().getSoundManager().play(sound);
    }
}
