package se.mickelus.tetra.module;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import se.mickelus.tetra.Tags;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.module.data.ImprovementData;
import se.mickelus.tetra.module.data.ModuleData;

import java.util.Arrays;

public class MultiSlotModule<T extends ModuleData> extends ItemModuleMajor<T> {

    protected String slotSuffix;

    protected String UNLOCALIZED_NAME;

    public MultiSlotModule(String slotKey, String moduleKey, String slotSuffix, String... improvementKeys) {
        super(slotKey, moduleKey + slotSuffix);

        this.slotSuffix = slotSuffix;

        this.UNLOCALIZED_NAME = moduleKey;

        this.dataKey = moduleKey + slotSuffix + "_material";

        if (improvementKeys.length > 0) {
            improvements = Arrays.stream(improvementKeys)
                    .map(key -> TetraMod.dataHandler.getModuleData(key, ImprovementData[].class))
                    .flatMap(Arrays::stream)
                    .toArray(ImprovementData[]::new);
        }

        settleMax = Arrays.stream(improvements)
                .filter(data -> data.key.equals(settleImprovement))
                .mapToInt(ImprovementData::getLevel)
                .max()
                .orElse(0);
    }

    @Override
    public String getUnlocalizedName() {
        return UNLOCALIZED_NAME;
    }

    public ResourceLocation[] getAllTextures() {
        return Arrays.stream(data)
                .map(moduleData -> moduleData.key)
                .map(key -> "items/" + key + slotSuffix)
                .map(key -> new ResourceLocation(Tags.MOD_ID, key))
                .toArray(ResourceLocation[]::new);
    }

    public ResourceLocation[] getTextures(ItemStack itemStack) {
        String string = "items/" + getData(itemStack).key + slotSuffix;
        return new ResourceLocation[]{new ResourceLocation(Tags.MOD_ID, string)};
    }
}
