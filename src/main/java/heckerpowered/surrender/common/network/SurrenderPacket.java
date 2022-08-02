package heckerpowered.surrender.common.network;

import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Represents a packet, all Surrender mod packets should implement this interface.
 * Any general-purpose packet implements implementation classes should provide a static
 * decode method to register: <pre> {@code public static SurrenderPacket decode(@Nonnull final FriendlyByteBuf buffer);} </pre>
 * Where {@code SurrenderPacket} is the implementation class.
 *
 * <p> The methods of this class all thread-safe, the caller thread is same thread as the game thread.
 *
 * @author Heckerpowered
 */
@ThreadSafe
public interface SurrenderPacket {
    /**
     * Handle packet, do not call {@link NetworkEvent.Context#enqueueWork(Runnable)}, the wrapper will do that for you.
     * @param context The context of the packet.
     */
    void handle(@Nonnull final NetworkEvent.Context context);

    /**
     * Encode the packet, write parameters to this buffer sequentially.
     *
     * @param buffer The buffer to write to.
     */
    void encode(@Nonnull final FriendlyByteBuf buffer);

    /**
     * Handle the packet, this is a wapper of {@link #handle(NetworkEvent.Context)}.
     *
     * @param <T> The type of the packet.
     * @param message The packet to handle, may be null.
     * @param contextSupplier The supplier of the context.
     */
    static <T extends SurrenderPacket> void handle(@Nullable final T message,
            @Nonnull final Supplier<NetworkEvent.Context> contextSupplier) {
        if (message != null) {
            //
            // Message should never be null unless something went horribly wrong decoding.
            // In which case we don't want to try enqueuing handling it, or set the packet as handled.
            //
            final var context = contextSupplier.get();
            context.enqueueWork(() -> message.handle(context));
            context.setPacketHandled(true);
        }
    }
}
