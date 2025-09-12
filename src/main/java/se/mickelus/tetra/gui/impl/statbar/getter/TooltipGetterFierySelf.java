package se.mickelus.tetra.gui.impl.statbar.getter;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.module.ItemEffect;

public class TooltipGetterFierySelf implements ITooltipGetter {
    private static final IStatGetter EFFICIENCY_GETTER = new StatGetterEffectEfficiency(ItemEffect.FIERY_SELF, 100);
    private static final IStatGetter LEVEL_GETTER = new StatGetterEffectLevel(ItemEffect.FIERY_SELF, 1);

    public TooltipGetterFierySelf() {
    }


    @Override
    public String getTooltip(EntityPlayer player, ItemStack itemStack) {
        return I18n.format("stats.fierySelf.tooltip",
                String.format("%.2f%%", EFFICIENCY_GETTER.getValue(player, itemStack)),
                String.format("%.2f", LEVEL_GETTER.getValue(player, itemStack)));
    }
}
