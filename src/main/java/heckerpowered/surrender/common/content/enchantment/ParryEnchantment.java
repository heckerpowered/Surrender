package heckerpowered.surrender.common.content.enchantment;

import javax.annotation.Nonnull;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Represents a enchantment that can only be applied on shields, when you hold up
 * a shield with this enchantment and sneak, you are immune to damage.
 * When you are successfully immune to any damage, you will
 * gain at least 0.5s (depends on lvl) of damage immunity.
 *
 * @author Heckerpowered
 */
@Mod.EventBusSubscriber
public final class ParryEnchantment extends Enchantment {

    public ParryEnchantment() {
        super(Rarity.COMMON, EnchantmentCategory.WEAPON,
                new EquipmentSlot[] { EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND });
    }

    @Override
    public final int getMaxLevel() {
        return 5;
    }

    @Override
    public final boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ShieldItem;
    }

    /**
     * Called when the player is attacked, check if {@link LivingAttackEvent#getEntity } is a player
     * that is holding a shield with this enchantment, and if so, cancel this attack to achieve
     * immunity to damage.
     *
     * @param event The event that is being handled.
     */
    @SubscribeEvent
    public static final void onLivingAttack(@Nonnull final LivingAttackEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.isShiftKeyDown()) {
            final var useItem = player.getUseItem();
            final var level = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.PARRY.get(), useItem);
            if (level > 0) {
                //
                // Cancel event to avoid damage.
                //
                event.setCanceled(true);

                if (player.invulnerableTime >= 10) {
                    return;
                }

                final var hand = player.getUsedItemHand();
                useItem.hurtAndBreak(1, player, (v) -> {
                    v.broadcastBreakEvent(hand);
                    ForgeEventFactory.onPlayerDestroyItem(player, useItem, hand);
                });

                //
                // Send packet to player to play sound
                //
                player.level.broadcastEntityEvent(player, (byte) 29);

                //
                // gain at least 0.5s (depends on lvl) of damage immunity.
                //
                player.invulnerableTime = Mth.clamp(player.invulnerableTime, 20, level * 100);
            }
        }
    }
}
