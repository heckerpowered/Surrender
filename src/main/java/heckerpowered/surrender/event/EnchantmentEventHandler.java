package heckerpowered.surrender.event;

import heckerpowered.surrender.content.SurrenderEnchantments;
import heckerpowered.surrender.util.scheduled.ScheduledTickTask;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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
        var player = event.getPlayer();
        var data = player.getPersistentData();

        boolean seek_activated = false;

        //
        // Determine whether "Seeker" enchantment is available.
        //
        if (tag != null && tag.getBoolean("surrender_seeker_available")) {
            var x = tag.getDouble("surrender_seeker_x");
            var y = tag.getDouble("surrender_seeker_y");
            var z = tag.getDouble("surrender_seeker_z");
            var entity = event.getEntityLiving();

            //
            // "Seeker" enchantment can only teleport while player is inside 20m of the target.
            //
            if (entity.distanceToSqr(x, y, z) > 400.0D) {
                return;
            }

            //
            // Teleport will set player's motion to zero, so we need to set it back to the original value after teleporting.
            //
            var motion = entity.getDeltaMovement();

            //
            // Teleport player.
            //
            entity.teleportTo(x, y, z);

            //
            // Set player's motion back to the original value.
            //
            entity.setDeltaMovement(motion);

            //
            // Visual effects
            //
            entity.swing(event.getHand());

            //
            // Play sound.
            //
            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);

            //
            // Hurt entities inside 20m of the player.
            //
            for (var victim : entity.level.getEntities(entity, entity.getBoundingBox().inflate(5))) {
                if (victim instanceof LivingEntity livingVictim) {
                    livingVictim.hurt(DamageSource.mobAttack(entity), 2.0F + livingVictim.getMaxHealth() * 0.12F);

                    //
                    // Knockback victim.
                    //
                    livingVictim.setDeltaMovement((livingVictim.getX() - x) * 0.5, livingVictim.getDeltaMovement().y,
                            (livingVictim.getZ() - z) * 0.5);
                }
            }

            //
            // Disables "Seeker" enchantment.
            //
            tag.remove("surrender_seeker_x");
            tag.remove("surrender_seeker_y");
            tag.remove("surrender_seeker_z");
            tag.remove("surrender_seeker_available");

            //
            // Mark the "Seeker" enchantment activated, so that the "Blink" enchantment won't active.
            //
            seek_activated = true;
        }

        var blinkLevel = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.BLINK.get(), item);

        //
        // "Blink" enchantment can only activate if "Seeker" enchantment is not activated.
        //
        if (blinkLevel > 0 && !seek_activated) {
            //
            // The higher value of the "Blink" enchantment, the lower cooldown time.
            //
            int blink_cooldown = 20 * (6 - blinkLevel);

            //
            // Gets the last time the "Blink" enchantment was activated in order to determine if the "Blink" enchantment can be activated.
            //
            var last_active_time = tag.getInt("surrender_blink_last_active_time");

            if (last_active_time == 0 || last_active_time > player.tickCount
                    || last_active_time + blink_cooldown < player.tickCount) {
                //
                // Activating "Blink" enchantment requires at least 1 durability.
                //
                item.hurtAndBreak(1, player, null);

                //
                // Gets the player's direction and ignore the influence of the Pitch axis on the x- and y-axis distances.
                //
                var forward = Vec3.directionFromRotation(0, player.getRotationVector().y);

                //
                // Mark "Blink" enchantment as activated, so that any damage won't taken by player.
                //
                data.putBoolean("surrender_blink_active", true);

                ScheduledTickEvent.scheduled(new ScheduledTickTask(3, () -> {
                    player.setDeltaMovement(forward.x * 3, player.getDeltaMovement().y, forward.z * 3);
                }).end(() -> {
                    data.putBoolean("surrender_blink_active", false);

                    //
                    // The "Blink" enchantment causes the player to dash in the direction of the player's direction for a distance,
                    // and upon completion of the dash, deals damage to entities around the player based on the enchantment level.
                    //
                    for (var victim : player.level.getEntities(player,
                            player.getBoundingBox().inflate(5))) {
                        float damageBouns;

                        //
                        // Different mob types may have impact on the extra damage added by the enchantments the item has,
                        // so we need to determine the type of mob.
                        //
                        if (victim instanceof Mob mob) {
                            damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), mob.getMobType());
                        } else {
                            damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEFINED);
                        }

                        //
                        // Caculate player's base damage (will be affected by potion effects)
                        //
                        damageBouns += (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

                        //
                        // Deal damage (40% AD * Enchant level).
                        //
                        victim.hurt(DamageSource.playerAttack(player), damageBouns * 0.4F * blinkLevel);

                        try {
                            //
                            // Thread unsafe (I don't know how to fix it because player.getServer() is null).
                            //
                            player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1);
                        } catch (Exception e) {
                            // Do nothing.
                        }

                        //
                        // Each hit on target costs one durability, and all entities will take damage regradless
                        // of whether weapon has been destroyed.
                        //
                        item.hurtAndBreak(1, player, null);

                        //
                        // Visual effects
                        //
                        player.swing(event.getHand());

                        //
                        // If the "Blink" enchantment successfully kills an entity or attacks a
                        // dead entity, it resets the cooldown of the "Blink" enchantment.
                        //
                        if (victim instanceof LivingEntity living && living.getHealth() <= 0.0F) {
                            tag.putInt("surrender_blink_last_active_time", 0);
                        }
                    }

                    //
                    // Visual effect.
                    //
                    player.swing(event.getHand());

                    //
                    // Resets the player's motion so that the player won't dash too far.
                    //
                    player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
                }));

                //
                // Sets the last time the "Blink" enchantment was activated, which is used to caculate cooldown.
                //
                tag.putInt("surrender_blink_last_active_time", player.tickCount);
            }
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

        var magicReductionLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.MAGIC_REDUCTION.get(),
                entity);
        if (magicReductionLevel > 0 && event.getSource().isMagic()) {
            event.setAmount(event.getAmount() * (1.0F - magicReductionLevel * 0.12F));
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

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        var entity = event.getEntityLiving();
        var data = entity.getPersistentData();

        if (!data.getBoolean("surrender_undying")) {
            var undyingLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.UNDYING.get(), entity);
            if (undyingLevel > 0) {
                event.setCanceled(true);
                entity.setHealth(entity.getMaxHealth());

                data.putBoolean("surrender_undying", true);
                ScheduledTickEvent.scheduled(new ScheduledTickTask(20 * 3 - (5 - undyingLevel) * 10, () -> {
                    if (entity instanceof Player player) {
                        player.getFoodData().setSaturation(0);
                    }

                    entity.setHealth(entity.getHealth() - 1.0F / 3.0F);
                    if (entity.getHealth() <= 0.0F) {

                    }
                }).end(() -> {
                    if (entity.getHealth() <= 0.0F) {
                        entity.die(event.getSource());
                    }

                    data.putBoolean("surrender_undying", false);
                }).terminate(() -> entity.getHealth() <= 0.0F));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(final LivingAttackEvent event) {
        var entity = event.getEntityLiving();
        var data = entity.getPersistentData();

        //
        // Avoiding taking damage while blinking.
        //
        if (data.getBoolean("surrender_blink_active")) {
            event.setCanceled(true);
        }
    }
}
