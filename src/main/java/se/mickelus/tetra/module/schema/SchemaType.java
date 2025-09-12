package se.mickelus.tetra.module.schema;

/**
 * Defines how a schema should look, primarily affects the outline around the schema glyph.
 */
public enum SchemaType {
    /**
     * Renders no outline.
     */
    OTHER,

    /**
     * Renders in the same way as major, but with a + int he bottom left. Use for improvement schemas.
     */
    IMPROVEMENT,

    /**
     * Renders the same outline as shown around minor modules, use for minor module schemas.
     */
    MINOR,

    /**
     * Renders a similar outline as major modules, but cut off at the top and the bottom. Use for major module schemas.
     */
    MAJOR
}
