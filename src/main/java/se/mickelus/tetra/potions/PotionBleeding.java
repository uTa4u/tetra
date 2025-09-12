package se.mickelus.tetra.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.registry.GameRegistry;
import se.mickelus.tetra.Tags;

public class PotionBleeding extends Potion {
    private static final String UNLOCALIZED_NAME = "bleeding";

    @GameRegistry.ObjectHolder(Tags.MOD_ID + ":" + UNLOCALIZED_NAME)
    public static PotionBleeding INSTANCE;

    public PotionBleeding() {
        super(true, 0);

        setRegistryName(UNLOCALIZED_NAME);
        setPotionName(UNLOCALIZED_NAME);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        entity.attackEntityFrom(DamageSource.GENERIC, amplifier);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return false;
    }

    @Override
    public boolean shouldRenderHUD(PotionEffect effect) {
        return false;
    }
}
