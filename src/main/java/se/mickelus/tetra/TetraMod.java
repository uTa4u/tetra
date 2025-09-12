package se.mickelus.tetra;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.advancements.critereon.ItemPredicates;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import se.mickelus.tetra.advancements.*;
import se.mickelus.tetra.blocks.ITetraBlock;
import se.mickelus.tetra.blocks.forged.*;
import se.mickelus.tetra.blocks.forged.container.BlockForgedContainer;
import se.mickelus.tetra.blocks.forged.extractor.BlockCoreExtractorBase;
import se.mickelus.tetra.blocks.forged.extractor.BlockCoreExtractorPipe;
import se.mickelus.tetra.blocks.forged.extractor.BlockCoreExtractorPiston;
import se.mickelus.tetra.blocks.forged.extractor.BlockSeepingBedrock;
import se.mickelus.tetra.blocks.forged.transfer.BlockTransferUnit;
import se.mickelus.tetra.blocks.geode.*;
import se.mickelus.tetra.blocks.hammer.BlockHammerBase;
import se.mickelus.tetra.blocks.hammer.BlockHammerHead;
import se.mickelus.tetra.blocks.workbench.BlockWorkbench;
import se.mickelus.tetra.data.DataHandler;
import se.mickelus.tetra.generation.TGenCommand;
import se.mickelus.tetra.generation.WorldGenFeatures;
import se.mickelus.tetra.items.ITetraItem;
import se.mickelus.tetra.items.ItemPredicateModular;
import se.mickelus.tetra.items.cell.ItemCellMagmatic;
import se.mickelus.tetra.items.duplex_tool.ItemDuplexToolModular;
import se.mickelus.tetra.items.forged.*;
import se.mickelus.tetra.items.journal.ItemJournal;
import se.mickelus.tetra.items.sword.ItemSwordModular;
import se.mickelus.tetra.items.toolbelt.ItemToolbeltModular;
import se.mickelus.tetra.loot.FortuneBonusCondition;
import se.mickelus.tetra.loot.FortuneBonusFunction;
import se.mickelus.tetra.loot.SetMetadataFunction;
import se.mickelus.tetra.module.ItemEffectHandler;
import se.mickelus.tetra.module.improvement.DestabilizationEffect;
import se.mickelus.tetra.module.improvement.HonePacket;
import se.mickelus.tetra.module.improvement.SettlePacket;
import se.mickelus.tetra.module.schema.CleanseSchema;
import se.mickelus.tetra.network.GuiHandlerRegistry;
import se.mickelus.tetra.network.PacketHandler;
import se.mickelus.tetra.potions.PotionBleeding;
import se.mickelus.tetra.potions.PotionEarthbound;
import se.mickelus.tetra.proxy.IProxy;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

// TODO:
//  1. Backport features from 1.20.1
//  2. Make structures spawn under villages
//  3. Add tetra village house with some loot
//  4. Add tetra villager
//  5. Draedon-lab-like new structures with epic loot
//  6. An analogue for TC's smeltery
//  7. Compat with the same materials as TC
//  8. Compat with GroovyScript
//  9. Refactor project structure to be more conventional (gui and item classes in blocks package, wtf???)
//  10. Remove data folder (we won't have datapacks, so no reason for this overcomplication)
@Mod(modid = Tags.MOD_ID, version = Tags.VERSION)
public class TetraMod {
    @SidedProxy(clientSide = "se.mickelus.tetra.proxy.ClientProxy", serverSide = "se.mickelus.tetra.proxy.ServerProxy")
    public static IProxy proxy;

    @Mod.Instance(Tags.MOD_ID)
    public static TetraMod INSTANCE;

    public static DataHandler dataHandler;

    // TODO: move this to separate ModItems/ModBlocks classes
    private Item[] items;
    private Block[] blocks;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ItemPredicates.register(new ResourceLocation("tetra:modular_item"), ItemPredicateModular::new);

        LootConditionManager.registerCondition(new FortuneBonusCondition.Serializer());
        LootFunctionManager.registerFunction(new FortuneBonusFunction.Serializer());
        LootFunctionManager.registerFunction(new SetMetadataFunction.Serializer());

        dataHandler = new DataHandler(event.getSourceFile());

