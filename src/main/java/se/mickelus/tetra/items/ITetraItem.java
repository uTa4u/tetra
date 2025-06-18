package se.mickelus.tetra.items;

import se.mickelus.tetra.network.PacketHandler;

public interface ITetraItem {
    void clientPreInit();

    void init(PacketHandler packetHandler);
}
