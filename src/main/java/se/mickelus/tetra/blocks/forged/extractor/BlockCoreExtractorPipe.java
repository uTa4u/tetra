package se.mickelus.tetra.blocks.forged.extractor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.blocks.TetraBlock;

public class BlockCoreExtractorPipe extends TetraBlock {
    public static final PropertyDirection FACING = BlockDirectional.FACING;
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private static final String UNLOCALIZED_NAME = "extractor_pipe";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockCoreExtractorPipe INSTANCE;

    public BlockCoreExtractorPipe() {
        super(Material.IRON);
        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);

        setBlockUnbreakable();
        setResistance(22);

        hasItem = true;

        setDefaultState(getBlockState().getBaseState()
                .withProperty(FACING, EnumFacing.UP)
                .withProperty(POWERED, false));
    }

    public static boolean isPowered(IBlockAccess world, BlockPos pos) {
        IBlockState pipeState = world.getBlockState(pos.down());
        return INSTANCE.equals(pipeState.getBlock()) && pipeState.getValue(POWERED);
    }

    private boolean shouldGetPower(IBlockAccess world, BlockPos pos, EnumFacing blockFacing) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (!facing.equals(blockFacing)) {
                IBlockState adjacent = world.getBlockState(pos.offset(facing));
                if (adjacent.getBlock().equals(this)
                        && facing.equals(adjacent.getValue(FACING).getOpposite())
                        && adjacent.getValue(POWERED)) {
                    return true;
                }
            }
        }

        return BlockSeepingBedrock.isActive(world, pos.offset(blockFacing.getOpposite()));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block fromBlock, BlockPos fromPos) {
        boolean getsPowered = shouldGetPower(world, pos, state.getValue(FACING));

        if (state.getValue(POWERED) != getsPowered) {
            world.setBlockState(pos, state.withProperty(POWERED, getsPowered));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, POWERED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.VALUES[meta & 3])
                .withProperty(POWERED, (meta >> 3 & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex()
                | (state.getValue(POWERED) ? 1 << 3 : 0);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer)
                .withProperty(FACING, facing)
                .withProperty(POWERED, shouldGetPower(world, pos, facing));
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }
}
