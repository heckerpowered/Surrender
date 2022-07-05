package heckerpowered.surrender.common.content.enchantment;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class GuardianEnchantment extends Enchantment {

    public GuardianEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR,
                new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                        EquipmentSlot.LEGS, EquipmentSlot.FEET });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getDamageProtection(int p_44680_, DamageSource p_44681_) {
        return p_44680_ * 2;
    }
}
