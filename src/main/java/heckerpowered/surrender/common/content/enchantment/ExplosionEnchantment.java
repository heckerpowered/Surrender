package heckerpowered.surrender.common.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class ExplosionEnchantment extends Enchantment {

    public ExplosionEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BOW, new EquipmentSlot[] { EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }
}
