package heckerpowered.surrender.common.core;

import com.google.errorprone.annotations.Immutable;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Immutable
public class Location extends Vec3 {

    private final ResourceKey<Level> level;

    public Location(ResourceKey<Level> level, double x, double y, double z) {
        super(x, y, z);
        this.level = level;
    }

    public Location(Level level, double x, double y, double z) {
        this(level.dimension(), x, y, z);
    }

    public Location(Level level, Vec3 location) {
        this(level.dimension(), location);
    }

    public Location(ResourceKey<Level> level, Vec3 location) {
        this(level, location.x, location.y, location.z);
    }

    public static Location of(final Entity entity) {
        return new Location(entity.level.dimension(), entity.position());
    }

    public ResourceKey<Level> level() {
        return level;
    }
}
