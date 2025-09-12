package se.mickelus.tetra.blocks.geode;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.TetraBlock;
import se.mickelus.tetra.network.PacketHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;

public class BlockGeode extends TetraBlock {
    private static final String UNLOCALIZED_NAME = "block_geode";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockGeode INSTANCE;

    // TODO: make this not hacky??
    // hacky, but avoids some log warnings
    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0,
            (int) Arrays.stream(TetraMod.dataHandler.getData("geode/variants", GeodeVariant[].class)).count() - 1);

    public GeodeVariant[] variants = new GeodeVariant[0];

    private final GeodeVariant fallbackVariant = new GeodeVariant();

    public BlockGeode() {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 0);


        setTranslationKey(UNLOCALIZED_NAME);
        setRegistryName(UNLOCALIZED_NAME);

        setCreativeTab(TetraCreativeTab.INSTANCE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, 0));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, meta);
    }

    @Override
    public int getMetaFromState(IBlockState blockState) {
        return blockState.getValue(VARIANT);
    }

    private GeodeVariant getVariant(IBlockState state) {
        int index = state.getValue(VARIANT);
        if (index < variants.length) {
            return variants[index];
        }

        return fallbackVariant;
    }

    @Override
    public ItemStack getPickBlock(IBlockState blockState, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getSilkTouchDrop(blockState);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemGeode.INSTANCE;
    }

    @Override
    public int damageDropped(IBlockState blockState) {
        return getVariant(blockState).dropMeta;
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state) {
        GeodeVariant variant = getVariant(state);
        return new ItemStack(variant.block, 1, variant.blockMeta);
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return getVariant(blockState).hardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return getVariant(world.getBlockState(pos)).resistance;
    }

    @Override
    public void init(PacketHandler packetHandler) {
        if (ConfigHandler.geodeGenerate) {
            GameRegistry.registerWorldGenerator(new GeodeGenerator(), 10);
        }

        variants = Arrays.stream(TetraMod.dataHandler.getData("geode/variants", GeodeVariant[].class))
                .filter(variant -> variant.block != null)
                .toArray(GeodeVariant[]::new);
    }
}
