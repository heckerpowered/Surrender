package heckerpowered.surrender.common.content.entity;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.util.Asserts;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

/**
 * Represents a Neculear tnt entity, which has the same appearance and fuse time of
 * vanilla tnt, but has far more powerful than the vanilla tnt.
 *
 * @author Heckerpowered
 */
@MethodsReturnNonnullByDefault
@FieldsAreNonnullByDefault
public final class NeculearTnt extends Entity {

    /**
     * NBT won't synchornize data when there is something change, so define a synced data
     * in order to synchornize data to client.
     */
    private static final EntityDataAccessor<Integer> DATA_FUSE = SynchedEntityData.defineId(NeculearTnt.class,
            EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> DATA_EXPLOSION_RANGE = SynchedEntityData.defineId(
            NeculearTnt.class, EntityDataSerializers.FLOAT);

    /**
     * Default fuse time, in ticks.
     */
    private static final int DEFAULT_FUSE_TIME = 80;

    /**
     * Default explosion radius.
     */
    private static final float DEFAULT_EXPLOSISON_RADIUS = 50.0F;

    /**
     * The entity that ignited this tnt.
     */
    @Nullable
    private LivingEntity igniter;

    public NeculearTnt(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Create a new instance of {@link NeculearTnt}, and set this tnt's position to supplied {@code x},
     * {@code y}, {@code z} and grant a random movement.
     * <p> Movement on x-axis: <pre>{@code -Math.sin(level.random.nextDouble() * Math.PI * 2.0F) * 0.02D}</pre>
     * <p> Movement on z-axis: <pre>{@code -Math.cos(level.random.nextDouble() * Math.PI * 2.0F) * 0.02D}</pre>
     * <p> Movement on y-axis: {@code 0.2D}
     *
     * @param level the level the entity is in
     * @param x the x coordinate of the entity
     * @param y the y coordinate of the entity
     * @param z the z coordinate of the entity
     * @param owner the entity who ignited the tnt.
     */
    public NeculearTnt(@Nonnull final Level level, final double x, final double y, final double z,
            @Nullable final LivingEntity owner) {
        super(SurrenderEntityType.NECULEAR_TNT.get(), level);
        setPos(x, y, z);
        final var movement = level.random.nextDouble() * Math.PI * 2.0F;
        setDeltaMovement(-Math.sin(movement) * 0.02D, 0.2D, -Math.cos(movement) * 0.02D);
    }

    /**
     * Set the fuse time of the Tnt.
     *
     * @param fuse the fuse time, in ticks.
     */
    public final void setFuse(int fuse) {
        entityData.set(DATA_FUSE, fuse);
    }

    /**
     * Returns the fuse time of the tnt.
     *
     * @return the fuse time of the tnt
     */
    public final int getFuse() {
        return entityData.get(DATA_FUSE);
    }

    /**
     * Set the explosion radius of the tnt, it cannot be negative.
     *
     * @param radius the radius of incoming explosion
     * @throws IllegalStateException if {@code radius} is negative.
     */
    public final void setExplosionRange(@Nonnegative float radius) {
        Asserts.check(radius > 0, "Fuse time cannot be negative.");
        entityData.set(DATA_EXPLOSION_RANGE, radius);
    }

    /**
     * Get the explosion radius of the incoming explosion.
     *
     * @return the explosion radius of the incoming explosion
     */
    public final float getExplosionRadius() {
        return entityData.get(DATA_EXPLOSION_RANGE);
    }

    /**
     * Returns the entity that ignited this tnt.
     *
     * @return the entity that ignited this tnt
     */
    @Nullable
    public final LivingEntity getIgniter() {
        return igniter;
    }

    @Override
    protected final MovementEmission getMovementEmission() {
        //
        // Returns false so that minecraft won't play sounds or broadcast events
        // when tnt moves.
        //
        return MovementEmission.NONE;
    }

    @Override
    public final boolean isPickable() {
        //
        // Returns true if other Entities should be prevented from moving through this Entity.
        //
        return !isRemoved();
    }

    @Override
    protected final void defineSynchedData() {
        entityData.define(DATA_FUSE, DEFAULT_FUSE_TIME);
        entityData.define(DATA_EXPLOSION_RANGE, DEFAULT_EXPLOSISON_RADIUS);
    }

    @Override
    protected final void readAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Fuse", getFuse());
        compound.putFloat("ExplosionRange", getExplosionRadius());
    }

    @Override
    protected final void addAdditionalSaveData(CompoundTag compound) {
        setFuse(compound.getInt("Fuse"));
        setExplosionRange(compound.getFloat("ExplosionRange"));
    }

    @Override
    public final Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public final void tick() {
        //
        // Determines if we should update entity's movement
        //
        if (!isNoGravity()) {
            setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        //
        // Move the tnt
        //
        move(MoverType.SELF, getDeltaMovement());

        //
        // Scale the entity's movement.
        //
        setDeltaMovement(getDeltaMovement().scale(0.98D));

        if (onGround) {
            setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
        }

        //
        // Update fuse time
        //
        final var fuse = getFuse() - 1;
        setFuse(fuse);

        //
        // Determines if the fuse time equals to or less than 0, which means the tnt should explode.
        //
        if (fuse <= 0) {
            //
            // Remove the tnt from the level.
            //
            discard();

            if (!level.isClientSide) {
                explode();
            }
        } else {
            updateInWaterStateAndDoFluidPushing();

            //
            // Determine whether the entity is in the client side,
            // we only spawn particles in the client side.
            //
            if (level.isClientSide) {
                level.addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public final void explode() {
        //
        // Explode
        //
        level.explode(igniter, getX(), getY(0.0625D), getZ(), getExplosionRadius(), Explosion.BlockInteraction.BREAK);

        //
        // Deals damage equal to 100% of their max health to all entities within 24 meters of the center
        // of the explosion
        //
        final var position = position();
        final var damageSource = DamageSource.explosion(igniter);
        for (final var entity : level.getEntities(igniter,
                new AABB(position, position).inflate(getExplosionRadius() * 2))) {
            if (entity instanceof LivingEntity living) {
                if (living.distanceToSqr(this) <= 576.0D /* 24 * 24 = 576 */) {
                    entity.invulnerableTime = 0;
                    living.hurt(damageSource, living.getMaxHealth());
                }

                living.addEffect(new MobEffectInstance(MobEffects.POISON, 600));
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 600));
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600));
                living.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 600));
                living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600));
                living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600));
                living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600));
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 600));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600));
                living.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 600));
            }
        }
    }
}
