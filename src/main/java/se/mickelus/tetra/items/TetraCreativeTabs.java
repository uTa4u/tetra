package se.mickelus.tetra.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.items.duplex_tool.ItemDuplexToolModular;

public class TetraCreativeTabs extends CreativeTabs {

    private static TetraCreativeTabs instance;

    public TetraCreativeTabs() {
        super("tetra");

        instance = this;
    }

    @Override
    public ItemStack createIcon() {
        return ItemDuplexToolModular.instance.createHammerStack("log", "stick");
    }

    public static TetraCreativeTabs getInstance() {
        return instance;
    }

}
