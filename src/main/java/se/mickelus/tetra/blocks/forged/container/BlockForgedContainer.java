package se.mickelus.tetra.blocks.forged.container;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.PropertyMatcher;
import se.mickelus.tetra.blocks.TetraBlock;
import se.mickelus.tetra.blocks.salvage.BlockInteraction;
import se.mickelus.tetra.blocks.salvage.IBlockCapabilityInteractive;
import se.mickelus.tetra.capabilities.Capability;
import se.mickelus.tetra.network.GuiHandlerRegistry;
import se.mickelus.tetra.network.PacketHandler;
import se.mickelus.tetra.network.TetraGuiHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Predicates.equalTo;

public class BlockForgedContainer extends TetraBlock implements ITileEntityProvider, IBlockCapabilityInteractive {
    public static final PropertyDirection PROP_FACING = BlockHorizontal.FACING;
    public static final PropertyBool PROP_FLIPPED = PropertyBool.create("flipped");
    // TODO: use PropertyEnum?
    public static final PropertyBool PROP_LOCKED_1 = PropertyBool.create("locked1");
    public static final PropertyBool PROP_LOCKED_2 = PropertyBool.create("locked2");
    public static final PropertyBool PROP_LOCKED_ADJACENT = PropertyBool.create("adjacent");
    public static final PropertyBool PROP_OPEN = PropertyBool.create("open");

