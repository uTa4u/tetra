package se.mickelus.tetra.blocks.hammer;

public enum HammerEffect {
    EFFICIENT(false),
    OVERCHARGED(true),
    LEAKY(false),
    DAMAGING(false);

    public final boolean requiresBoth;

    HammerEffect(boolean requiresBoth) {
        this.requiresBoth = requiresBoth;
    }

    public static HammerEffect fromConfig(HammerConfig config, long seed) {
        return HammerEffect.values()[config.ordinal() % HammerEffect.values().length];
    }
}
