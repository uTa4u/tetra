package se.mickelus.tetra.blocks.geode;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.items.TetraCreativeTabs;
import se.mickelus.tetra.items.TetraItem;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGeode extends TetraItem {
    private static final String unlocalizedName = "geode";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + unlocalizedName)
    public static ItemGeode instance;

    public ItemGeode() {
        setRegistryName(unlocalizedName);
        setTranslationKey(unlocalizedName);
        setCreativeTab(TetraCreativeTabs.getInstance());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(I18n.format("item.geode.tooltip"));
    }
}
