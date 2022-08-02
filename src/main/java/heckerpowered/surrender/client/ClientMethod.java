package heckerpowered.surrender.client;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

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

    public static final void displayStack(@Nonnull final ItemStack stack) {
        final var minecraft = Minecraft.getInstance();
        minecraft.gameRenderer.displayItemActivation(stack);
    }
}
