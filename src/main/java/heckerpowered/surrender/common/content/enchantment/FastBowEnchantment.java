package heckerpowered.surrender.common.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public final class FastBowEnchantment extends Enchantment {

    public FastBowEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.VANISHABLE,
                new EquipmentSlot[] { EquipmentSlot.MAINHAND,
                        EquipmentSlot.OFFHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean canEnchant(ItemStack p_44689_) {
        return p_44689_.getUseDuration() > 0 && !p_44689_.isEdible() && !(p_44689_.getItem() instanceof ShieldItem);
    }
}
