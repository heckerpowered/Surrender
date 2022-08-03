package heckerpowered.surrender.common.content.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

/**
 * Nuclear explosion deals an additional 100% of maximum life damage to entities within one-half of
 * the explosion range, and applies harmful effects to entities within twice the explosion range.
 *
 * @author Heckerpowered
 */
public final class NuclearExplosion extends Explosion {

    /**
     * Create a new nuclear explosion.
     *
     * @param level The level to create the explosion in.
     * @param source The entity that caused the explosion.
     * @param x The x coordinate of the explosion.
     * @param y The y coordinate of the explosion.
     * @param z The z coordinate of the explosion.
     * @param radius The radius of the explosion.
     * @param fire Indicates that should the explosion causes fire.
     * @param blockInteraction Indicates that how the explosion should interact with blocks.
     */
    public NuclearExplosion(Level level, Entity source, double x, double y, double z, float radius, boolean fire,
            BlockInteraction blockInteraction) {
        super(level, source, x, y, z, radius, fire, blockInteraction);
    }
}
