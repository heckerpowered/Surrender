package heckerpowered.surrender.common.core.util.player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Player utility, this class consists exclusively of static methods that operate on players.
 *
 * @author Heckerpowered
 */
@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PlayerUtil {
    private PlayerUtil() {
    }

    /**
     * Synchornize player's movement to client.
     *
     * @param player The player to synchronize, can not be null.
     * @throws NullPointerException if {@code player} or {@link ServerPlayer#connection} is null.
     */
    public static void synchornizeMovement(@Nonnull final ServerPlayer player) {
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
    }

    /**
     * Synchornize player's movement to client, checks if {@code player} is server player.
     *
     * @param player The player to synchronize, can not be null.
     * @throws NullPointerException if {@code player} or {@link ServerPlayer#connection} is null.
     */
    public static void synchornizeMovement(@Nonnull final Player player) {
        if (player instanceof final ServerPlayer serverPlayer) {
            synchornizeMovement(serverPlayer);
        }
    }

    /**
     * Synchornize player's movement to client, this function is the nullable version of {@link #synchornizeMovement(Player)}.
     *
     * @param player The player to synchronize, can be null.
     */
    public static void synchornizeMovementNullable(@Nullable final ServerPlayer player) {
        if (player != null) {
            synchornizeMovement(player);
        }
    }

    /**
     * Synchornize player's movement to client, this function is the nullable version of {@link #synchornizeMovement(Player)}.
     *
     * @param player The player to synchronize, can be null.
     */
    public static void synchornizeMovementNullable(@Nullable final Player player) {
        if (player != null) {
            synchornizeMovement(player);
        }
    }
}
