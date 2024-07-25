package net.asodev.islandutils.commands;

import net.asodev.islandutils.util.MusicUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MusicCommands {
    public MusicCommands(MusicUtil music) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager
                        .literal("resetmusic")
                        .executes(context -> {
                            music.stopMusicInstant();
                            context.getSource().sendFeedback(Text
                                    .literal("Reset your music!")
                                    .setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
                            return 1;
                        })
        ));
    }
}
