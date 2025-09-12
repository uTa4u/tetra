package se.mickelus.tetra.blocks.forged.extractor;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.blocks.TetraBlock;

import javax.annotation.Nullable;
import java.util.List;

public class BlockSeepingBedrock extends TetraBlock {
    public static final PropertyInteger ACTIVE = PropertyInteger.create("active", 0, 15);
    private static final String UNLOCALIZED_NAME = "seeping_bedrock";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockSeepingBedrock INSTANCE;

    public BlockSeepingBedrock() {
        super(Material.ROCK);
        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);

        setBlockUnbreakable();

        hasItem = true;

        setDefaultState(getBlockState().getBaseState()
                .withProperty(ACTIVE, 15));
    }

    public static boolean isActive(IBlockAccess world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos);
        return INSTANCE.equals(blockState.getBlock()) && blockState.getValue(ACTIVE) > 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("forged_description"));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(ACTIVE, placer.isSneaking() ? 0 : 15);
    }
}
