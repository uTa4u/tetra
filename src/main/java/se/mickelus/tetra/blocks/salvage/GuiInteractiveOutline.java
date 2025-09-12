package se.mickelus.tetra.blocks.salvage;

import net.minecraft.entity.player.EntityPlayer;
import se.mickelus.tetra.gui.GuiAttachment;
import se.mickelus.tetra.gui.GuiElement;
import se.mickelus.tetra.gui.GuiTexture;
import se.mickelus.tetra.gui.animation.Applier;
import se.mickelus.tetra.gui.animation.KeyframeAnimation;

public class GuiInteractiveOutline extends GuiElement {
    private static final String TEXTURE = "textures/gui/block-interaction.png";

    private final BlockInteraction blockInteraction;

    private final GuiTexture topLeft;
    private final GuiTexture topRight;
    private final GuiTexture bottomLeft;
    private final GuiTexture bottomRight;

    private GuiInteractiveCapability capability;

    public GuiInteractiveOutline(BlockInteraction blockInteraction, EntityPlayer player) {
        super((int) blockInteraction.minX * 2, (int) blockInteraction.minY * 2,
                (int) (blockInteraction.maxX - blockInteraction.minX) * 2,
                (int) (blockInteraction.maxY - blockInteraction.minY) * 2);

        this.blockInteraction = blockInteraction;

        opacity = 0.3f;

        topLeft = new GuiTexture(-2, -2, 4, 4, 0, 0, TEXTURE);
        addChild(topLeft);
        new KeyframeAnimation(100, topLeft)
                .applyTo(new Applier.Opacity(0, 1),
                        new Applier.TranslateX(0, -2),
                        new Applier.TranslateY(0, -2))
                .withDelay(500)
                .start();

        topRight = new GuiTexture(2, -2, 4, 4, 3, 0, TEXTURE);
        topRight.setAttachment(GuiAttachment.TOP_RIGHT);
        addChild(topRight);
        new KeyframeAnimation(100, topRight)
                .applyTo(new Applier.Opacity(0, 1),
                        new Applier.TranslateX(0, 2),
                        new Applier.TranslateY(0, -2))
                .withDelay(650)
                .start();

        bottomLeft = new GuiTexture(-2, 2, 4, 4, 3, 0, TEXTURE);
        bottomLeft.setAttachment(GuiAttachment.BOTTOM_LEFT);
        addChild(bottomLeft);
        new KeyframeAnimation(100, bottomLeft)
                .applyTo(new Applier.Opacity(0, 1),
                        new Applier.TranslateX(0, -2),
                        new Applier.TranslateY(0, 2))
                .withDelay(500)
                .start();

        bottomRight = new GuiTexture(2, 2, 4, 4, 0, 0, TEXTURE);
        bottomRight.setAttachment(GuiAttachment.BOTTOM_RIGHT);
        addChild(bottomRight);
        new KeyframeAnimation(100, bottomRight)
                .applyTo(new Applier.Opacity(0, 1),
                        new Applier.TranslateX(0, 2),
                        new Applier.TranslateY(0, 2))
                .withDelay(650)
                .onStop(complete -> {
                    if (capability != null) capability.updateFadeTime();
                })
                .start();

        if (blockInteraction.requiredCapability != null) {
            capability = new GuiInteractiveCapability(0, 0,
                    blockInteraction.requiredCapability, blockInteraction.requiredLevel, player);
            addChild(capability);

            float centerY = y + height / 2f;
            float centerX = x + width / 2f;

            if (Math.abs(centerX - 16) > Math.abs(centerY - 16)) {
                if (centerX < 16) {
                    capability.setAttachmentPoint(GuiAttachment.MIDDLE_LEFT);
                    capability.setAttachmentAnchor(GuiAttachment.MIDDLE_RIGHT);
                } else {
                    capability.setAttachmentPoint(GuiAttachment.MIDDLE_RIGHT);
                    capability.setAttachmentAnchor(GuiAttachment.MIDDLE_LEFT);
                    capability.setX(1);
                }
            } else {
                if (centerY < 16) {
                    capability.setAttachmentPoint(GuiAttachment.TOP_CENTER);
                    capability.setAttachmentAnchor(GuiAttachment.BOTTOM_CENTER);
                    capability.setY(1);
                } else {
                    capability.setAttachmentPoint(GuiAttachment.BOTTOM_CENTER);
                    capability.setAttachmentAnchor(GuiAttachment.TOP_CENTER);
                    capability.setY(-2);
                }
            }
        }
    }

    @Override
    protected void onFocus() {
        super.onFocus();

        if (capability != null) {
            capability.show();
        }
    }

    @Override
    protected void onBlur() {
        super.onBlur();

        if (capability != null) {
            capability.hide();
        }
    }

    public BlockInteraction getBlockInteraction() {
        return blockInteraction;
    }

    public void transitionOut(Runnable onStop) {
        new KeyframeAnimation(200, topLeft)
                .applyTo(new Applier.Opacity(1, 0),
                        new Applier.TranslateX(-5),
                        new Applier.TranslateY(-5))
                .start();

        new KeyframeAnimation(200, topRight)
                .applyTo(new Applier.Opacity(1, 0),
                        new Applier.TranslateX(5),
                        new Applier.TranslateY(-5))
                .start();

        new KeyframeAnimation(200, bottomLeft)
                .applyTo(new Applier.Opacity(1, 0),
                        new Applier.TranslateX(-5),
                        new Applier.TranslateY(5))
                .start();

        new KeyframeAnimation(200, bottomRight)
                .applyTo(new Applier.Opacity(1, 0),
                        new Applier.TranslateX(5),
                        new Applier.TranslateY(5))
                .onStop(finished -> onStop.run())
                .start();
    }
}
