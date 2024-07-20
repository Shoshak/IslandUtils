package net.asodev.islandutils.commands;

import net.asodev.islandutils.modules.crafting.state.CraftingNotifier;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class CraftingCommands {
    public CraftingCommands(CraftingNotifier craftingNotifier) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommandManager
                        .literal("crafting")
                        .executes(context -> {
                            context.getSource().sendFeedback(craftingNotifier.activeCraftsMessage());
                            return 1;
                        })
        ));
    }
}
