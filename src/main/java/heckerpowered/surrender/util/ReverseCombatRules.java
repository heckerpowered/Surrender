package heckerpowered.surrender.util;

import net.minecraft.util.Mth;

public final class ReverseCombatRules {
    private ReverseCombatRules() {
    }

    public static float getDamageAfterAbsorb(float p_19273_, float p_19274_, float p_19275_) {
        float f = 2.0F + p_19275_ / 4.0F;
        float f1 = Mth.clamp(p_19274_ - p_19273_ / f, p_19274_ * 0.2F, 20.0F);
        return p_19273_ * (1.0F - f1 / 25.0F);
    }

    public static float getDamageBeforeAbsorb(float damage, float armor, float toughness) {
        return damage * (1.0F + toughness / 25.0F) * (1.0F + armor / 25.0F);
    }
}
