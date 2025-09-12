package se.mickelus.tetra.blocks.hammer;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.util.EnumFacing;

public enum HammerPlate {
    EAST(EnumFacing.EAST, "platee"),
    WEST(EnumFacing.WEST, "platew");

    public final EnumFacing face;
    public final PropertyBool prop;
    public final String key;

    HammerPlate(EnumFacing face, String key) {
        this.face = face;
        this.key = key;
        this.prop = PropertyBool.create(key);
    }
}
