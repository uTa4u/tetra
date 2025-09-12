package se.mickelus.tetra.gui;

public enum GuiAlignment {
    LEFT,
    CENTER,
    RIGHT;

    public GuiAlignment flip() {
        if (this == LEFT) {
            return RIGHT;
        } else if (this == RIGHT) {
            return LEFT;
        }
        return CENTER;
    }

    public GuiAttachment toAttachment() {
        if (this == LEFT) {
            return GuiAttachment.TOP_LEFT;
        } else if (this == RIGHT) {
            return GuiAttachment.TOP_RIGHT;
        }
        return GuiAttachment.TOP_CENTER;
    }
}
