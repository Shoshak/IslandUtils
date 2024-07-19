package net.asodev.islandutils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundUtil {
    public static void playSound(Identifier sound) {
        MinecraftClient instance = MinecraftClient.getInstance();
        assert instance.player != null;
        SoundEvent soundEvent = SoundEvent.of(sound);
        instance.player.playSound(soundEvent);
    }
}
