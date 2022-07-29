package heckerpowered.surrender.common.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import heckerpowered.surrender.common.content.SurrenderEnchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    public final ItemStack self = (ItemStack) (Object) this;

    @Inject(method = "onUseTick", at = @At("HEAD"), cancellable = true)
    public void onUseTick(Level level, LivingEntity livingEntity, int count, CallbackInfo info) {
        final var fastBowLevel = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.FAST_BOW.get(), self);
        if (fastBowLevel > 0 && (count % (6 - fastBowLevel)) == 0) {
            self.getItem().releaseUsing(self, level, livingEntity, 0);
        }
    }
}
