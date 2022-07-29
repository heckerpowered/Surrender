package heckerpowered.surrender.common.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import heckerpowered.surrender.common.content.SurrenderEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    public final LivingEntity self = (LivingEntity) (Object) this;

    @Inject(method = "getAttributeValue", at = @At("TAIL"), cancellable = true)
    public void getAttributeValue(Attribute attribute, CallbackInfoReturnable<Double> info) {
        if (attribute == Attributes.ARMOR_TOUGHNESS || attribute == Attributes.ARMOR) {
            info.setReturnValue(info.getReturnValue()
                    * (1.0 + EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.GUARDIAN.get(), self) * 0.2));
        }
    }
}
