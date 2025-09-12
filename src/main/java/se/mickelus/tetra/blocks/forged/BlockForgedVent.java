package se.mickelus.tetra.blocks.forged;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.Materials;
import se.mickelus.tetra.blocks.PropertyMatcher;
import se.mickelus.tetra.blocks.TetraBlock;
import se.mickelus.tetra.blocks.salvage.BlockInteraction;
import se.mickelus.tetra.blocks.salvage.IBlockCapabilityInteractive;
import se.mickelus.tetra.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Predicates.equalTo;

public class BlockForgedVent extends TetraBlock implements IBlockCapabilityInteractive {
    private static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 3);
    private static final PropertyBool X = PropertyBool.create("x");
    private static final PropertyBool BROKEN = PropertyBool.create("broken");

    private static final String UNLOCALIZED_NAME = "forged_vent";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockForgedVent INSTANCE;

    private static final BlockInteraction[] INTERACTIONS = new BlockInteraction[]{
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.EAST, 1, 4, 12, 15,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(0)),
                    BlockForgedVent::breakBolt),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.EAST, 1, 4, 1, 4,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(1)),
                    BlockForgedVent::breakBolt),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.EAST, 12, 15, 12, 15,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(2)),
                    BlockForgedVent::breakBolt),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.EAST, 12, 15, 1, 4,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(3)),
                    BlockForgedVent::breakBolt),

            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.WEST, 12, 15, 12, 15,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(0)),
                    BlockForgedVent::breakBolt),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.WEST, 12, 15, 1, 4,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(1)),
                    BlockForgedVent::breakBolt),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.WEST, 1, 4, 12, 15,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(2)),
                    BlockForgedVent::breakBolt),
            new BlockInteraction(Capability.HAMMER, 3, EnumFacing.WEST, 1, 4, 1, 4,
                    new PropertyMatcher().where(BROKEN, equalTo(false)).where(ROTATION, equalTo(3)),
                    BlockForgedVent::breakBolt),

            new BlockInteraction(Capability.PRY, 1, EnumFacing.EAST, 7, 11, 8, 12,
                    new PropertyMatcher().where(BROKEN, equalTo(true)),
                    BlockForgedVent::breakPlate),
            new BlockInteraction(Capability.PRY, 1, EnumFacing.WEST, 7, 11, 8, 12,
                    new PropertyMatcher().where(BROKEN, equalTo(true)),
                    BlockForgedVent::breakPlate),
    };

    private static final ResourceLocation BOLT_LOOT_TABLE = TetraMod.getResource("forged/bolt_break");
    private static final ResourceLocation VENT_LOOT_TABLE = TetraMod.getResource("forged/vent_break");

    public BlockForgedVent() {
        super(Materials.forged);

        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
        setBlockUnbreakable();
        setResistance(22);

        hasItem = true;

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(ROTATION, 0)
                .withProperty(X, true)
                .withProperty(BROKEN, false));
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
    }

    private static boolean breakBolt(World world, BlockPos pos, IBlockState blockState, EntityPlayer player,
                                     EnumHand hand, EnumFacing facing) {
        world.setBlockState(pos, world.getBlockState(pos).withProperty(BROKEN, true), 2);

        if (!world.isRemote) {
            WorldServer worldServer = (WorldServer) world;
            LootTable table = worldServer.getLootTableManager().getLootTableFromLocation(BOLT_LOOT_TABLE);
            LootContext.Builder builder = new LootContext.Builder(worldServer);
            builder.withLuck(player.getLuck()).withPlayer(player);

            table.generateLootForPools(player.getRNG(), builder.build()).forEach(itemStack -> {
                if (!player.inventory.addItemStackToInventory(itemStack)) {
                    player.dropItem(itemStack, false);
                }
            });

            worldServer.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.PLAYERS, 1, 0.5f);
        }

        return true;
    }

    private static boolean breakPlate(World world, BlockPos pos, IBlockState blockState, EntityPlayer player,
                                      EnumHand hand, EnumFacing facing) {
        List<BlockPos> connectedVents = getConnectedBlocks(world, pos, new LinkedList<>(), blockState.getValue(X));

        if (connectedVents.stream().anyMatch(blockPos -> !world.getBlockState(blockPos).getValue(BROKEN))) {
            return false;
        }

        connectedVents.forEach(blockPos -> {
            world.playEvent(null, 2001, blockPos, Block.getStateId(world.getBlockState(blockPos)));
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
        });

        if (!world.isRemote) {
            WorldServer worldServer = (WorldServer) world;
            LootTable table = worldServer.getLootTableManager().getLootTableFromLocation(VENT_LOOT_TABLE);
            LootContext.Builder builder = new LootContext.Builder(worldServer);
            builder.withLuck(player.getLuck()).withPlayer(player);

            table.generateLootForPools(player.getRNG(), builder.build())
                    .forEach(itemStack -> spawnAsEntity(worldServer, pos, itemStack));
        }

        return true;
    }

    private static List<BlockPos> getConnectedBlocks(IBlockAccess world, BlockPos pos, List<BlockPos> visited, boolean isXAxis) {
        if (!visited.contains(pos) && world.getBlockState(pos).getBlock() instanceof BlockForgedVent) {
            visited.add(pos);

            getConnectedBlocks(world, pos.up(), visited, isXAxis);
            getConnectedBlocks(world, pos.down(), visited, isXAxis);

            if (isXAxis) {
                getConnectedBlocks(world, pos.east(), visited, isXAxis);
                getConnectedBlocks(world, pos.west(), visited, isXAxis);
            } else {
                getConnectedBlocks(world, pos.north(), visited, isXAxis);
                getConnectedBlocks(world, pos.south(), visited, isXAxis);
            }
        }

        return visited;
    }

    @Override
    public BlockInteraction[] getPotentialInteractions(IBlockState state, EnumFacing face, Collection<Capability> capabilities) {
        return Arrays.stream(INTERACTIONS)
                .filter(interaction -> interaction.isPotentialInteraction(state, state.getValue(X) ? EnumFacing.EAST : EnumFacing.SOUTH, face, capabilities))
                .toArray(BlockInteraction[]::new);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return BlockInteraction.attemptInteraction(world, state.getActualState(world, pos), pos, player, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(ChatFormatting.DARK_GRAY + I18n.format("forged_description"));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ROTATION, X, BROKEN);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
        iblockstate = iblockstate.withProperty(X, EnumFacing.Axis.X.equals(placer.getHorizontalFacing().getAxis()));

        int rotation = 0;

        if (EnumFacing.EAST.equals(placer.getHorizontalFacing()) || EnumFacing.SOUTH.equals(placer.getHorizontalFacing())) {
            rotation = 2;
        }

        if (facing != EnumFacing.UP && (facing == EnumFacing.DOWN || hitY > 0.5)) {
            rotation++;
        }

        iblockstate = iblockstate.withProperty(ROTATION, rotation);

        return iblockstate;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState blockState = this.getDefaultState();
        int rotation = meta & 3;
        if (rotation < EnumFacing.HORIZONTALS.length) {
            blockState = blockState.withProperty(ROTATION, rotation);
        }
        return blockState
                .withProperty(X, (meta >> 2 & 1) == 1)
                .withProperty(BROKEN, meta >> 3 == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ROTATION)
                | (state.getValue(X) ? 1 << 2 : 0)
                | (state.getValue(BROKEN) ? 1 << 3 : 0);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        boolean isXAxis = state.getValue(X);
        if (rot.equals(Rotation.CLOCKWISE_90) || rot.equals(Rotation.COUNTERCLOCKWISE_90)) {
            state = state.withProperty(X, !isXAxis);
        }
        if (rot.equals(Rotation.CLOCKWISE_180)
                || (!isXAxis && rot.equals(Rotation.CLOCKWISE_90))
                || (isXAxis && rot.equals(Rotation.COUNTERCLOCKWISE_90))) {
            return state.withProperty(ROTATION, state.getValue(ROTATION) ^ 2);
        }

        return state.withProperty(ROTATION, state.getValue(ROTATION));
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
        if (state.getValue(X)) {
            return new AxisAlignedBB(0, 0, 0.4375, 1, 1, 0.5625);
        }
        return new AxisAlignedBB(0.4375, 0, 0, 0.5625, 1, 1);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }
}
