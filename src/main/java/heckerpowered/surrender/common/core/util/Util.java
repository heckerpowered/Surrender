package heckerpowered.surrender.common.core.util;

import javax.annotation.Nullable;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class Util {
    private Util() {
    }

    public static void synchornizeMovement(@Nullable Entity entity) {
        if (entity == null) {
            return;
        }

        if (entity instanceof ServerPlayer serverPlayer && serverPlayer.connection != null) {
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
        }
    }
}
