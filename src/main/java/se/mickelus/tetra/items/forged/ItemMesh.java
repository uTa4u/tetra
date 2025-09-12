package se.mickelus.tetra.items.forged;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.items.TetraItem;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMesh extends TetraItem {
    private static final String UNLOCALIZED_NAME = "forged_mesh";
    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static ItemMesh INSTANCE;

    public ItemMesh() {
        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("forged_description"));
    }
}
