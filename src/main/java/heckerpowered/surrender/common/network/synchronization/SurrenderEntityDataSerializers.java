package heckerpowered.surrender.common.network.synchronization;

import java.util.Optional;

import heckerpowered.surrender.common.core.Location;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

public final class SurrenderEntityDataSerializers {
    private SurrenderEntityDataSerializers() {
    }

    public static final EntityDataSerializer<Location> LOCATION = EntityDataSerializer.simple((buffer, location) -> {
        buffer.writeResourceKey(location.level());
        buffer.writeDouble(location.x);
        buffer.writeDouble(location.y);
        buffer.writeDouble(location.z);
    }, buffer -> {
        return new Location(buffer.readResourceKey(Registry.DIMENSION_REGISTRY),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    });

    public static final EntityDataSerializer<Optional<Location>> OPTIONAL_LOCATION = EntityDataSerializer
            .optional((buffer, location) -> {
                buffer.writeResourceKey(location.level());
                buffer.writeDouble(location.x);
                buffer.writeDouble(location.y);
                buffer.writeDouble(location.z);
            }, buffer -> {
                return new Location(buffer.readResourceKey(Registry.DIMENSION_REGISTRY),
                        buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            });

    public static final EntityDataSerializer<Vec3> VECTOR = EntityDataSerializer.simple((buffer, vector) -> {
        buffer.writeDouble(vector.x);
        buffer.writeDouble(vector.y);
        buffer.writeDouble(vector.z);
    }, buffer -> {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    });

    public static final EntityDataSerializer<Optional<Vec3>> OPTIONAL_VECTOR = EntityDataSerializer
            .optional((buffer, vector) -> {
                buffer.writeDouble(vector.x);
                buffer.writeDouble(vector.y);
                buffer.writeDouble(vector.z);
            }, buffer -> {
                return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
            });
}
