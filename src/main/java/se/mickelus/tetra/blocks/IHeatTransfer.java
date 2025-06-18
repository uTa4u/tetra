package se.mickelus.tetra.blocks;

public interface IHeatTransfer {
    boolean canRecieve();

    boolean canSend();

    void setReceiving(boolean receiving);

    boolean isReceiving();

    void setSending(boolean sending);

    boolean isSending();

    int getReceiveLimit();

    int getSendLimit();

    int drain(int amount);

    int fill(int amount);

    int getCharge();

    float getEfficiency();

    void updateTransferState();
}
