package se.mickelus.tetra.blocks.forged;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
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

public class BlockForgedPillar extends TetraBlock {
    private static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);

    private static final String UNLOCALIZED_NAME = "forged_pillar";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockForgedPillar INSTANCE;

    public BlockForgedPillar() {
        super(Materials.forged);

        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
        setBlockUnbreakable();
        setResistance(25);

        hasItem = true;

        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta < EnumFacing.Axis.values().length) {
            return this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.values()[meta]);
        }
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS).ordinal();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case X:
                        return state.withProperty(AXIS, EnumFacing.Axis.Z);
                    case Z:
                        return state.withProperty(AXIS, EnumFacing.Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }
}
