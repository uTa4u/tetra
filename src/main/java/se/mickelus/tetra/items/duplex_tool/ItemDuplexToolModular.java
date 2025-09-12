package se.mickelus.tetra.items.duplex_tool;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.mickelus.tetra.ConfigHandler;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.blocks.workbench.BlockWorkbench;
import se.mickelus.tetra.capabilities.Capability;
import se.mickelus.tetra.items.BasicMajorModule;
import se.mickelus.tetra.items.BasicModule;
import se.mickelus.tetra.items.ItemModularHandheld;
import se.mickelus.tetra.module.ItemUpgradeRegistry;
import se.mickelus.tetra.module.Priority;
import se.mickelus.tetra.module.schema.BookEnchantSchema;
import se.mickelus.tetra.module.schema.RemoveSchema;
import se.mickelus.tetra.module.schema.RepairSchema;
import se.mickelus.tetra.network.PacketHandler;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;


public class ItemDuplexToolModular extends ItemModularHandheld {

    public final static String HEAD_LEFT_KEY = "duplex/head_left";
    public final static String HEAD_RIGHT_KEY = "duplex/head_right";

    public final static String HANDLE_KEY = "duplex/handle";
    public final static String BINDING_KEY = "duplex/binding";
    public final static String ACCESSORY_KEY = "duplex/accessory";

    public final static String LEFT_SUFFIX = "_left";
    public final static String RIGHT_SUFFIX = "_right";

    private static final String UNLOCALIZED_NAME = "duplex_tool_modular";

    public static DuplexHeadModule basicHammerHeadLeft;
    public static DuplexHeadModule basicHammerHeadRight;

    public static DuplexHeadModule basicAxeLeft;
    public static DuplexHeadModule basicAxeRight;

    public static DuplexHeadModule basicPickaxeLeft;
    public static DuplexHeadModule basicPickaxeRight;

    public static DuplexHeadModule hoeLeft;
    public static DuplexHeadModule hoeRight;

    public static DuplexHeadModule adzeLeft;
    public static DuplexHeadModule adzeRight;

    public static DuplexHeadModule sickleLeft;
    public static DuplexHeadModule sickleRight;

    public static DuplexHeadModule clawLeft;
    public static DuplexHeadModule clawRight;

    public static DuplexHeadModule butt;

    public static BasicMajorModule handle;

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static ItemDuplexToolModular INSTANCE;

