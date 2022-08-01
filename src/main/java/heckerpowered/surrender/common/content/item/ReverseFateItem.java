package heckerpowered.surrender.common.content.item;

import javax.annotation.Nonnull;

import heckerpowered.surrender.common.SurrenderMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Reverse fate is similar to {@link Items#TOTEM_OF_UNDYING}, The difference is that "Reverse Fate" only
 * needs to be in the item bar to be effective, and the effect is different.
 * <p> When you take a damage that can put you into dying state, the damage will be converted into a poison
 * effect that lasts for 20 seconds, while grants a short time of invisibility and speed effect to allow
 * you to escape, the above effect have no particle effect. After 20 seconds, grants you 5 seconds of regeneration.
 */
@Mod.EventBusSubscriber
public final class ReverseFateItem extends Item {

    public ReverseFateItem() {
        super(new Properties().stacksTo(1).tab(SurrenderMod.CREATIVE_MODE_TAB).rarity(Rarity.UNCOMMON));
    }

    @SubscribeEvent
    public static final void onLivingDeath(@Nonnull final LivingDeathEvent event) {

    }
}
