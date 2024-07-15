package net.asodev.islandutils.state;

import com.noxcrew.noxesium.network.NoxesiumPackets;
import com.noxcrew.noxesium.network.clientbound.ClientboundMccServerPacket;

public class IslandStateHolder {
    private IslandState state;

    public IslandStateHolder() {
        NoxesiumPackets.MCC_SERVER.addListener(this, (any, packet, ctx) -> state = fromPacket(packet));
    }

    private IslandState fromPacket(ClientboundMccServerPacket packet) throws IllegalArgumentException {
        return switch (packet.associatedGame()) {
            case "hub" -> new Hub();
            default -> throw new IllegalStateException("Unexpected packet: " + packet.associatedGame());
        };
    }

    public IslandState getState() {
        return state;
    }
}
