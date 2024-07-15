package net.asodev.islandutils.modules;

import com.noxcrew.noxesium.network.NoxesiumPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoxesiumIntegration {
    private final Logger LOGGER = LoggerFactory.getLogger(NoxesiumIntegration.class);

    public void init() {
        NoxesiumPackets.MCC_SERVER.addListener(this, (any, packet, ctx) -> {
            handleServerPacket(packet);
        });
    }
}
