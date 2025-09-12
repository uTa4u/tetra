package se.mickelus.tetra.blocks.geode;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.items.TetraItem;
import se.mickelus.tetra.network.PacketHandler;

public class ItemPristineDiamond extends TetraItem {
    private static final String UNLOCALIZED_NAME = "pristine_diamond";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static ItemPristineDiamond INSTANCE;

    public ItemPristineDiamond() {
        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);
        setCreativeTab(TetraCreativeTab.INSTANCE);
    }

    @Override
    public void init(PacketHandler packetHandler) {
        OreDictionary.registerOre("gemDiamond", this);
    }
}
