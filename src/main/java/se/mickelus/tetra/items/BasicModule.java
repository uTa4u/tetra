package se.mickelus.tetra.items;

import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.module.ItemModule;
import se.mickelus.tetra.module.ItemUpgradeRegistry;
import se.mickelus.tetra.module.data.ModuleData;
import se.mickelus.tetra.module.data.TweakData;

public class BasicModule extends ItemModule<ModuleData> {
    public BasicModule(String slotKey, String moduleKey) {
        super(slotKey, moduleKey);

        data = TetraMod.dataHandler.getModuleData(moduleKey, ModuleData[].class);
        ItemUpgradeRegistry.INSTANCE.registerModule(moduleKey, this);
    }

    public BasicModule(String slotKey, String moduleKey, String tweakKey) {
        this(slotKey, moduleKey);

        tweaks = TetraMod.dataHandler.getModuleData(tweakKey, TweakData[].class);
    }
}
