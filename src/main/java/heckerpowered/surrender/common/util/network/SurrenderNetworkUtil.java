package heckerpowered.surrender.common.util.network;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import heckerpowered.surrender.common.network.SurrenderNetwork;
import heckerpowered.surrender.common.network.clientbound.ClientboundCreateTrackingEmitter;
import heckerpowered.surrender.common.network.clientbound.ClientboundDisplayItemActivationPacket;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public final class SurrenderNetworkUtil {
    public SurrenderNetworkUtil() {
    }

    public static final void displayItemActivation(@Nonnull final ItemStack stack, @Nonnull final ServerPlayer player) {
        SurrenderNetwork.sendTo(new ClientboundDisplayItemActivationPacket(stack), player);
    }

    public static final void createTrackingEmitter(@Nonnull final Entity entity,
            @Nonnull final ParticleOptions particleOptions, @Nonnegative final int lifeTime) {
        SurrenderNetwork.sendToAllTrackingAndSelf(
                new ClientboundCreateTrackingEmitter(entity, particleOptions, lifeTime),
                entity);
    }
}
