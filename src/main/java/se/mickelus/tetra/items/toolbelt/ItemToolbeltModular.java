package se.mickelus.tetra.items.toolbelt;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraCreativeTab;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.items.BasicModule;
import se.mickelus.tetra.items.ItemModular;
import se.mickelus.tetra.items.toolbelt.booster.JumpHandlerBooster;
import se.mickelus.tetra.items.toolbelt.booster.TickHandlerBooster;
import se.mickelus.tetra.items.toolbelt.booster.UpdateBoosterPacket;
import se.mickelus.tetra.items.toolbelt.inventory.InventoryToolbelt;
import se.mickelus.tetra.module.ItemEffect;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemUpgradeRegistry;
import se.mickelus.tetra.module.schema.RemoveSchema;
import se.mickelus.tetra.network.GuiHandlerRegistry;
import se.mickelus.tetra.network.PacketHandler;
import se.mickelus.tetra.util.IntegrationHelper;

import java.util.*;
import java.util.stream.Collectors;


@Optional.Interface(modid = IntegrationHelper.baublesModId, iface = IntegrationHelper.baublesApiClass)
public class ItemToolbeltModular extends ItemModular implements IBauble {

    private final static String UNLOCALIZED_NAME = "toolbelt_modular";

    public final static String SLOT_1_KEY = "toolbelt/slot1";
    public final static String SLOT_2_KEY = "toolbelt/slot2";
    public final static String SLOT_3_KEY = "toolbelt/slot3";
    public final static String BELT_KEY = "toolbelt/belt";

    public final static String SLOT_1_SUFFIX = "_slot1";
    public final static String SLOT_2_SUFFIX = "_slot2";
    public final static String SLOT_3_SUFFIX = "_slot3";

    private final ItemModule defaultBelt;
    private final ItemModule defaultStrap;

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static ItemToolbeltModular INSTANCE;

    public ItemToolbeltModular() {
        super();

        setRegistryName(UNLOCALIZED_NAME);
        setTranslationKey(UNLOCALIZED_NAME);

        setMaxStackSize(1);

        setCreativeTab(TetraCreativeTab.INSTANCE);

        majorModuleKeys = new String[]{SLOT_1_KEY, SLOT_2_KEY, SLOT_3_KEY};
        minorModuleKeys = new String[]{BELT_KEY};

        requiredModules = new String[]{BELT_KEY};

        defaultBelt = new BasicModule(BELT_KEY, BELT_KEY);

        defaultStrap = new ToolbeltModule(SLOT_1_KEY, "strap", SLOT_1_SUFFIX);
        new ToolbeltModule(SLOT_2_KEY, "strap", SLOT_2_SUFFIX);
        new ToolbeltModule(SLOT_3_KEY, "strap", SLOT_3_SUFFIX);

        new ToolbeltModule(SLOT_1_KEY, "potion_storage", SLOT_1_SUFFIX);
        new ToolbeltModule(SLOT_2_KEY, "potion_storage", SLOT_2_SUFFIX);
        new ToolbeltModule(SLOT_3_KEY, "potion_storage", SLOT_3_SUFFIX);

        new ToolbeltModule(SLOT_1_KEY, "storage", SLOT_1_SUFFIX);
        new ToolbeltModule(SLOT_2_KEY, "storage", SLOT_2_SUFFIX);
        new ToolbeltModule(SLOT_3_KEY, "storage", SLOT_3_SUFFIX);

        new ToolbeltModule(SLOT_1_KEY, "quiver", SLOT_1_SUFFIX);
        new ToolbeltModule(SLOT_2_KEY, "quiver", SLOT_2_SUFFIX);
        new ToolbeltModule(SLOT_3_KEY, "quiver", SLOT_3_SUFFIX);

        new ToolbeltModule(SLOT_1_KEY, "booster", SLOT_1_SUFFIX);
        new ToolbeltModule(SLOT_2_KEY, "booster", SLOT_2_SUFFIX);
        new ToolbeltModule(SLOT_3_KEY, "booster", SLOT_3_SUFFIX);
    }

    @Override
    public void clientPreInit() {
        super.clientPreInit();
        MinecraftForge.EVENT_BUS.register(new JumpHandlerBooster(Minecraft.getMinecraft()));
    }

    @Override
    public void init(PacketHandler packetHandler) {
        GuiHandlerRegistry.INSTANCE.registerHandler(GuiHandlerToolbelt.toolbeltId, new GuiHandlerToolbelt());

        packetHandler.registerPacket(EquipToolbeltItemPacket.class, Side.SERVER);
        packetHandler.registerPacket(UpdateBoosterPacket.class, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new TickHandlerBooster());

        InventoryToolbelt.initializePredicates();

        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/belt");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/strap");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/strap_improvements");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/booster");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/potion_storage");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/storage");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/storage_improvements");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/quiver");
        ItemUpgradeRegistry.INSTANCE.registerConfigSchema("toolbelt/quiver_improvements");

        RemoveSchema.registerRemoveSchemas(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs creativeTabs, NonNullList<ItemStack> itemList) {
        if (isInCreativeTab(creativeTabs)) {
            itemList.add(createDefaultStack());
        }
    }

    private ItemStack createDefaultStack() {
        ItemStack itemStack = new ItemStack(this);
        defaultBelt.addModule(itemStack, "belt/rope", null);
        defaultStrap.addModule(itemStack, "strap1/leather", null);
        return itemStack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.openGui(TetraMod.INSTANCE, GuiHandlerToolbelt.toolbeltId, world, hand.ordinal(), 0, 0);

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public int getNumSlots(ItemStack itemStack, SlotType slotType) {
        return getAllModules(itemStack).stream()
                .map(module -> module.getEffectLevel(itemStack, slotType.effect))
                .reduce(0, Integer::sum);
    }

    public List<Collection<ItemEffect>> getSlotEffects(ItemStack itemStack, SlotType slotType) {
        return getAllModules(itemStack).stream()
                .filter(module -> module.getEffects(itemStack).contains(slotType.effect))
                .map(module -> {
                    EnumMap<ItemEffect, Integer> effectLevelMap = new EnumMap<>(ItemEffect.class);
                    ((Collection<ItemEffect>) module.getEffects(itemStack)).stream()
                            .filter(itemEffect -> !itemEffect.equals(slotType.effect))
                            .forEach(itemEffect -> effectLevelMap.put(itemEffect, module.getEffectLevel(itemStack, itemEffect)));


                    int slotCount = module.getEffectLevel(itemStack, slotType.effect);
                    Collection<Collection<ItemEffect>> result = new ArrayList<>(slotCount);
                    for (int i = 0; i < slotCount; i++) {
                        ArrayList<ItemEffect> slotEffects = new ArrayList<>();
                        for (Map.Entry<ItemEffect, Integer> entry : effectLevelMap.entrySet()) {
                            if (entry.getValue() > i) {
                                slotEffects.add(entry.getKey());
                            }
                        }
                        result.add(slotEffects);
                    }

                    return result;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Tells baubles which slot this item can go into. Implements a method in the IBauble interface.
     *
     * @param itemstack The itemstack
     * @return
     */
    @Optional.Method(modid = IntegrationHelper.baublesModId)
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.BELT;
    }
}
