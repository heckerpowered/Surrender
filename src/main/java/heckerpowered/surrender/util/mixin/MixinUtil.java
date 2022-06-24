package heckerpowered.surrender.util.mixin;

import net.minecraft.world.entity.player.Player;

public final class MixinUtil {
    private static final String DISABLE_MOVEMENT = "surrender_disable_movement";

    private MixinUtil() {
    }

    public static void disableMovement(Player player) {
        var data = player.getPersistentData();
        data.putInt(DISABLE_MOVEMENT, data.getInt(DISABLE_MOVEMENT) + 1);
    }

    public static void enableMovement(Player player) {
        var data = player.getPersistentData();
        data.putInt(DISABLE_MOVEMENT, data.getInt(DISABLE_MOVEMENT) - 1);
    }

    public static boolean isMovementDisabled(Player player) {
        var data = player.getPersistentData();
        return data.getInt(DISABLE_MOVEMENT) > 0;
    }
}
