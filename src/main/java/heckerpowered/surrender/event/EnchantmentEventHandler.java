package heckerpowered.surrender.event;

import heckerpowered.surrender.content.SurrenderEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class EnchantmentEventHandler {
    private EnchantmentEventHandler() {
    }

    @SubscribeEvent
    public static final void onLivingDead(final LivingDeathEvent event) {
        var source = event.getSource().getEntity();
        if (source != null && source instanceof Player player) {
            var mainHandItem = player.getMainHandItem();
            var offHandItem = player.getOffhandItem();
            var entity = event.getEntity();

            //
            // If both main hand item and off hand item have enchantment "Seeker",
            // "Seeker" enchantment only works on the main hand.
            //
            if (EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.SEEKER.get(), mainHandItem) > 0) {
                var tag = mainHandItem.getOrCreateTag();
                tag.putDouble("surrender_seeker_x", entity.getX());
                tag.putDouble("surrender_seeker_y", entity.getY());
                tag.putDouble("surrender_seeker_z", entity.getZ());
                tag.putBoolean("surrender_seeker_available", true);
            } else if (EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.SEEKER.get(), offHandItem) > 0) {
                var tag = offHandItem.getOrCreateTag();
                tag.putDouble("surrender_seeker_x", entity.getX());
                tag.putDouble("surrender_seeker_y", entity.getY());
                tag.putDouble("surrender_seeker_z", entity.getZ());
                tag.putBoolean("surrender_seeker_available", true);
            }
        }
    }

    @SubscribeEvent
    public static final void onRightClickItem(final RightClickItem event) {
        var tag = event.getItemStack().getTag();
        if (tag != null && tag.getBoolean("surrender_seeker_available")) {
            var x = tag.getDouble("surrender_seeker_x");
            var y = tag.getDouble("surrender_seeker_y");
            var z = tag.getDouble("surrender_seeker_z");
            var entity = event.getEntityLiving();
            if (entity.distanceToSqr(x, y, z) > 400.0D) {
                return;
            }

            entity.teleportTo(x, y, z);
            entity.swing(event.getHand());
            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
            for (var victim : entity.level.getEntities(entity, entity.getBoundingBox().inflate(3))) {
                if (victim instanceof LivingEntity livingVictim) {
                    livingVictim.hurt(DamageSource.mobAttack(entity), 2.0F + livingVictim.getMaxHealth() * 0.12F);
                    livingVictim.setDeltaMovement((livingVictim.getX() - x) * 0.5, livingVictim.getDeltaMovement().y,
                            (livingVictim.getZ() - z) * 0.5);
                }
            }

            tag.remove("surrender_seeker_x");
            tag.remove("surrender_seeker_y");
            tag.remove("surrender_seeker_z");
            tag.remove("surrender_seeker_available");
        }
    }
}
