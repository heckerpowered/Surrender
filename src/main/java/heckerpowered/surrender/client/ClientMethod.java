package heckerpowered.surrender.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;

public final class ClientMethod {
    private ClientMethod() {
    }

    public static final void playSweepSound() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        minecraft.submit(() -> {
            minecraft.player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1, 1);
        });
    }
}
