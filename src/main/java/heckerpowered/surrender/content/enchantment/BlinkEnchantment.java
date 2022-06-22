package heckerpowered.surrender.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class BlinkEnchantment extends Enchantment {

    public BlinkEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
