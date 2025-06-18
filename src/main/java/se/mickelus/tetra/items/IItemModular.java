package se.mickelus.tetra.items;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemModuleMajor;

public interface IItemModular {

    boolean isModuleRequired(String moduleSlot);

    int getNumMajorModules();

    String[] getMajorModuleKeys();

    String[] getMajorModuleNames();

    ItemModuleMajor[] getMajorModules(ItemStack itemStack);

    int getNumMinorModules();

    String[] getMinorModuleKeys();

    String[] getMinorModuleNames();

    ItemModule[] getMinorModules(ItemStack itemStack);

    ItemStack getDefaultStack();

    /**
     * Resets and applies effects for the current setup of modules & improvements. Applies enchantments and other things which cannot be emulated
     * through other means. Call this after each time the module setup changes.
     *
     * @param itemStack The modular item itemstack
     */
    void assemble(ItemStack itemStack, World world);

    ImmutableList<ResourceLocation> getTextures(ItemStack itemStack);
}
