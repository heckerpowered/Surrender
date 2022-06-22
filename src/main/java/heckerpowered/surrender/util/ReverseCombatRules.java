package heckerpowered.surrender.util;

public final class ReverseCombatRules {
    private ReverseCombatRules() {
    }

    public static float getDamageAfterAbsorb(float damage, float armor, float toughness) {
        return damage * ((25 - armor) / 25) + damage * damage * (50 + (25 * toughness) / 4);
    }
}
