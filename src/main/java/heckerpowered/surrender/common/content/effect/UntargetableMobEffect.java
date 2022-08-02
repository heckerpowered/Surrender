package heckerpowered.surrender.common.content.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Mobs will not able to select entities with this effect as their targets.
 */
public final class UntargetableMobEffect extends MobEffect {

    public UntargetableMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFA9A9A9);
    }

}
