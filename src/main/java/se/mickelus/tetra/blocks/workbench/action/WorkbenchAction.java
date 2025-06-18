package se.mickelus.tetra.blocks.workbench.action;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.blocks.workbench.TileEntityWorkbench;
import se.mickelus.tetra.capabilities.Capability;

public interface WorkbenchAction {

    String getKey();

    boolean canPerformOn(EntityPlayer player, ItemStack itemStack);

    Capability[] getRequiredCapabilitiesFor(ItemStack itemStack);

    int getCapabilityLevel(ItemStack itemStack, Capability capability);

    void perform(EntityPlayer player, ItemStack itemStack, TileEntityWorkbench workbench);
}
