package se.mickelus.tetra.capabilities;

import net.minecraft.item.ItemStack;

import java.util.Collection;

public interface ICapabilityProvider {
    int getCapabilityLevel(ItemStack itemStack, Capability capability);

    float getCapabilityEfficiency(ItemStack itemStack, Capability capability);

    Collection<Capability> getCapabilities(ItemStack itemStack);
}
