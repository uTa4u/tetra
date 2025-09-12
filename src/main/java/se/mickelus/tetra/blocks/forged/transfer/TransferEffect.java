package se.mickelus.tetra.blocks.forged.transfer;

// TODO: wtf is this, TransferConfig should not exist...
public enum TransferEffect {
    SEND,
    RECEIVE,
    REDSTONE;

    private static final TransferEffect[] VALUES = values();
    private static final int LENGTH = VALUES.length;

    public static TransferEffect fromConfig(TransferConfig config, long seed) {
        return VALUES[config.ordinal() % LENGTH];
    }
}
