package net.asodev.islandutils.state;

import com.noxcrew.noxesium.network.NoxesiumPackets;
import com.noxcrew.noxesium.network.clientbound.ClientboundMccServerPacket;
import net.asodev.islandutils.events.StateChangeEvent;
import net.asodev.islandutils.state.states.Hub;
import net.asodev.islandutils.state.states.PKWD;
import net.asodev.islandutils.state.states.PKWS;

public class IslandStateHolder {
    public IslandStateHolder() {
        NoxesiumPackets.MCC_SERVER.addListener(this, (any, packet, ctx) -> eventFromPacket(packet));
    }

    private void eventFromPacket(ClientboundMccServerPacket packet) throws IllegalArgumentException {
        IslandState islandState = switch (packet.associatedGame()) {
            case "hub" -> new Hub();
            case "parkour_warrior" -> {
                if (packet.subType().contains("survival")) {
                    yield new PKWS(false);
                } else {
                    yield new PKWD();
                }
            }
            default -> null;
        };
        if (islandState != null) {
            StateChangeEvent.EVENT.invoker().change(islandState);
        }
    }
}
