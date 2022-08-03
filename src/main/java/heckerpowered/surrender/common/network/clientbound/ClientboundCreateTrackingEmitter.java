package heckerpowered.surrender.common.network.clientbound;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import heckerpowered.surrender.common.network.SurrenderPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClientboundCreateTrackingEmitter implements SurrenderPacket {
    private final Entity entity;
    private final ParticleOptions particleOptions;
    private final int lifeTime;

    public ClientboundCreateTrackingEmitter(@Nonnull final Entity entity,
            @Nonnull final ParticleOptions particleOptions, @Nonnegative final int lifeTime) {
        this.entity = entity;
        this.particleOptions = particleOptions;
        this.lifeTime = lifeTime;
    }

    @Override
    public void handle(@Nonnull final Context context) {
        final var minecraft = Minecraft.getInstance();
        minecraft.particleEngine.createTrackingEmitter(entity, particleOptions, lifeTime);
    }

    @Override
    public void encode(@Nonnull final FriendlyByteBuf buffer) {
        buffer.writeInt(entity.getId());
        EntityDataSerializers.PARTICLE.write(buffer, particleOptions);
        buffer.writeInt(lifeTime);
    }

    public static final ClientboundCreateTrackingEmitter decode(@Nonnull final FriendlyByteBuf buffer) {
        final var minecraft = Minecraft.getInstance();
        return new ClientboundCreateTrackingEmitter(minecraft.level.getEntity(buffer.readInt()),
                EntityDataSerializers.PARTICLE.read(buffer), buffer.readInt());
    }
}
