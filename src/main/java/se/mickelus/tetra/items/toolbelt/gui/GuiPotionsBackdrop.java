package se.mickelus.tetra.items.toolbelt.gui;

import net.minecraft.util.ResourceLocation;
import se.mickelus.tetra.TetraMod;
import se.mickelus.tetra.gui.GuiAttachment;
import se.mickelus.tetra.gui.GuiElement;
import se.mickelus.tetra.gui.GuiRect;
import se.mickelus.tetra.gui.GuiTexture;
import se.mickelus.tetra.gui.impl.GuiColors;
import se.mickelus.tetra.items.toolbelt.SlotType;
import se.mickelus.tetra.module.ItemEffect;

import java.util.Collection;

public class GuiPotionsBackdrop extends GuiElement {
    private static final ResourceLocation texture = TetraMod.getResource("textures/gui/toolbelt-inventory.png");

    public GuiPotionsBackdrop(int x, int y, int numSlots, Collection<Collection<ItemEffect>> inventoryEffects) {
        super(x, y, numSlots * 17 - 9, 28);

        setAttachmentPoint(GuiAttachment.TOP_CENTER);
        setAttachmentAnchor(GuiAttachment.TOP_CENTER);

        // background rects
        addChild(new GuiRect(0, 3, width, 22, 0xff000000));
        addChild(new GuiRect(0, 4, width, 1, GuiColors.muted));
        addChild(new GuiRect(0, 23, width, 1, GuiColors.normal));

        // left cap
        GuiTexture leftCap = new GuiTexture(0, 0, 16, 28, 64, 0, texture);
        leftCap.setAttachmentPoint(GuiAttachment.TOP_RIGHT);
        addChild(leftCap);

        GuiTexture rightCap = new GuiTexture(0, 0, 16, 28, 80, 0, texture);
        rightCap.setAttachmentPoint(GuiAttachment.TOP_LEFT);
        rightCap.setAttachmentAnchor(GuiAttachment.TOP_RIGHT);
        addChild(rightCap);

        GuiSlotEffect.getEffectsForInventory(SlotType.POTION, inventoryEffects).forEach(this::addChild);
    }
}
