package se.mickelus.tetra.blocks.forged;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.blocks.Materials;
import se.mickelus.tetra.blocks.TetraBlock;

// TODO: unused
public class BlockForgedPiping extends TetraBlock {
    private static final String UNLOCALIZED_NAME = "forged_piping";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static BlockForgedPiping INSTANCE;

    public BlockForgedPiping() {
        super(Materials.forged);

        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
        setBlockUnbreakable();
        setResistance(23);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.clear();
    }
}