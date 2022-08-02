package heckerpowered.surrender.common.network.clientbound;

import java.util.Objects;

import javax.annotation.Nonnull;

import heckerpowered.surrender.common.network.SurrenderPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

public final class ClientboundDisplayItemActivationPacket implements SurrenderPacket {
    private final ItemStack stack;

    public ClientboundDisplayItemActivationPacket(@Nonnull final ItemStack stack) {
        this.stack = Objects.requireNonNull(stack);
    }

    @Override
    public void handle(@Nonnull final Context context) {
        final var minecraft = Minecraft.getInstance();
        minecraft.gameRenderer.displayItemActivation(stack);
    }

    @Override
    public void encode(@Nonnull final FriendlyByteBuf buffer) {
        buffer.writeItem(stack);
    }

    public static final ClientboundDisplayItemActivationPacket decode(@Nonnull final FriendlyByteBuf buffer) {
        return new ClientboundDisplayItemActivationPacket(buffer.readItem());
    }
}
