package heckerpowered.surrender.event;

import heckerpowered.surrender.content.SurrenderEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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

    @SubscribeEvent
    public static final void onLivingHurt(final LivingHurtEvent event) {
        final int guardian_cooldown = 20 * 30;

        var entity = event.getEntityLiving();
        if (entity.getHealth() - event.getAmount() <= entity.getMaxHealth() * 0.3) {
            for (var armor : event.getEntityLiving().getArmorSlots()) {
                var level = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.GUARDIAN.get(), armor);
                var absoprtionAmount = 10 * level;
                var tag = armor.getOrCreateTag();

                var last_active_time = tag.getInt("surrender_guardian_last_active_time");
                if (last_active_time == 0 || last_active_time > entity.tickCount
                        || last_active_time + guardian_cooldown < entity.tickCount) {
                    if (level > 0) {
                        entity.setAbsorptionAmount(entity.getAbsorptionAmount() + absoprtionAmount);
                        tag.putInt("surrender_guardian_last_active_time", entity.tickCount);
                    }
                }
            }
        }

        var sourceEntity = event.getSource().getEntity();
        if (sourceEntity != null && sourceEntity instanceof LivingEntity source) {
            var regeneratorLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.REGENERATOR.get(),
                    source);
            if (regeneratorLevel > 0) {
                var healAmount = +regeneratorLevel * 0.02F * (source.getMaxHealth() - source.getHealth());
                if (source.getHealth() <= source.getMaxHealth() * 0.25) {
                    healAmount *= 3;
                } else if (source.getHealth() <= source.getMaxHealth() * 0.5) {
                    healAmount *= 2;
                }

                source.heal(healAmount);
            }
        }
    }
}
