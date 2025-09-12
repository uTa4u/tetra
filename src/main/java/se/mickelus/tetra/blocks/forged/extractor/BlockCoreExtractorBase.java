package se.mickelus.tetra.blocks.forged.extractor;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.blocks.TetraBlock;
import se.mickelus.tetra.util.TileEntityOptional;

import javax.annotation.Nullable;
import java.util.List;

public class BlockCoreExtractorBase extends TetraBlock implements ITileEntityProvider {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final String UNLOCALIZED_NAME = "core_extractor";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockCoreExtractorBase INSTANCE;

    public BlockCoreExtractorBase() {
        super(Material.IRON);
        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        GameRegistry.registerTileEntity(TileEntityCoreExtractorBase.class, new ResourceLocation(Tags.MOD_ID, UNLOCALIZED_NAME));
        setCreativeTab(TetraCreativeTab.INSTANCE);

        setBlockUnbreakable();

        hasItem = true;

        setDefaultState(getBlockState().getBaseState()
                .withProperty(FACING, EnumFacing.EAST));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("forged_description"));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block fromBlock, BlockPos fromPos) {
        if (!pos.offset(world.getBlockState(pos).getValue(FACING)).equals(fromPos)) {
            TileEntityOptional.from(world, pos, TileEntityCoreExtractorBase.class)
                    .ifPresent(TileEntityCoreExtractorBase::updateTransferState);
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.HORIZONTALS[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCoreExtractorBase();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState iblockstate = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);

        return iblockstate.withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }
}
