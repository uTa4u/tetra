package se.mickelus.tetra.items.toolbelt;

import se.mickelus.tetra.module.ItemEffect;

public enum SlotType {
    STORAGE(ItemEffect.STORAGE_SLOT),
    QUIVER(ItemEffect.QUIVER_SLOT),
    POTION(ItemEffect.POTION_SLOT),
    QUICK(ItemEffect.QUICK_SLOT);

    ItemEffect effect;

    SlotType(ItemEffect effect) {
        this.effect = effect;
    }
}
