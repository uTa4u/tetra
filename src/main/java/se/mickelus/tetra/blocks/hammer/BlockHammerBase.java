package se.mickelus.tetra.blocks.hammer;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
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
import se.mickelus.tetra.advancements.BlockUseCriterion;
import se.mickelus.tetra.blocks.TetraBlock;
import se.mickelus.tetra.blocks.salvage.BlockInteraction;
import se.mickelus.tetra.blocks.salvage.IBlockCapabilityInteractive;
import se.mickelus.tetra.capabilities.Capability;
import se.mickelus.tetra.items.ItemModular;
import se.mickelus.tetra.items.cell.ItemCellMagmatic;
import se.mickelus.tetra.items.forged.ItemVentPlate;
import se.mickelus.tetra.util.TileEntityOptional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlockHammerBase extends TetraBlock implements ITileEntityProvider, IBlockCapabilityInteractive {
    static final PropertyDirection FACING = BlockHorizontal.FACING;
    // TODO: turn into two instances of CellState enum (no, uncharged, charged)
    private static final PropertyBool CELL_1 = PropertyBool.create("cell1");
    private static final PropertyBool CELL_1_CHARGED = PropertyBool.create("cell1charged");
    private static final PropertyBool CELL_2 = PropertyBool.create("cell2");
    private static final PropertyBool CELL_2_CHARGED = PropertyBool.create("cell2charged");

    private static final ResourceLocation LOOT_TABLE = TetraMod.getResource("forged/plate_break");

    private static final String UNLOCALIZED_NAME = "hammer_base";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockHammerBase INSTANCE;

    private static final BlockInteraction[] INTERACTIONS = new BlockInteraction[]{
            new BlockInteraction(Capability.PRY, 1, HammerPlate.EAST.face, 5, 11, 9, 11,
                    HammerPlate.EAST.prop, true, (world, pos, blockState, player, hand, hitFace) ->
                    removePlate(world, pos, blockState, player, HammerPlate.EAST, hitFace)),
            new BlockInteraction(Capability.PRY, 1, HammerPlate.WEST.face, 5, 11, 9, 11,
                    HammerPlate.WEST.prop, true, (world, pos, blockState, player, hand, hitFace) ->
                    removePlate(world, pos, blockState, player, HammerPlate.WEST, hitFace)),

            new BlockInteraction(Capability.HAMMER, 1, EnumFacing.EAST, 6, 10, 2, 9,
                    HammerPlate.EAST.prop, false, (world, pos, blockState, player, hand, hitFace) ->
                    reconfigure(world, pos, blockState, player, EnumFacing.EAST)),
            new BlockInteraction(Capability.HAMMER, 1, EnumFacing.WEST, 6, 10, 2, 9,
                    HammerPlate.WEST.prop, false, (world, pos, blockState, player, hand, hitFace) ->
                    reconfigure(world, pos, blockState, player, EnumFacing.WEST))
    };

    public BlockHammerBase() {
        super(Material.IRON);

        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
        setBlockUnbreakable();

        GameRegistry.registerTileEntity(TileEntityHammerBase.class, new ResourceLocation(Tags.MOD_ID, UNLOCALIZED_NAME));

        hasItem = true;

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.EAST)
                .withProperty(CELL_1, false)
                .withProperty(CELL_1_CHARGED, false)
                .withProperty(CELL_2, false)
                .withProperty(CELL_2_CHARGED, false));
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
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TileEntityOptional.from(world, pos, TileEntityHammerBase.class)
                .map(te -> state
                        .withProperty(CELL_1, te.hasCellInSlot(0))
                        .withProperty(CELL_1_CHARGED, te.getCellFuel(0) > 0)
                        .withProperty(CELL_2, te.hasCellInSlot(1))
                        .withProperty(CELL_2_CHARGED, te.getCellFuel(1) > 0)
                        .withProperty(HammerPlate.EAST.prop, te.hasPlate(HammerPlate.EAST))
                        .withProperty(HammerPlate.WEST.prop, te.hasPlate(HammerPlate.WEST))
                        .withProperty(HammerConfig.propE, te.getConfiguration(EnumFacing.EAST))
                        .withProperty(HammerConfig.propW, te.getConfiguration(EnumFacing.WEST)))
                .orElse(state);
    }

    public boolean isFueled(World world, BlockPos pos) {
        return TileEntityOptional.from(world, pos, TileEntityHammerBase.class)
                .map(TileEntityHammerBase::isFueled)
                .orElse(false);
    }

    public void consumeFuel(World world, BlockPos pos) {
        TileEntityOptional.from(world, pos, TileEntityHammerBase.class)
                .ifPresent(te -> {
                    IBlockState blockState = world.getBlockState(pos);
                    te.consumeFuel();

                    world.notifyBlockUpdate(pos, blockState, blockState, 3);
                });
    }

    public void applyEffects(World world, BlockPos pos, ItemStack itemStack, EntityPlayer player) {
        TileEntityOptional.from(world, pos, TileEntityHammerBase.class)
                .ifPresent(te -> {
                    if (te.hasEffect(HammerEffect.DAMAGING) && itemStack.getItem() instanceof ItemModular) {
                        ItemModular item = (ItemModular) itemStack.getItem();
                        int damage = (int) (itemStack.getMaxDamage() * 0.1);
                        item.applyDamage(damage, itemStack, player);
                    }
                });
    }

    public int getHammerLevel(World world, BlockPos pos) {
        return TileEntityOptional.from(world, pos, TileEntityHammerBase.class)
                .map(TileEntityHammerBase::getHammerLevel)
                .orElse(0);
    }

    public static boolean removePlate(World world, BlockPos pos, IBlockState state, EntityPlayer player, HammerPlate plate, EnumFacing face) {
        TileEntityOptional.from(world, pos, TileEntityHammerBase.class)
                .ifPresent(te -> {
                    te.removePlate(plate);

                    if (!world.isRemote) {
                        WorldServer worldServer = (WorldServer) world;
                        LootTable table = worldServer.getLootTableManager().getLootTableFromLocation(LOOT_TABLE);
                        LootContext.Builder builder = new LootContext.Builder(worldServer);
                        builder.withLuck(player.getLuck()).withPlayer(player);

                        table.generateLootForPools(player.getRNG(), builder.build())
                                .forEach(itemStack -> spawnAsEntity(worldServer, pos, itemStack));
                    }

                    world.playSound(player, pos, SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1, 0.5f);
                    world.notifyBlockUpdate(pos, state, state, 3);
                });

        return true;
    }

    public static boolean reconfigure(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing adjustedFace) {
        TileEntityOptional.from(world, pos, TileEntityHammerBase.class)
                .ifPresent(te -> {
                    te.reconfigure(adjustedFace);
                    world.playSound(player, pos, SoundEvents.BLOCK_ANVIL_HIT, SoundCategory.PLAYERS, 1, 1);
                    world.notifyBlockUpdate(pos, state, state, 3);
                });

        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        EnumFacing blockFacing = state.getValue(FACING);
        TileEntityHammerBase te = TileEntityOptional.from(world, pos, TileEntityHammerBase.class).orElse(null);
        ItemStack heldStack = player.getHeldItem(hand);

        if (te == null) {
            return false;
        }

        if (blockFacing.getAxis().equals(facing.getAxis())) {
            int slotIndex = blockFacing.equals(facing) ? 0 : 1;
            if (te.hasCellInSlot(slotIndex)) {
                ItemStack cell = te.removeCellFromSlot(slotIndex);
                if (player.inventory.addItemStackToInventory(cell)) {
                    player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1, 1);
                } else {
                    spawnAsEntity(world, pos.offset(facing), cell);
                }

                world.playSound(player, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 0.5f, 0.6f);
                world.notifyBlockUpdate(pos, state, state, 3);

                if (!player.world.isRemote) {
                    BlockUseCriterion.trigger((EntityPlayerMP) player, getActualState(state, world, pos), ItemStack.EMPTY);
                }

                return true;
            } else if (heldStack.getItem() instanceof ItemCellMagmatic) {
                te.putCellInSlot(heldStack, slotIndex);
                player.setHeldItem(hand, ItemStack.EMPTY);
                world.playSound(player, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.PLAYERS, 0.5f, 0.5f);
                world.notifyBlockUpdate(pos, state, state, 3);

                if (!player.world.isRemote) {
                    BlockUseCriterion.trigger((EntityPlayerMP) player, getActualState(state, world, pos), heldStack);
                }

                return true;
            }
        } else if (heldStack.getItem() instanceof ItemVentPlate) {
            if (Rotation.CLOCKWISE_90.rotate(blockFacing).equals(facing) && !te.hasPlate(HammerPlate.EAST)) {
                te.attachPlate(HammerPlate.EAST);
                world.notifyBlockUpdate(pos, state, state, 3);

                if (!player.world.isRemote) {
                    BlockUseCriterion.trigger((EntityPlayerMP) player, getActualState(state, world, pos), heldStack);
                }

                heldStack.shrink(1);

                return true;
            } else if (Rotation.COUNTERCLOCKWISE_90.rotate(blockFacing).equals(facing) && !te.hasPlate(HammerPlate.WEST)) {
                te.attachPlate(HammerPlate.WEST);
                world.notifyBlockUpdate(pos, state, state, 3);

                if (!player.world.isRemote) {
                    BlockUseCriterion.trigger((EntityPlayerMP) player, getActualState(state, world, pos), heldStack);
                }

                heldStack.shrink(1);

                return true;
            }
        }

        return BlockInteraction.attemptInteraction(world, state.getActualState(world, pos), pos, player, hand, facing, hitX, hitY, hitZ);
    }

    public static void spawnAsEntity(World worldIn, BlockPos pos, ItemStack stack) {
        if (!worldIn.isRemote && !stack.isEmpty() && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
            if (captureDrops.get()) {
                capturedDrops.get().add(stack);
                return;
            }
            EntityItem entityitem = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
            entityitem.setDefaultPickupDelay();
            worldIn.spawnEntity(entityitem);
        }
    }

    @Override
    public BlockInteraction[] getPotentialInteractions(IBlockState state, EnumFacing face, Collection<Capability> capabilities) {
        return Arrays.stream(INTERACTIONS)
                .filter(interaction -> interaction.isPotentialInteraction(state, state.getValue(FACING), face, capabilities))
                .toArray(BlockInteraction[]::new);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityHammerBase();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, CELL_1, CELL_1_CHARGED, CELL_2, CELL_2_CHARGED,
                HammerPlate.EAST.prop, HammerPlate.WEST.prop, HammerConfig.propE, HammerConfig.propW);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(FACING, EnumFacing.HORIZONTALS[meta & 0xf]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }
}
