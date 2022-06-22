package heckerpowered.surrender.event;

import heckerpowered.surrender.content.SurrenderEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
        var item = event.getItemStack();
        var tag = item.getTag();

        var blinkLevel = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.BLINK.get(), item);
        if (blinkLevel > 0) {
            var player = event.getPlayer();
            int blink_cooldown = 20 * (6 - blinkLevel);

            var last_active_time = tag.getInt("surrender_blink_last_active_time");
            if (last_active_time == 0 || last_active_time > player.tickCount
                    || last_active_time + blink_cooldown < player.tickCount) {
                var forward = player.getForward();

                var offsetX = forward.x * 3;
                var offsetZ = forward.z * 3;

                player.setDeltaMovement(offsetX, player.getDeltaMovement().y, offsetZ);
                player.invulnerableTime = 20;
                player.swing(event.getHand());

                for (var victim : player.level.getEntities(player,
                        player.getBoundingBox().move(offsetX, 0, offsetZ).inflate(3))) {
                    float damageBouns;

                    if (victim instanceof Mob mob) {
                        damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), mob.getMobType());
                    } else {
                        damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEFINED);
                    }

                    damageBouns += (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    victim.invulnerableTime = 10;
                    victim.hurt(DamageSource.playerAttack(player), damageBouns * 0.4F * blinkLevel);
                }

                tag.putInt("surrender_blink_last_active_time", player.tickCount);
            }
        }

        if (tag != null && tag.getBoolean("surrender_seeker_available")) {
            var x = tag.getDouble("surrender_seeker_x");
            var y = tag.getDouble("surrender_seeker_y");
            var z = tag.getDouble("surrender_seeker_z");
            var entity = event.getEntityLiving();
            if (entity.distanceToSqr(x, y, z) > 400.0D) {
                return;
            }

            var motion = entity.getDeltaMovement();
            entity.teleportTo(x, y, z);
            entity.setDeltaMovement(motion);
            entity.swing(event.getHand());
            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
            for (var victim : entity.level.getEntities(entity, entity.getBoundingBox().inflate(5))) {
                if (victim instanceof LivingEntity livingVictim) {
                    victim.invulnerableTime = 10;
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
        var entity = event.getEntityLiving();
        var sourceEntity = event.getSource().getEntity();

        if (sourceEntity != null && sourceEntity instanceof LivingEntity source) {
            var decisiveStrikeLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.DECISIVE_STRIKE.get(),
                    source);
            if (decisiveStrikeLevel > 0 && entity.getHealth() < entity.getMaxHealth() * 0.4F) {
                event.setAmount(event.getAmount() * (1.0F + decisiveStrikeLevel * 0.05F));
            }

            var lastStandLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.LAST_STAND.get(), source);
            if (lastStandLevel > 0 && source.getHealth() < source.getMaxHealth() * 0.4F) {
                event.setAmount(event.getAmount() * (1.0F + lastStandLevel * 0.05F));
            }

            var ripperLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.RIPPER.get(), source);
            if (ripperLevel > 0) {
                event.setAmount(event.getAmount() * (1.0F + ripperLevel * 0.2F));
            }
        }

        final int guardian_cooldown = 20 * 30;

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

        if (sourceEntity != null && sourceEntity instanceof LivingEntity source) {
            var regeneratorLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.REGENERATOR.get(),
                    source);
            if (regeneratorLevel > 0) {
                var healAmount = regeneratorLevel * 0.02F * (source.getMaxHealth() - source.getHealth());
                if (source.getHealth() <= source.getMaxHealth() * 0.25) {
                    healAmount *= 3;
                } else if (source.getHealth() <= source.getMaxHealth() * 0.5) {
                    healAmount *= 2;
                }

                source.heal(healAmount);
            }

            var shieldRadierLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.SHIELD_RAIDER.get(),
                    source);
            if (shieldRadierLevel > 0) {
                var reductionAmount = 0.1F * shieldRadierLevel;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() * reductionAmount);
            }

            if (source instanceof Player player) {
                var predatorLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.PREDATOR.get(), source);
                if (predatorLevel > 0) {
                    var foodData = player.getFoodData();
                    foodData.setSaturation(foodData.getSaturationLevel() + predatorLevel);
                    foodData.setFoodLevel(foodData.getFoodLevel() + predatorLevel);
                }
            }
        }
    }
}
