package se.mickelus.tetra.blocks.forged;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
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


public class BlockForgedPlatformSlab extends TetraBlock {
    private static final PropertyEnum<BlockSlab.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockSlab.EnumBlockHalf.class);
    private static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    private static final AxisAlignedBB AABB_TOP_HALF = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

    private static final String UNLOCALIZED_NAME = "forged_platform_slab";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockForgedPlatformSlab INSTANCE;

    public BlockForgedPlatformSlab() {
        super(Materials.forged);

        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
        setBlockUnbreakable();

        hasItem = true;

        fullBlock = false;

        this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM));
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

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HALF, meta == 1 ? BlockSlab.EnumBlockHalf.TOP : BlockSlab.EnumBlockHalf.BOTTOM);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            return 1;
        }

        return 0;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP ? AABB_TOP_HALF : AABB_BOTTOM_HALF;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face == EnumFacing.UP && state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            return BlockFaceShape.SOLID;
        } else if (face == EnumFacing.DOWN && state.getValue(HALF) == BlockSlab.EnumBlockHalf.BOTTOM) {
            return BlockFaceShape.SOLID;
        }
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling) {
            return false;
        }

        BlockSlab.EnumBlockHalf side = state.getValue(HALF);
        return (side == BlockSlab.EnumBlockHalf.TOP && face == EnumFacing.UP) || (side == BlockSlab.EnumBlockHalf.BOTTOM && face == EnumFacing.DOWN);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);

        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? iblockstate : iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.TOP);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (super.shouldSideBeRendered(blockState, blockAccess, pos, side)) {
            return true;
        }
        return !(side != EnumFacing.UP && side != EnumFacing.DOWN);
    }
}