package se.mickelus.tetra.blocks.forged.transfer;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

// TODO: wtf is this naming
public enum TransferConfig implements IStringSerializable {
    A,
    B,
    C;

    private static final TransferConfig[] VALUES = values();
    private static final int LENGTH = VALUES.length;

    public static final PropertyEnum<TransferConfig> PROP = PropertyEnum.create("config", TransferConfig.class);

    @Override
    public String getName() {
        return toString().toLowerCase();
    }

    public static TransferConfig getNextConfiguration(TransferConfig config) {
        return VALUES[(config.ordinal() + 1) % LENGTH];
    }
}
