package se.mickelus.tetra.module.schema;

/**
 * The rarity of a schema affects how it is rendered. Colors and effects are used to differentiate between effects.
 */
public enum SchemaRarity {
    TEMPORARY(0xffdfaa),
    HONE(0xceceff),
    BASIC(0xffffff);

    public int tint;

    SchemaRarity(int tint) {
        this.tint = tint;
    }
}
