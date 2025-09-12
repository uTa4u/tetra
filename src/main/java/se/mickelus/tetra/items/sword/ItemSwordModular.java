package se.mickelus.tetra.items.sword;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.items.BasicMajorModule;
import se.mickelus.tetra.items.BasicModule;
import se.mickelus.tetra.items.ItemModularHandheld;
import se.mickelus.tetra.module.ItemModuleMajor;
import se.mickelus.tetra.module.ItemUpgradeRegistry;
import se.mickelus.tetra.module.Priority;
import se.mickelus.tetra.module.schema.BookEnchantSchema;
import se.mickelus.tetra.module.schema.RemoveSchema;
import se.mickelus.tetra.module.schema.RepairSchema;
import se.mickelus.tetra.network.PacketHandler;

public class ItemSwordModular extends ItemModularHandheld {
    private final static String BLADE_KEY = "sword/blade";
    private final static String HILT_KEY = "sword/hilt";

    private final static String GUARD_KEY = "sword/guard";
    private final static String POMMEL_KEY = "sword/pommel";
    private final static String FULLER_KEY = "sword/fuller";

    private static final String UNLOCALIZED_NAME = "sword_modular";

    private final ItemModuleMajor basicBladeModule;
    private final ItemModuleMajor shortBladeModule;
    private final ItemModuleMajor heavyBladeModule;
    private final ItemModuleMajor macheteModule;
    private final ItemModuleMajor hiltModule;

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static ItemSwordModular INSTANCE;

    public ItemSwordModular() {
        setTranslationKey(UNLOCALIZED_NAME);
        setRegistryName(UNLOCALIZED_NAME);
        setMaxStackSize(1);

        blockDestroyDamage = 2;

        majorModuleKeys = new String[]{BLADE_KEY, HILT_KEY};
        minorModuleKeys = new String[]{FULLER_KEY, GUARD_KEY, POMMEL_KEY};

        requiredModules = new String[]{BLADE_KEY, HILT_KEY};

        basicBladeModule = new BasicMajorModule(BLADE_KEY, "sword/basic_blade",
                "sword/improvements/shared_blade",
                "sword/improvements/shared_blade_hone",
                "sword/improvements/basic_blade",
                "settling_improvements",
                "destabilization_improvements");
        shortBladeModule = new BasicMajorModule(BLADE_KEY, "sword/short_blade",
                "sword/improvements/shared_blade",
                "sword/improvements/shared_blade_hone",
                "sword/improvements/short_blade",
                "settling_improvements",
                "destabilization_improvements");
        heavyBladeModule = new BasicMajorModule(BLADE_KEY, "sword/heavy_blade",
                "sword/improvements/shared_blade",
                "sword/improvements/shared_blade_hone",
                "sword/improvements/heavy_blade",
                "settling_improvements",
                "destabilization_improvements");
        macheteModule = new BasicMajorModule(BLADE_KEY, "sword/machete",
                "sword/improvements/shared_blade",
                "sword/improvements/shared_blade_hone",
                "settling_improvements",
                "destabilization_improvements");

        hiltModule = new BasicMajorModule(HILT_KEY, "sword/basic_hilt",
                "sword/improvements/shared_hilt",
                "sword/improvements/shared_hilt_hone",
                "settling_improvements",
                "destabilization_improvements")
                .withRenderLayer(Priority.LOWER);

        new BasicModule(GUARD_KEY, "sword/makeshift_guard");
        new BasicModule(GUARD_KEY, "sword/wide_guard");
        new BasicModule(GUARD_KEY, "sword/forefinger_ring");
        new BasicModule(GUARD_KEY, "sword/binding", "sword/tweaks/binding");
        new BasicModule(GUARD_KEY, "sword/socket");

        new BasicModule(POMMEL_KEY, "sword/decorative_pommel");
        new BasicModule(POMMEL_KEY, "sword/counterweight");
        new BasicModule(POMMEL_KEY, "sword/grip_loop");

        new BasicModule(FULLER_KEY, "sword/reinforced_fuller");

        updateConfig(ConfigHandler.honeSwordBase, ConfigHandler.honeSwordIntegrityMultiplier);
    }

    @Override
    public void init(PacketHandler packetHandler) {
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/basic_blade");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/basic_blade_improvements");
        new BookEnchantSchema(basicBladeModule);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/short_blade");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/short_blade_improvements");
        new BookEnchantSchema(shortBladeModule);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/heavy_blade");
        new BookEnchantSchema(heavyBladeModule);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/machete");
        new BookEnchantSchema(macheteModule);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/basic_hilt");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/basic_hilt_improvements");
        new BookEnchantSchema(hiltModule);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/wide_guard");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/counterweight");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/grip_loop");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/forefinger_ring");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/binding");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/reinforced_fuller");

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/shared_blade_hone");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/shared_hilt_hone");

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("sword/socket");

        new RepairSchema(this);
        RemoveSchema.registerRemoveSchemas(this);

        ItemUpgradeRegistry.INSTANCE.registerReplacementDefinition("sword");
    }

    public void updateConfig(int honeBase, int honeIntegrityMultiplier) {
        this.honeBase = honeBase;
        this.honeIntegrityMultiplier = honeIntegrityMultiplier;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockState) {
        return blockState.getBlock() == Blocks.WEB;
    }
}
