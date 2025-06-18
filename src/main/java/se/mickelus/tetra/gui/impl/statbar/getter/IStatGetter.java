package se.mickelus.tetra.gui.impl.statbar.getter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IStatGetter {

    default boolean shouldShow(EntityPlayer player, ItemStack currentStack, ItemStack previewStack) {
        double baseValue = getValue(player, ItemStack.EMPTY);
        return getValue(player, currentStack) > baseValue || getValue(player, previewStack) > baseValue;
    }

    double getValue(EntityPlayer player, ItemStack itemStack);

    double getValue(EntityPlayer player, ItemStack itemStack, String slot);

    double getValue(EntityPlayer player, ItemStack itemStack, String slot, String improvement);
}
