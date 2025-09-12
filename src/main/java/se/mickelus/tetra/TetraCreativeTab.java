package se.mickelus.tetra;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.items.duplex_tool.ItemDuplexToolModular;

public final class TetraCreativeTab extends CreativeTabs {
    public static final TetraCreativeTab INSTANCE = new TetraCreativeTab();

    private TetraCreativeTab() {
        super(Tags.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        return ItemDuplexToolModular.INSTANCE.createHammerStack("log", "stick");
    }
}
