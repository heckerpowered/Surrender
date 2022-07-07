package heckerpowered.surrender.common.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BlisteringEnchantment extends Enchantment {

    public BlisteringEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON,
                new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND });
    }

    @Override
    public int getMaxLevel() {
        return 7;
    }

}