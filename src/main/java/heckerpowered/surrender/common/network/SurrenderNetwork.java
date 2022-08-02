package heckerpowered.surrender.common.network;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import heckerpowered.surrender.common.SurrenderMod;
import heckerpowered.surrender.common.network.clientbound.ClientboundDisplayItemActivationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * This class consists of static methods that read or write data to
 * or from a {@link FriendlyByteBuf}, and static methods that send packet
 * to a supplied target.
 *
 * @author Heckerpowered
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SurrenderNetwork {

    /**
     * Index for registering packet.
     */
    private static int index;

    /**
     * Directly reference a slf4j marker
     */
    public static final Marker MARKER = MarkerFactory.getMarker("SURRENDERNETWORK");

    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private SurrenderNetwork() {
    }

    /**
     * Define a channel to register and send packets.
     */
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(SurrenderMod.resource(SurrenderMod.MODID))
            .clientAcceptedVersions(SurrenderMod.VERSION.get().toString()::equals)
            .serverAcceptedVersions(SurrenderMod.VERSION.get().toString()::equals)
            .networkProtocolVersion(SurrenderMod.VERSION.get()::toString).simpleChannel();

    /**
     * Register client to server packet.
     *
     * @param <T> The type of the packet, must implement {@link SurrenderPacket}.
     * @param type The class object of the packet.
     * @param decoder The decoder function of the packet.
     */
    public static <T extends SurrenderPacket> void registerServerbound(@Nonnull final Class<T> type,
            @Nonnull final Function<FriendlyByteBuf, T> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
    }

    /**
     * Register server to client packet.
     *
     * @param <T> The type of the packet, must implement {@link SurrenderPacket}.
     * @param type The class object of the packet.
     * @param decoder The decoder function of the packet.
     */
    public static <T extends SurrenderPacket> void registerClientbound(@Nonnull final Class<T> type,
            @Nonnull final Function<FriendlyByteBuf, T> decoder) {
        registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Register packet with supplied direction.
     *
     * @param <T> The type of the packet, must implement {@link SurrenderPacket}.
     * @param type The class object of the packet.
     * @param decoder The decoder function of the packet.
     * @param direction The direction of the packet to be sent to.
     */
    public static <T extends SurrenderPacket> void registerMessage(@Nonnull final Class<T> type,
            @Nonnull final Function<FriendlyByteBuf, T> decoder, @Nonnull final NetworkDirection direction) {
        CHANNEL.registerMessage(index++, type, SurrenderPacket::encode, decoder, SurrenderPacket::handle,
                Optional.of(direction));
    }

    /**
     * Send this message to the specified player.
     *
     * @param message the message to send
     * @param player the player to send it to
     */
    public static <T> void sendTo(@Nonnull final T message, @Nonnull final ServerPlayer player) {
        //
        // Determines if the player is fake player.
        //
        if (player instanceof FakePlayer) {
            return;
        }

        CHANNEL.sendTo(message, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Send this message to everyone connected to the server.
     *
     * @param message message to send
     */
    public static <T> void sendToAll(@Nonnull final T message) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Send this message to everyone connected to the server if the server has loaded.
     *
     * @param message message to send
     *
     * @apiNote This is useful for reload listeners
     */
    public static <T> void sendToAllIfLoaded(T message) {
        //
        // Determines if the server has loaded.
        //
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            sendToAll(message);
        }
    }

    /**
     * Send this message to everyone within the supplied dimension.
     *
     * @param message the message to send
     * @param dimension the dimension to target
     */
    public static <T> void sendToDimension(@Nonnull final T message, @Nonnull final ResourceKey<Level> dimension) {
        CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    /**
     * Send this message to the server.
     *
     * @param message the message to send
     */
    public static <T> void sendToServer(@Nonnull final T message) {
        CHANNEL.sendToServer(message);
    }

    public static <T> void sendToAllTracking(@Nonnull final T message, @Nonnull final Entity entity) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    public static <T> void sendToAllTrackingAndSelf(T message, Entity entity) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    public static <T> void sendToAllTracking(@Nonnull final T message, @Nonnull final BlockEntity entity) {
        sendToAllTracking(message, entity.getLevel(), entity.getBlockPos());
    }

    public static <T> void sendToAllTracking(T message, Level world, BlockPos location) {
        if (world instanceof final ServerLevel level) {
            //
            // If we have a ServerWorld just directly figure out the ChunkPos to not require looking up the chunk
            // This provides a decent performance boost over using the packet distributor
            //
            final var chunkSource = level.getChunkSource();
            chunkSource.chunkMap.getPlayers(new ChunkPos(location), false).forEach(p -> sendTo(message, p));
        } else {
            //
            // Otherwise, fallback to entities tracking the chunk if some mod did something odd and our world is not a ServerWorld
            //
            CHANNEL.send(PacketDistributor.TRACKING_CHUNK
                    .with(() -> world.getChunk(location.getX() >> 4, location.getZ() >> 4)), message);
        }
    }

    @SubscribeEvent
    public static final void onCommonSetup(@Nonnull final FMLCommonSetupEvent event) {
        registerClientbound(ClientboundDisplayItemActivationPacket.class,
                ClientboundDisplayItemActivationPacket::decode);
    }
}
