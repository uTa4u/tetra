package se.mickelus.tetra.blocks.hammer;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

// TODO: wtf is this naming, probably should not exist because HammerEffect exists...
public enum HammerConfig implements IStringSerializable {
    A,
    B,
    C,
    D;

    public static final PropertyEnum<HammerConfig> propE = PropertyEnum.create("confige", HammerConfig.class);
    public static final PropertyEnum<HammerConfig> propW = PropertyEnum.create("configw", HammerConfig.class);

    @Override
    public String getName() {
        return toString().toLowerCase();
    }

    public static HammerConfig getNextConfiguration(HammerConfig config) {
        return values()[(config.ordinal() + 1) % values().length];
    }
}
