package se.mickelus.tetra.blocks.forged;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.blocks.Materials;
import se.mickelus.tetra.blocks.TetraBlock;

import javax.annotation.Nullable;
import java.util.List;

public class BlockForgedPlatform extends TetraBlock {
    private static final String UNLOCALIZED_NAME = "forged_platform";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockForgedPlatform INSTANCE;

    public BlockForgedPlatform() {
        super(Materials.forged);

        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
        setBlockUnbreakable();

        hasItem = true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("forged_description"));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        if (explosion.getPosition().y < pos.getY()) {
            return 4;
        }

        return 6;
    }
}