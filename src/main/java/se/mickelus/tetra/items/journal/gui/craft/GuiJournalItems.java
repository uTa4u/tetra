package se.mickelus.tetra.items.journal.gui.craft;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import se.mickelus.tetra.gui.GuiAttachment;
import se.mickelus.tetra.gui.GuiElement;
import se.mickelus.tetra.gui.animation.Applier;
import se.mickelus.tetra.gui.animation.KeyframeAnimation;
import se.mickelus.tetra.items.ItemModular;
import se.mickelus.tetra.items.duplex_tool.ItemDuplexToolModular;
import se.mickelus.tetra.items.sword.ItemSwordModular;
import se.mickelus.tetra.items.toolbelt.ItemToolbeltModular;

import java.util.function.Consumer;

public class GuiJournalItems extends GuiElement {

    private final GuiJournalItem sword;
    private final GuiJournalItem toolbelt;
    private final GuiJournalItem tools;

    public GuiJournalItems(int x, int y, int width, int height, Consumer<ItemModular> onItemSelect, Consumer<String> onSlotSelect) {
        super(x, y, width, height);


        sword = new GuiJournalItem(-39, 0, ItemSwordModular.INSTANCE, I18n.format("journal.craft.sword"),
                () -> onItemSelect.accept(ItemSwordModular.INSTANCE), onSlotSelect);
        sword.setAttachment(GuiAttachment.TOP_CENTER);
        addChild(sword);

        toolbelt = new GuiJournalItem(1, -40, ItemToolbeltModular.INSTANCE, I18n.format("journal.craft.toolbelt"),
                () -> onItemSelect.accept(ItemToolbeltModular.INSTANCE), onSlotSelect);
        toolbelt.setAttachment(GuiAttachment.TOP_CENTER);
        addChild(toolbelt);

        tools = new GuiJournalItem(41, 0, ItemDuplexToolModular.INSTANCE, I18n.format("journal.craft.tool"),
                () -> onItemSelect.accept(ItemDuplexToolModular.INSTANCE), onSlotSelect);
        tools.setAttachment(GuiAttachment.TOP_CENTER);
        addChild(tools);
    }

    public void animateOpen() {
        new KeyframeAnimation(200, this)
                .applyTo(new Applier.TranslateY(y - 4, y), new Applier.Opacity(0, 1))
                .withDelay(800)
                .start();
    }

    public void animateBack() {
        new KeyframeAnimation(100, this)
                .applyTo(new Applier.TranslateY(y - 4, y), new Applier.Opacity(0, 1))
                .start();
    }

    public void changeItem(Item item) {
        if (item instanceof ItemSwordModular) {
            toolbelt.setVisible(false);
            tools.setVisible(false);

            sword.setVisible(true);
            sword.setSelected(true);
        } else if (item instanceof ItemToolbeltModular) {
            sword.setVisible(false);
            tools.setVisible(false);

            toolbelt.setVisible(true);
            toolbelt.setSelected(true);
        } else if (item instanceof ItemDuplexToolModular) {
            sword.setVisible(false);
            toolbelt.setVisible(false);

            tools.setVisible(true);
            tools.setSelected(true);
        } else {
            sword.setSelected(false);
            toolbelt.setSelected(false);
            tools.setSelected(false);

            sword.setVisible(true);
            toolbelt.setVisible(true);
            tools.setVisible(true);
        }
    }
}
