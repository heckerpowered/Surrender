package heckerpowered.surrender.common.event;

import heckerpowered.surrender.client.ClientMethod;
import heckerpowered.surrender.common.content.SurrenderEnchantments;
import heckerpowered.surrender.common.core.util.SurrenderUtil;
import heckerpowered.surrender.common.core.util.scheduled.ScheduledTickTask;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.BadRespawnPointDamage;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class EnchantmentEventHandler {
    private EnchantmentEventHandler() {
    }

    public static final EntityDataAccessor<Boolean> DATA_BLINK_ACTIVE = SynchedEntityData.defineId(
            Player.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> DATA_BLISTERING = SynchedEntityData.defineId(Player.class,
            EntityDataSerializers.BOOLEAN);

    @SubscribeEvent
    public static final void onLivingDead(final LivingDeathEvent event) {
        final var source = event.getSource().getEntity();
        if (source != null && source instanceof Player player) {
            final var mainHandItem = player.getMainHandItem();
            final var offHandItem = player.getOffhandItem();
            final var entity = event.getEntity();

            if (entity.level.isClientSide) {
                return;
            }

            //
            // If both main hand item and off hand item have enchantment "Seeker",
            // "Seeker" enchantment only works on the main hand.
            //
            final var mainHandSeekerLevel = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.SEEKER.get(),
                    mainHandItem);
            final var offHandSeekerLevel = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.SEEKER.get(),
                    offHandItem);
            if (mainHandSeekerLevel > 0) {
                var tag = mainHandItem.getOrCreateTag();
                tag.putDouble("surrender_seeker_x", entity.getX());
                tag.putDouble("surrender_seeker_y", entity.getY());
                tag.putDouble("surrender_seeker_z", entity.getZ());
                tag.putBoolean("surrender_seeker_available", true);
                tag.putInt("surrender_seeker_level", mainHandSeekerLevel);
            } else if (offHandSeekerLevel > 0) {
                var tag = offHandItem.getOrCreateTag();
                tag.putDouble("surrender_seeker_x", entity.getX());
                tag.putDouble("surrender_seeker_y", entity.getY());
                tag.putDouble("surrender_seeker_z", entity.getZ());
                tag.putBoolean("surrender_seeker_available", true);
                tag.putInt("surrender_seeker_level", offHandSeekerLevel);
            }
        }
    }

    @SubscribeEvent
    public static final void onRightClickItem(final RightClickItem event) {
        final var item = event.getItemStack();
        final var tag = item.getTag();
        final var player = event.getPlayer();
        final var synchornizedData = player.getEntityData();
        final var playerPersistentData = player.getPersistentData();
        final var interactionHand = event.getHand();

        if (player.getLevel().isClientSide()) {
            return;
        }

        boolean seek_activated = false;

        //
        // Determine whether "Seeker" enchantment is available.
        //
        if (tag != null && tag.getBoolean("surrender_seeker_available")) {
            final var x = tag.getDouble("surrender_seeker_x");
            final var y = tag.getDouble("surrender_seeker_y");
            final var z = tag.getDouble("surrender_seeker_z");
            final var seekerLevel = tag.getInt("surrender_seeker_level");

            //
            // "Seeker" enchantment can only teleport while player is inside 20m of the
            // target.
            //
            if (player.distanceToSqr(x, y, z) < 400.0D) {

                //
                // Teleport will set player's motion to zero, so we need to set it back to the
                // original value after teleporting.
                //
                final var movement = player.getDeltaMovement();

                //
                // Teleport player.
                //
                player.teleportTo(x, y, z);

                //
                // Set player's motion back to the original value.
                //
                player.setDeltaMovement(movement);

                //
                // Synchornize player's motion.
                //
                SurrenderUtil.synchornizeMovement(player);

                //
                // Visual effects
                //
                player.swing(event.getHand());

                //
                // Play sound.
                //
                player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);

                //
                // Hurt entities inside 20m of the player.
                //
                for (var victim : player.level.getEntities(player, player.getBoundingBox().inflate(5))) {
                    if (victim instanceof LivingEntity livingVictim) {
                        livingVictim.hurt(DamageSource.playerAttack(player),
                                2.0F + livingVictim.getMaxHealth() * 0.12F * seekerLevel);

                        //
                        // Knockback victim.
                        //
                        livingVictim.setDeltaMovement((livingVictim.getX() - x) * 0.5,
                                livingVictim.getDeltaMovement().y,
                                (livingVictim.getZ() - z) * 0.5);

                        //
                        // Synchornize victim's motion if the victim is a player.
                        //
                        SurrenderUtil.synchornizeMovement(livingVictim);
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
                // Mark the "Seeker" enchantment activated, so that the "Blink" enchantment
                // won't active.
                //
                seek_activated = true;
            }
        }

        final var blinkLevel = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.BLINK.get(), item);

        //
        // "Blink" enchantment can only activate if "Seeker" enchantment is not
        // activated.
        // "Blink" cannot be re-activated while it is active.
        //
        // "Blink" use bool to determine whether it is active, so when "Blink" is
        // re-activated
        // while it is active, the player won't be rendered correctly.
        //
        // If you want "Blink" to reactivate when it is activated, change the bool to a
        // count.
        //
        if (blinkLevel > 0 && !seek_activated && !synchornizedData.get(DATA_BLINK_ACTIVE)) {
            //
            // The higher value of the "Blink" enchantment, the lower cooldown time.
            //
            final int blink_cooldown = 20 * (6 - blinkLevel);

            //
            // Gets the last time the "Blink" enchantment was activated in order to
            // determine if the "Blink" enchantment can be activated.
            //
            final var last_active_time = tag.getInt("surrender_blink_last_active_time");

            //
            // No cooldown recorded, or the cooldown is over, so the "Blink" enchantment can be activated.
            //
            if (last_active_time == 0 || last_active_time > player.tickCount
                    || last_active_time + blink_cooldown < player.tickCount) {
                //
                // Activating "Blink" enchantment requires at least 1 durability.
                //
                item.hurtAndBreak(1, player, v -> {
                    //
                    // Broadcast break event.
                    //
                    player.broadcastBreakEvent(interactionHand);

                    //
                    // Trigger forge events.
                    //
                    ForgeEventFactory.onPlayerDestroyItem(player, item, interactionHand);
                });

                //
                // Gets the player's direction and ignore the influence of the Pitch axis on the
                // x- and y-axis distances.
                //
                final var forward = Vec3.directionFromRotation(0, player.getRotationVector().y);

                //
                // Mark "Blink" enchantment as activated, so that any damage won't taken by
                // player.
                //
                // SyncedEntityData is used instead of NBT in order to synchronize data with the
                // client
                // so that invisible units can be rendered correctly.
                //
                synchornizedData.set(DATA_BLINK_ACTIVE, true);

                //
                // Make the player do uniform linear motion
                //
                ScheduledTickEvent.scheduled(new ScheduledTickTask(3, () -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        //
                        // Make the player dash.
                        //
                        player.setDeltaMovement(forward.x * 3, player.getDeltaMovement().y, forward.z * 3);

                        //
                        // The server does not automatically synchronize the player's motion,
                        // so we need to synchronize it manually.
                        //
                        SurrenderUtil.synchornizeMovement(player);

                    }
                }).end(() -> {
                    synchornizedData.set(DATA_BLINK_ACTIVE, false);

                    //
                    // The "Blink" enchantment causes the player to dash in the direction of the
                    // player's direction for a distance,
                    // and upon completion of the dash, deals damage to entities around the player
                    // based on the enchantment level.
                    //
                    for (var victim : player.level.getEntities(player,
                            player.getBoundingBox().inflate(2))) {
                        float damageBouns;

                        //
                        // Different mob types may have impact on the extra damage added by the
                        // enchantments the item has,
                        // so we need to determine the type of mob.
                        //
                        if (victim instanceof Mob mob) {
                            //
                            // The victim is a mob.
                            //
                            damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), mob.getMobType());
                        } else {
                            //
                            // The victim isn't a mob.
                            //
                            damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEFINED);
                        }

                        //
                        // Caculate player's base damage (will be affected by potion effects)
                        //
                        damageBouns += (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

                        //
                        // Deal damage (40% AD * Enchant level).
                        // If the attack was blocked, or the attack cannot be applied,
                        // no durability is consumed.
                        //
                        if (victim.isAttackable()
                                && !victim.hurt(DamageSource.playerAttack(player), damageBouns * 0.4F * blinkLevel)) {
                            //
                            // Each hit on target costs one durability, and all entities will take damage
                            // regradless
                            // of whether weapon has been destroyed.
                            //
                            item.hurtAndBreak(1, player, v -> {
                                //
                                // Broadcast break event.
                                //
                                player.broadcastBreakEvent(interactionHand);

                                //
                                // Trigger forge events.
                                //
                                ForgeEventFactory.onPlayerDestroyItem(player, item, interactionHand);
                            });
                        }

                        //
                        // Play sound is thread unsafe.
                        //
                        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientMethod::playSweepSound);

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

                    //
                    // The server does not automatically synchronize the player's motion,
                    // so we need to synchronize it manually.
                    //
                    SurrenderUtil.synchornizeMovement(player);
                }));

                //
                // Sets the last time the "Blink" enchantment was activated, which is used to
                // caculate cooldown.
                //
                tag.putInt("surrender_blink_last_active_time", player.tickCount);
            }
        }

        final var blisteringLevel = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.BLISTERING.get(),
                item);

        if (blisteringLevel > 0 && !tag.getBoolean("surrender_blistering_active")) {

            //
            // The higher value of the "Blistering" enchantment, the lower cooldown time.
            //
            final int blistering_cooldown = 40 * (9 - blisteringLevel);

            //
            // Gets the last time the "Blistering" enchantment was activated in order to
            // determine if the "Blistering" enchantment can be activated.
            //
            final int last_active_time = tag.getInt("surrender_blistering_last_active_time");

            if (last_active_time == 0 || last_active_time > player.tickCount
                    || last_active_time + blistering_cooldown < player.tickCount) {

                final var forward = Vec3.directionFromRotation(0, player.getRotationVector().y);

                //
                // Mark "Blistering" enchantment as activated, so that any damage won't taken by
                // player.
                //
                playerPersistentData.putBoolean("surrender_blistering_active", true);

                //
                // Make the player do uniform linear motion
                //
                ScheduledTickEvent.scheduled(new ScheduledTickTask(3, () -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        player.setDeltaMovement(forward.x * 3, player.getDeltaMovement().y, forward.z * 3);

                        SurrenderUtil.synchornizeMovement(player);
                    }
                    for (var victim : player.level.getEntities(player,
                            player.getBoundingBox().inflate(1.3D + (blisteringLevel / 10)))) {
                        float damageBouns;

                        //
                        // Different mob types may have impact on the extra damage added by the
                        // enchantments the item has,
                        // so we need to determine the type of mob.
                        //
                        if (victim instanceof Mob mob) {
                            //
                            // The victim is a mob.
                            //
                            damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), mob.getMobType());
                        } else {
                            //
                            // The victim isn't a mob.
                            //
                            damageBouns = EnchantmentHelper.getDamageBonus(player.getMainHandItem(), MobType.UNDEFINED);
                        }

                        //
                        // Caculate player's base damage (will be affected by potion effects)
                        //
                        damageBouns += (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);

                        //
                        // Deal damage (40% AD * Enchant level).
                        // If the attack was blocked, or the attack cannot be applied,
                        // no durability is consumed.
                        //
                        if (victim.isAttackable() && !victim.hurt(
                                DamageSource.playerAttack(player), damageBouns * 0.4F * blisteringLevel)) {
                            //
                            // Each hit on target costs one durability, and all entities will take damage
                            // regradless
                            // of whether weapon has been destroyed.
                            //
                            item.hurtAndBreak(1, player, v -> {
                                //
                                // Broadcast break event.
                                //
                                player.broadcastBreakEvent(interactionHand);

                                //
                                // Trigger forge events.
                                //
                                ForgeEventFactory.onPlayerDestroyItem(player, item, interactionHand);
                            });

                            tag.putInt("surrender_blistering_last_active_time", 0);
                        }

                        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientMethod::playSweepSound);

                    }
                }).end(() -> {
                    synchornizedData.set(DATA_BLISTERING, false);

                    //
                    // Visual effects.
                    //
                    player.swing(event.getHand());

                    //
                    // Make player stop dashing instantly.
                    //
                    player.setDeltaMovement(0, player.getDeltaMovement().y, 0);

                    //
                    // The server does not automatically synchronize the player's motion,
                    // so we need to synchronize it manually.
                    //
                    SurrenderUtil.synchornizeMovement(player);
                }));

                tag.putInt("surrender_blistering_last_active_time", player.tickCount);
            }
        }
    }

    @SubscribeEvent
    public static final void onLivingHurt(final LivingHurtEvent event) {
        final var entity = event.getEntityLiving();

        if (entity.level.isClientSide) {
            return;
        }

        //
        // May be null.
        //
        final var sourceEntity = event.getSource().getEntity();

        //
        // Source entity may be null, and only LivingEntity may have enchantments.
        // so we need to check it, and detemine whether the source entity is a
        // LivingEntity,
        // and cast it.
        //
        if (sourceEntity != null && sourceEntity instanceof LivingEntity source) {
            //
            // Gets entity's persistent data.
            //
            final var persistentData = sourceEntity.getPersistentData();

            final var undyingLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.UNDYING.get(), source);

            //
            // Determine whether "Undying" is activating.
            //
            if (undyingLevel > 0 && persistentData.getBoolean("surrender_undying")) {
                //
                // Increase damage.
                //
                event.setAmount(event.getAmount() + entity.getMaxHealth() * (0.1F + 0.02F * undyingLevel));
            }

            final var decisiveStrikeLevel = EnchantmentHelper.getEnchantmentLevel(
                    SurrenderEnchantments.DECISIVE_STRIKE.get(),
                    source);

            //
            // "Decisive Strike" enchantment can only activate if the source entity's health
            // is below 40%.
            //
            if (decisiveStrikeLevel > 0 && entity.getHealth() < entity.getMaxHealth() * 0.4F) {
                event.setAmount(event.getAmount() * (1.0F + decisiveStrikeLevel * 0.05F));
            }

            final var lastStandLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.LAST_STAND.get(),
                    source);

            //
            // "Last Stand" enchantment can only activate if the attacker's health is below
            // 40%.
            //
            if (lastStandLevel > 0 && source.getHealth() < source.getMaxHealth() * 0.4F) {
                event.setAmount(event.getAmount() * (1.0F + lastStandLevel * 0.05F));
            }

            final var ripperLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.RIPPER.get(), source);

            //
            // "Ripper" enchantment always actives.
            //
            if (ripperLevel > 0) {
                event.setAmount(event.getAmount() * (1.0F + ripperLevel * 0.2F));
            }

            final var hasteLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.HASTE.get(), source);
            if (hasteLevel > 0) {
                //
                // Invisible, hide icon and particles.
                //
                source.addEffect(
                        new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * hasteLevel, hasteLevel, false, false,
                                false));
            }
        }

        final int guardian_cooldown = 20 * 30;

        //
        // "Guardian" enchantment actives when taking damage that can cause you to drop
        // below 30% of your health.
        //
        if (entity.getHealth() - event.getAmount() <= entity.getMaxHealth() * 0.3F) {
            //
            // Caculate coolddown for each armor seperatly (30s)
            //
            for (final var armor : event.getEntityLiving().getArmorSlots()) {
                final var level = EnchantmentHelper.getTagEnchantmentLevel(SurrenderEnchantments.GUARDIAN.get(), armor);

                //
                // Each level of "Guardian" enchantment provides 10 shield amount.
                //
                final var absoprtionAmount = 10 * level;
                final var tag = armor.getOrCreateTag();

                final var last_active_time = tag.getInt("surrender_guardian_last_active_time");
                if (last_active_time == 0 || last_active_time > entity.tickCount
                        || last_active_time + guardian_cooldown < entity.tickCount) {
                    if (level > 0) {
                        entity.setAbsorptionAmount(entity.getAbsorptionAmount() + absoprtionAmount);

                        //
                        // Sets the last time guardian was active in order to caculate cooldown.
                        //
                        tag.putInt("surrender_guardian_last_active_time", entity.tickCount);
                    }
                }
            }
        }

        final var magicReductionLevel = EnchantmentHelper.getEnchantmentLevel(
                SurrenderEnchantments.MAGIC_REDUCTION.get(),
                entity);

        //
        // Determine whether damage is magic because the "Magic Reduction" enchantment
        // is only effective on magic damage.
        //
        if (magicReductionLevel > 0 && event.getSource().isMagic()) {
            //
            // Reduce magic damage by 5% per level of "Magic Reduction" enchantment.
            //
            event.setAmount(event.getAmount() * (1.0F - magicReductionLevel * 0.12F));
        }

        if (sourceEntity != null && sourceEntity instanceof LivingEntity source) {
            final var regeneratorLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.REGENERATOR.get(),
                    source);
            if (regeneratorLevel > 0) {
                //
                // Heal amount = 2% * damaged health * Enchantment level.
                //
                var healAmount = regeneratorLevel * 0.02F * (source.getMaxHealth() - source.getHealth());
                if (source.getHealth() <= source.getMaxHealth() * 0.25) {
                    //
                    // Triple the heal amount if the your health is below 25%.
                    //
                    healAmount *= 3;
                } else if (source.getHealth() <= source.getMaxHealth() * 0.5) {
                    //
                    // Double the heal amount if the your health is below 50%.
                    //
                    healAmount *= 2;
                }

                source.heal(healAmount);
            }

            final var shieldRadierLevel = EnchantmentHelper.getEnchantmentLevel(
                    SurrenderEnchantments.SHIELD_RAIDER.get(),
                    source);
            if (shieldRadierLevel > 0) {
                //
                // 10% shield reduction per level.
                //
                final var reductionAmount = 0.1F * shieldRadierLevel;

                //
                // Reduce sheidl.
                //
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() * reductionAmount);
            }

            if (source instanceof Player player) {
                final var predatorLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.PREDATOR.get(),
                        source);
                if (predatorLevel > 0) {
                    var foodData = player.getFoodData();
                    foodData.setSaturation(foodData.getSaturationLevel() + predatorLevel);
                    foodData.setFoodLevel(foodData.getFoodLevel() + predatorLevel);
                }
            }
        }

        final var persisdentData = entity.getPersistentData();
        if (persisdentData.getBoolean("surrender_undying")) {
            event.setAmount(event.getAmount() * 0.1F);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(final LivingDeathEvent event) {
        final var entity = event.getEntityLiving();
        final var sourceEntiity = event.getSource().getEntity();
        final var data = entity.getPersistentData();

        if (entity.level.isClientSide) {
            return;
        }

        //
        // Determine whether undying is activating.
        //
        if (!data.getBoolean("surrender_undying")) {
            final var undyingLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.UNDYING.get(), entity);
            if (undyingLevel > 0) {
                //
                // Prevent the entity from dying.
                //
                event.setCanceled(true);

                //
                // Set entity's health to it's max health so that it won't die.
                //
                entity.setHealth(entity.getMaxHealth());

                //
                // Mark "Undying" enchantment is activing so that it won't prevent entity from
                // dying again.
                //
                data.putBoolean("surrender_undying", true);

                //
                // Continuous deduction of 100% of the entity's health for a period of time
                // based on the level of enchantment.
                //
                var ticks = 20 * 3 - (5 - undyingLevel) * 10;
                ScheduledTickEvent.scheduled(new ScheduledTickTask(ticks, () -> {
                    if (entity instanceof Player player) {
                        //
                        // Set player's "Saturation" to zero so that player won't heal by "Saturation".
                        //
                        player.getFoodData().setSaturation(0);
                    }

                    entity.setHealth(entity.getHealth() - entity.getMaxHealth() / ticks);
                }).end(() -> {
                    //
                    // Determine if the entity is dead.
                    //
                    if (entity.getHealth() <= 0.0F) {
                        //
                        // Make entity drop items and send death message.
                        //
                        entity.die(event.getSource());
                    }

                    //
                    // Mark "Undying" enchantment is not activating so that it can be activated again.
                    //
                    data.putBoolean("surrender_undying", false);

                    //
                    // Terminates tick task when entity is dead, so that the player will be
                    // re-spawned without dyinng again.
                    //
                }).terminate(() -> entity.getHealth() <= 0.0F));
            }
        }

        if (sourceEntiity != null && sourceEntiity instanceof Player player) {
            final var experienceLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.EXPERIENCE.get(),
                    player);
            if (experienceLevel > 0) {
                //
                // Give experience to the player.
                //
                player.giveExperiencePoints(experienceLevel);

                //
                // Give durability to player's item in hand.
                //
                for (final var hand : InteractionHand.values()) {
                    final var item = player.getItemInHand(hand);
                    item.setDamageValue(item.getDamageValue() - experienceLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(final LivingAttackEvent event) {
        final var entity = event.getEntityLiving();
        final var synchornizedData = entity.getEntityData();
        final var source = event.getSource();
        if (entity.level.isClientSide) {
            return;
        }

        //
        // Avoiding taking damage while blinking.
        //
        if (entity instanceof Player
                && (synchornizedData.get(DATA_BLINK_ACTIVE) || synchornizedData.get(DATA_BLISTERING))) {
            event.setCanceled(true);
        }

        if (source.getEntity() != null) {
            final var sourceEntity = source.getEntity();
            final var persistentData = sourceEntity.getPersistentData();

            //
            // Determine whether the damage source can be reused.
            //
            if (persistentData.getBoolean("surrender_undying") && (source instanceof BadRespawnPointDamage
                    || source instanceof EntityDamageSource)) {
                source.bypassArmor().bypassEnchantments().bypassInvul().bypassMagic();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        final var entity = event.getEntity();
        if (entity.level.isClientSide) {
            return;
        }

        //
        // Determine if the entity is a projectile.
        //
        if (entity instanceof Projectile projectile) {
            final var owner = projectile.getOwner();

            //
            // Determine if the projectile's owner is a living entity in order to determine
            // "Explosion" enchantment level.
            //
            if (owner instanceof LivingEntity living) {
                final var explosionLevel = EnchantmentHelper.getEnchantmentLevel(SurrenderEnchantments.EXPLOSION.get(),
                        living);
                if (explosionLevel > 0) {

                    //
                    // Sets the "Explosion" enchantment level in order to explode.
                    //
                    projectile.getPersistentData().putInt("surrender_explosion_level", explosionLevel);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(final ProjectileImpactEvent event) {
        final var projectile = event.getProjectile();
        final var data = projectile.getPersistentData();

        if (projectile.level.isClientSide) {
            return;
        }

        final var explosionLevel = data.getInt("surrender_explosion_level");
        if (explosionLevel > 0) {
            //
            // Explode the projectile.
            //
            projectile.level.explode(projectile.getOwner(), projectile.getX(), projectile.getY(), projectile.getZ(),
                    explosionLevel * 2, Explosion.BlockInteraction.NONE);
        }
    }

    @SubscribeEvent
    public static final void onEntityConstruct(final EntityConstructing event) {
        final var entity = event.getEntity();
        if (entity instanceof Player) {
            entity.getEntityData().define(DATA_BLINK_ACTIVE, false);
            entity.getEntityData().define(DATA_BLISTERING, false);
        }
    }
}
