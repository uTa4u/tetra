package se.mickelus.tetra.gui.impl.statbar.getter;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import se.mickelus.tetra.module.ItemEffect;

public class TooltipGetterCriticalStrike implements ITooltipGetter {
    private static final IStatGetter EFFICIENCY_GETTER = new StatGetterEffectEfficiency(ItemEffect.CRITICAL_STRIKE, 100);
    private static final IStatGetter LEVEL_GETTER = new StatGetterEffectLevel(ItemEffect.CRITICAL_STRIKE, 1);

    public TooltipGetterCriticalStrike() {
    }

    @Override
    public String getTooltip(EntityPlayer player, ItemStack itemStack) {
        return I18n.format("stats.criticalStrike.tooltip",
                String.format("%.0f%%", LEVEL_GETTER.getValue(player, itemStack)),
                String.format("%.0f%%", EFFICIENCY_GETTER.getValue(player, itemStack)),
                String.format("%.0f%%", LEVEL_GETTER.getValue(player, itemStack)));
    }
}
