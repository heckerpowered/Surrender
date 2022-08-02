package heckerpowered.surrender.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import heckerpowered.surrender.common.content.effect.SurrenderMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "getTarget", at = @At("TAIL"), cancellable = true)
    public void getTarget(CallbackInfoReturnable<LivingEntity> info) {
        final var target = info.getReturnValue();
        if (target != null) {
            if (target.hasEffect(SurrenderMobEffects.UNTARGETABLE.get())) {
                info.setReturnValue(null);
            }
        }
    }
}
