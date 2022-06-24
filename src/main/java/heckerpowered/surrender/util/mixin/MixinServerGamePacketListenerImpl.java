package heckerpowered.surrender.util.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Shadow
    public Player player;

    @Inject(method = "handleMovePlayer", at = @At("HEAD"), cancellable = true)
    public void handleMovePlayer(ServerboundMovePlayerPacket packet, CallbackInfo info) {
        if (MixinUtil.isMovementDisabled(player)) {
            info.cancel();
        }
    }
}
