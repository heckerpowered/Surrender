package heckerpowered.surrender.common.util.player;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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

    @Nonnull
    public static final Optional<ItemStack> search(@Nonnull final LivingEntity entity,
            @Nonnull final Predicate<ItemStack> condition) {
        //
        // For-each any slots to search the item.
        //
        for (final var equipmentSlot : EquipmentSlot.values()) {
            final var stack = entity.getItemBySlot(equipmentSlot);
            if (condition.test(stack)) {
                return Optional.of(stack);
            }
        }

        //
        // Determine if the entity is a player, and for-each the player's inventory to
        // search the item.
        //
        if (entity instanceof final Player player) {
            for (final var stack : player.getInventory().items) {
                if (condition.test(stack)) {
                    return Optional.of(stack);
                }
            }
        }

        return Optional.empty();
    }
}