    private static final BlockInteraction[] INTERACTIONS = new BlockInteraction[]{
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.SOUTH, 5, 7, 2, 5,
                    new PropertyMatcher().where(PROP_LOCKED_1, equalTo(true)).where(PROP_FLIPPED, equalTo(false)),
                    BlockForgedContainer::breakLock0),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.SOUTH, 11, 13, 2, 5,
                    new PropertyMatcher().where(PROP_LOCKED_2, equalTo(true)).where(PROP_FLIPPED, equalTo(false)),
                    BlockForgedContainer::breakLock1),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.SOUTH, 17, 19, 2, 5,
                    new PropertyMatcher().where(PROP_LOCKED_1, equalTo(true)).where(PROP_FLIPPED, equalTo(true)),
                    BlockForgedContainer::breakLock2),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.SOUTH, 23, 25, 2, 5,
                    new PropertyMatcher().where(PROP_LOCKED_2, equalTo(true)).where(PROP_FLIPPED, equalTo(true)),
                    BlockForgedContainer::breakLock3),
            new BlockInteraction(Capability.PRY, 1, EnumFacing.SOUTH, 1, 15, 3, 4,
                    new PropertyMatcher()
                            .where(PROP_LOCKED_1, equalTo(false))
                            .where(PROP_LOCKED_2, equalTo(false))
                            .where(PROP_LOCKED_ADJACENT, equalTo(false))
                            .where(PROP_OPEN, equalTo(false))
                            .where(PROP_FLIPPED, equalTo(false)),
                    BlockForgedContainer::open),
            new BlockInteraction(Capability.PRY, 1, EnumFacing.SOUTH, 15, 28, 3, 4,
                    new PropertyMatcher()
                            .where(PROP_LOCKED_1, equalTo(false))
                            .where(PROP_LOCKED_2, equalTo(false))
                            .where(PROP_LOCKED_ADJACENT, equalTo(false))
                            .where(PROP_OPEN, equalTo(false))
                            .where(PROP_FLIPPED, equalTo(true)),
                    BlockForgedContainer::open)
    };

    private static final AxisAlignedBB aabbZ1 = new AxisAlignedBB(0.0625, 0.0, -0.9375, 0.9375, 0.75, 0.9375);
    private static final AxisAlignedBB aabbZ2 = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.75, 1.9375);
    private static final AxisAlignedBB aabbX1 = new AxisAlignedBB(-0.9375, 0.0, 0.0625, 0.9375, 0.75, 0.9375);
    private static final AxisAlignedBB aabbX2 = new AxisAlignedBB(0.0625, 0.0, 0.0625, 1.9375, 0.75, 0.9375);

    public static final String UNLOCALIZED_NAME = "forged_container";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockForgedContainer INSTANCE;

    public BlockForgedContainer() {
        super(Material.IRON);
        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        GameRegistry.registerTileEntity(TileEntityForgedContainer.class, new ResourceLocation(Tags.MOD_ID, UNLOCALIZED_NAME));
        setCreativeTab(TetraCreativeTab.INSTANCE);

        setBlockUnbreakable();

        hasItem = true;

        setDefaultState(getBlockState().getBaseState()
                .withProperty(PROP_FACING, EnumFacing.EAST)
                .withProperty(PROP_FLIPPED, false));
    }

    @Override
    public void init(PacketHandler packetHandler) {
        GuiHandlerRegistry.INSTANCE.registerHandler(TetraGuiHandler.forgedContainerId, new GuiHandlerForgedContainer());
        packetHandler.registerPacket(ChangeCompartmentPacket.class, Side.SERVER);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
    }

    /**
     * Special item registration to to check that multiblock is allowed to be placed
     *
     * @param registry Item registry
     */
    @Override
    public void registerItem(IForgeRegistry<Item> registry) {
        Item item = new ItemBlockForgedContainer(this);
        item.setRegistryName(getRegistryName());
        registry.register(item);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("forged_description"));
    }

    private static void breakLock(IBlockAccess world, BlockPos pos, EntityPlayer player, int index) {
        TileEntityForgedContainer te = (TileEntityForgedContainer) world.getTileEntity(pos);
        if (te != null) {
            te.getOrDelegate().breakLock(player, index);
        }
    }

    private static boolean breakLock0(IBlockAccess world, BlockPos pos, IBlockState blockState, EntityPlayer player,
                                      EnumHand hand, EnumFacing facing) {
        breakLock(world, pos, player, 0);
        return true;
    }

    private static boolean breakLock1(IBlockAccess world, BlockPos pos, IBlockState blockState, EntityPlayer player,
                                      EnumHand hand, EnumFacing facing) {
        breakLock(world, pos, player, 1);
        return true;
    }

    private static boolean breakLock2(IBlockAccess world, BlockPos pos, IBlockState blockState, EntityPlayer player,
                                      EnumHand hand, EnumFacing facing) {
        breakLock(world, pos, player, 2);
        return true;
    }

    private static boolean breakLock3(IBlockAccess world, BlockPos pos, IBlockState blockState, EntityPlayer player,
                                      EnumHand hand, EnumFacing facing) {
        breakLock(world, pos, player, 3);
        return true;
    }

    private static boolean open(IBlockAccess world, BlockPos pos, IBlockState blockState, EntityPlayer player,
                                EnumHand hand, EnumFacing facing) {

        TileEntityForgedContainer te = (TileEntityForgedContainer) world.getTileEntity(pos);
        if (te != null) {
            te.getOrDelegate().open(player);
        }

        return true;
    }

    @Override
    public BlockInteraction[] getPotentialInteractions(IBlockState state, EnumFacing face, Collection<Capability> capabilities) {
        return Arrays.stream(INTERACTIONS)
                .filter(interaction -> interaction.isPotentialInteraction(state, state.getValue(PROP_FACING), face, capabilities))
                .toArray(BlockInteraction[]::new);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        boolean didInteract = BlockInteraction.attemptInteraction(world, getActualState(world.getBlockState(pos), world, pos), pos, player, hand,
                facing, hitX, hitY, hitZ);

        if (!didInteract) {
            TileEntityForgedContainer te = (TileEntityForgedContainer) world.getTileEntity(pos);
            if (te != null) {
                if (te.getOrDelegate().isOpen()) {
                    player.openGui(TetraMod.INSTANCE, TetraGuiHandler.forgedContainerId, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        } else {
            world.notifyBlockUpdate(pos, state, state, 3);
        }

        return true;
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB aabb = null;

        state = getActualState(state, source, pos);
        EnumFacing facing = state.getValue(PROP_FACING);
        boolean flipped = state.getValue(PROP_FLIPPED);
        boolean open = state.getValue(PROP_OPEN);

        if (flipped) {
            switch (facing) {
                case NORTH:
                    aabb = aabbX1;
                    break;
                case EAST:
                    aabb = aabbZ1;
                    break;
                case SOUTH:
                    aabb = aabbX2;
                    break;
                case WEST:
                    aabb = aabbZ2;
                    break;
            }
        } else {
            switch (facing) {
                case NORTH:
                    aabb = aabbX2;
                    break;
                case EAST:
                    aabb = aabbZ2;
                    break;
                case SOUTH:
                    aabb = aabbX1;
                    break;
                case WEST:
                    aabb = aabbZ1;
                    break;
            }
        }

        if (open && aabb != null) {
            aabb = aabb.setMaxY(0.5625);
        }

        return aabb;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROP_FACING, PROP_FLIPPED, PROP_LOCKED_1, PROP_LOCKED_2, PROP_LOCKED_ADJACENT, PROP_OPEN);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IBlockState actualState = super.getExtendedState(state, world, pos);
        TileEntityForgedContainer te = (TileEntityForgedContainer) world.getTileEntity(pos);

        if (te != null) {
            te = te.getOrDelegate();

            boolean anyLocked = Arrays.stream(te.getOrDelegate().isLocked()).anyMatch(isLocked -> isLocked);

            if (state.getValue(PROP_FLIPPED)) {
                actualState = actualState
                        .withProperty(PROP_LOCKED_1, te.isLocked(2))
                        .withProperty(PROP_LOCKED_2, te.isLocked(3));
            } else {
                actualState = actualState
                        .withProperty(PROP_LOCKED_1, te.isLocked(0))
                        .withProperty(PROP_LOCKED_2, te.isLocked(1));
            }

            actualState = actualState
                    .withProperty(PROP_OPEN, te.isOpen())
                    .withProperty(PROP_LOCKED_ADJACENT, anyLocked);
        }

        return actualState;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getDefaultState()
                .withProperty(PROP_FACING, EnumFacing.HORIZONTALS[meta & 0b11])
                .withProperty(PROP_FLIPPED, (meta >> 2 & 1) == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PROP_FACING).getHorizontalIndex()
                | (state.getValue(PROP_FLIPPED) ? 1 << 2 : 0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityForgedContainer();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        IBlockState iblockstate = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);

        return iblockstate.withProperty(PROP_FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        EnumFacing facing = state.getValue(PROP_FACING);
        worldIn.setBlockState(pos.offset(facing.rotateY()), getDefaultState().withProperty(PROP_FLIPPED, true).withProperty(PROP_FACING, facing));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        BlockPos relativePos;
        if (state.getValue(PROP_FLIPPED)) {
            relativePos = pos.offset(state.getValue(PROP_FACING).rotateYCCW());
        } else {
            relativePos = pos.offset(state.getValue(PROP_FACING).rotateY());
        }

        if (!equals(world.getBlockState(relativePos).getBlock())) {
            world.setBlockToAir(pos);
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        EnumFacing facing = state.getValue(PROP_FACING);

        if (Rotation.CLOCKWISE_180.equals(rot)
                || Rotation.CLOCKWISE_90.equals(rot) && (EnumFacing.NORTH.equals(facing) || EnumFacing.SOUTH.equals(facing))
                || Rotation.COUNTERCLOCKWISE_90.equals(rot) && (EnumFacing.EAST.equals(facing) || EnumFacing.WEST.equals(facing))) {
            state = state.withProperty(PROP_FLIPPED, state.getValue(PROP_FLIPPED));
        }

        return state.withProperty(PROP_FACING, rot.rotate(facing));
    }
}