        CriteriaTriggers.register(BlockLookTrigger.INSTANCE);
        CriteriaTriggers.register(BlockUseCriterion.TRIGGER);
        CriteriaTriggers.register(BlockInteractionCriterion.TRIGGER);
        CriteriaTriggers.register(ModuleCraftCriterion.TRIGGER);
        CriteriaTriggers.register(ImprovementCraftCriterion.TRIGGER);

        MinecraftForge.EVENT_BUS.register(new ItemEffectHandler());
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(TetraMod.proxy);
        MinecraftForge.EVENT_BUS.register(BlockLookTrigger.INSTANCE);

        blocks = new Block[]{
                new BlockWorkbench(),
                new BlockGeode(),
                new BlockHammerHead(),
                new BlockHammerBase(),
                new BlockForgedWall(),
                new BlockForgedPillar(),
                new BlockForgedPlatform(),
                new BlockForgedPlatformSlab(),
                new BlockForgedVent(),
                new BlockForgedContainer(),
                new BlockForgedCrate(),
                new BlockTransferUnit(),
                new BlockCoreExtractorBase(),
                new BlockCoreExtractorPiston(),
                new BlockCoreExtractorPipe(),
                new BlockSeepingBedrock()
        };

        items = new Item[]{
                new ItemSwordModular(),
                new ItemToolbeltModular(),
                new ItemDuplexToolModular(),
                new ItemGeode(),
                new ItemPristineLapis(),
                new ItemPristineEmerald(),
                new ItemPristineDiamond(),
                new ItemCellMagmatic(),
                new ItemBolt(),
                new ItemBeam(),
                new ItemMesh(),
                new ItemQuickLatch(),
                new ItemMetalScrap(),
                new ItemVentPlate(),
                new ItemJournal()
        };

        ForgeRegistries.POTIONS.register(new PotionBleeding());
        ForgeRegistries.POTIONS.register(new PotionEarthbound());

        proxy.preInit(
                event,
                Arrays.stream(items)
                        .filter(item -> item instanceof ITetraItem)
                        .map(item -> (ITetraItem) item)
                        .toArray(ITetraItem[]::new),
                Arrays.stream(blocks)
                        .filter(block -> block instanceof ITetraBlock)
                        .map(block -> (ITetraBlock) block)
                        .toArray(ITetraBlock[]::new)
        );
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        if (ConfigHandler.generateFeatures) {
            WorldGenFeatures worldGenFeatures = new WorldGenFeatures();
            GameRegistry.registerWorldGenerator(worldGenFeatures, 11);
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, GuiHandlerRegistry.INSTANCE);

        PacketHandler packetHandler = new PacketHandler();

        Arrays.stream(items)
                .filter(item -> item instanceof ITetraItem)
                .map(item -> (ITetraItem) item)
                .forEach(item -> item.init(packetHandler));
        Arrays.stream(blocks)
                .filter(block -> block instanceof ITetraBlock)
                .map(block -> (ITetraBlock) block)
                .forEach(block -> block.init(packetHandler));

        packetHandler.registerPacket(HonePacket.class, Side.CLIENT);
        packetHandler.registerPacket(SettlePacket.class, Side.CLIENT);

        DestabilizationEffect.LoadEffects();
        new CleanseSchema();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new TGenCommand());
    }

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (Tags.MOD_ID.equals(event.getName().getNamespace())) {
            LootTable lootTable = event.getTable();
            LootPool[] extendedPools = dataHandler.getExtendedLootPools(event.getName());
            Optional.ofNullable(extendedPools)
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty)
                    .forEach(lootTable::addPool);
        }
    }

    @SubscribeEvent
    public void onRegisterBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(blocks);
    }

    @SubscribeEvent
    public void onRegisterItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(items);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            Arrays.stream(items)
                    .forEach(item -> {
                        ModelLoader.setCustomModelResourceLocation(item, 0,
                                new ModelResourceLocation(item.getRegistryName(), "inventory"));
                    });
        }

        Arrays.stream(blocks)
                .filter(block -> block instanceof ITetraBlock)
                .map(block -> (ITetraBlock) block)
                .filter(ITetraBlock::hasItem)
                .forEach(block -> block.registerItem(event.getRegistry()));
    }

    public static ResourceLocation getResource(String path) {
        return new ResourceLocation(Tags.MOD_ID, path);
    }
}
