package se.mickelus.tetra.gui;

public enum GuiAttachment {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_CENTER,
    MIDDLE_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    public GuiAttachment flipHorizontal() {
        switch (this) {
            case TOP_LEFT:
                return TOP_RIGHT;
            case TOP_RIGHT:
                return TOP_LEFT;
            case MIDDLE_LEFT:
                return MIDDLE_RIGHT;
            case MIDDLE_RIGHT:
                return MIDDLE_LEFT;
            case BOTTOM_LEFT:
                return BOTTOM_RIGHT;
            case BOTTOM_RIGHT:
                return BOTTOM_LEFT;
            default:
                return this;
        }
    }
}
