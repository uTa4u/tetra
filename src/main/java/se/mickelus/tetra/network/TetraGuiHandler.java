package se.mickelus.tetra.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface TetraGuiHandler {
    int toolbeltId = 0;
    int workbenchId = 1;
    int forgedContainerId = 2;

    Object getServerGuiElement(EntityPlayer player, World world, int x, int y, int z);

    Object getClientGuiElement(EntityPlayer player, World world, int x, int y, int z);
}