    public ItemDuplexToolModular() {
        setTranslationKey(UNLOCALIZED_NAME);
        setRegistryName(UNLOCALIZED_NAME);
        setMaxStackSize(1);
        setCreativeTab(TetraCreativeTab.INSTANCE);

        entityHitDamage = 2;

        majorModuleKeys = new String[]{HEAD_LEFT_KEY, HEAD_RIGHT_KEY, HANDLE_KEY};
        minorModuleKeys = new String[]{BINDING_KEY};

        requiredModules = new String[]{HANDLE_KEY, HEAD_LEFT_KEY, HEAD_RIGHT_KEY};

        synergies = TetraMod.dataHandler.getSynergyData("modules/duplex/synergies");

        basicHammerHeadLeft = new DuplexHeadModule(HEAD_LEFT_KEY, "basic_hammer", LEFT_SUFFIX,
                "duplex/improvements/basic_hammer",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");
        basicHammerHeadRight = new DuplexHeadModule(HEAD_RIGHT_KEY, "basic_hammer", RIGHT_SUFFIX,
                "duplex/improvements/basic_hammer",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        basicAxeLeft = new DuplexHeadModule(HEAD_LEFT_KEY, "basic_axe", LEFT_SUFFIX,
                "duplex/improvements/basic_axe",
                "duplex/improvements/basic_axe_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");
        basicAxeRight = new DuplexHeadModule(HEAD_RIGHT_KEY, "basic_axe", RIGHT_SUFFIX,
                "duplex/improvements/basic_axe",
                "duplex/improvements/basic_axe_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        basicPickaxeLeft = new DuplexHeadModule(HEAD_LEFT_KEY, "basic_pickaxe", LEFT_SUFFIX,
                "duplex/improvements/basic_pickaxe",
                "duplex/improvements/basic_pickaxe_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");
        basicPickaxeRight = new DuplexHeadModule(HEAD_RIGHT_KEY, "basic_pickaxe", RIGHT_SUFFIX,
                "duplex/improvements/basic_pickaxe",
                "duplex/improvements/basic_pickaxe_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        hoeLeft = new DuplexHeadModule(HEAD_LEFT_KEY, "hoe", LEFT_SUFFIX,
                "duplex/improvements/hoe",
                "duplex/improvements/hoe_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");
        hoeRight = new DuplexHeadModule(HEAD_RIGHT_KEY, "hoe", RIGHT_SUFFIX,
                "duplex/improvements/hoe",
                "duplex/improvements/hoe_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        adzeLeft = new DuplexHeadModule(HEAD_LEFT_KEY, "adze", LEFT_SUFFIX,
                "duplex/improvements/adze",
                "duplex/improvements/adze_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");
        adzeRight = new DuplexHeadModule(HEAD_RIGHT_KEY, "adze", RIGHT_SUFFIX,
                "duplex/improvements/adze",
                "duplex/improvements/adze_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        sickleLeft = new DuplexHeadModule(HEAD_LEFT_KEY, "sickle", LEFT_SUFFIX,
                "duplex/improvements/sickle",
                "duplex/improvements/sickle_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");
        sickleRight = new DuplexHeadModule(HEAD_RIGHT_KEY, "sickle", RIGHT_SUFFIX,
                "duplex/improvements/sickle",
                "duplex/improvements/sickle_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        clawLeft = new DuplexHeadModule(HEAD_LEFT_KEY, "claw", LEFT_SUFFIX,
                "duplex/improvements/claw",
                "duplex/improvements/claw_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");
        clawRight = new DuplexHeadModule(HEAD_RIGHT_KEY, "claw", RIGHT_SUFFIX,
                "duplex/improvements/claw",
                "duplex/improvements/claw_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        butt = new DuplexHeadModule(HEAD_RIGHT_KEY, "butt", RIGHT_SUFFIX,
                "duplex/improvements/butt",
                "duplex/improvements/butt_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements");

        handle = new BasicMajorModule(HANDLE_KEY, "duplex/basic_handle",
                "duplex/improvements/basic_handle",
                "duplex/improvements/basic_handle_hone",
                "duplex/improvements/shared_head_hone",
                "settling_improvements",
                "destabilization_improvements")
                .withRenderLayer(Priority.LOWER);


        new BasicModule(BINDING_KEY, "duplex/binding", "duplex/tweaks/binding");
        new BasicModule(BINDING_KEY, "duplex/socket");

        updateConfig(ConfigHandler.honeDuplexBase, ConfigHandler.honeDuplexIntegrityMultiplier);
    }

    public void updateConfig(int honeBase, int honeIntegrityMultiplier) {
        this.honeBase = honeBase;
        this.honeIntegrityMultiplier = honeIntegrityMultiplier;
    }

    @Override
    public void init(PacketHandler packetHandler) {
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_hammer");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_hammer_hone");
        new BookEnchantSchema(basicHammerHeadLeft);
        new BookEnchantSchema(basicHammerHeadRight);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_axe");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_axe_hone");
        new BookEnchantSchema(basicAxeLeft);
        new BookEnchantSchema(basicAxeRight);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_pickaxe");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_pickaxe_hone");
        new BookEnchantSchema(basicPickaxeLeft);
        new BookEnchantSchema(basicPickaxeRight);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/hoe");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/hoe_hone");
        new BookEnchantSchema(hoeLeft);
        new BookEnchantSchema(hoeRight);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/adze");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/adze_hone");
        new BookEnchantSchema(adzeLeft);
        new BookEnchantSchema(adzeRight);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/sickle");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/sickle_hone");
        new BookEnchantSchema(sickleLeft);
        new BookEnchantSchema(sickleRight);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/claw");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/claw_hone");
        new BookEnchantSchema(clawLeft);
        new BookEnchantSchema(clawRight);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/butt");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/butt_hone");
        new BookEnchantSchema(butt);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_handle");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_handle_hone");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/basic_handle_improvements");
        new BookEnchantSchema(handle);

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/shared_head_hone");

        new RepairSchema(this);
        RemoveSchema.registerRemoveSchemas(this);

        ItemUpgradeRegistry.INSTANCE.registerReplacementDefinition("axe");
        ItemUpgradeRegistry.INSTANCE.registerReplacementDefinition("pickaxe");
        ItemUpgradeRegistry.INSTANCE.registerReplacementDefinition("hoe");

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/binding");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("duplex/socket");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs creativeTabs, NonNullList<ItemStack> itemList) {
        if (isInCreativeTab(creativeTabs)) {
            itemList.add(createHammerStack("log", "stick"));
            itemList.add(createHammerStack("obsidian", "iron"));
        }
    }

    public ItemStack createHammerStack(String headMaterial, String handleMaterial) {
        ItemStack itemStack = new ItemStack(this);

        basicHammerHeadLeft.addModule(itemStack, "basic_hammer/" + headMaterial, null);
        basicHammerHeadRight.addModule(itemStack, "basic_hammer/" + headMaterial, null);
        handle.addModule(itemStack, "basic_handle/" + handleMaterial, null);
        return itemStack;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public ImmutableList<ResourceLocation> getTextures(ItemStack itemStack) {
        return super.getTextures(itemStack);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.isSneaking() && world.getBlockState(pos).getBlock().equals(Blocks.CRAFTING_TABLE)
                && getCapabilityLevel(player.getHeldItem(hand), Capability.HAMMER) > 0) {
            return BlockWorkbench.upgradeWorkbench(player, world, pos, hand, side);
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    protected String getDisplayNamePrefixes(ItemStack itemStack) {
        String modulePrefix = Optional.ofNullable(getModuleFromSlot(itemStack, HEAD_LEFT_KEY))
                .map(module -> module.getItemPrefix(itemStack))
                .map(prefix -> prefix + " ")
                .orElse("");
        return Arrays.stream(getImprovements(itemStack))
                .map(improvement -> improvement.key + ".prefix")
                .filter(I18n::hasKey)
                .map(I18n::format)
                .findFirst()
                .map(prefix -> prefix + " " + modulePrefix)
                .orElse(modulePrefix);
    }

    @Override
    public double getDamageModifier(ItemStack itemStack) {
        if (isBroken(itemStack)) {
            return 0;
        }

        // only use the damage from the highest damaging head
        double damageModifier = Stream.of(getModuleFromSlot(itemStack, HEAD_LEFT_KEY), getModuleFromSlot(itemStack, HEAD_RIGHT_KEY))
                .filter(Objects::nonNull)
                .mapToDouble(module -> module.getDamageModifier(itemStack))
                .max()
                .orElse(0);

        damageModifier = getAllModules(itemStack).stream()
                .filter(itemModule -> !(HEAD_LEFT_KEY.equals(itemModule.getSlot()) || HEAD_RIGHT_KEY.equals(itemModule.getSlot())))
                .map(itemModule -> itemModule.getDamageModifier(itemStack))
                .reduce(damageModifier, Double::sum);

        damageModifier = Arrays.stream(getSynergyData(itemStack))
                .mapToDouble(synergyData -> synergyData.damage)
                .reduce(damageModifier, Double::sum);

        damageModifier = Arrays.stream(getSynergyData(itemStack))
                .mapToDouble(synergyData -> synergyData.damageMultiplier)
                .reduce(damageModifier, (a, b) -> a * b);

        return getAllModules(itemStack).stream()
                .map(itemModule -> itemModule.getDamageMultiplierModifier(itemStack))
                .reduce(damageModifier, (a, b) -> a * b);
    }
}


