package heckerpowered.surrender.common.content.item;

import javax.annotation.Nonnull;

import heckerpowered.surrender.common.SurrenderMod;
import heckerpowered.surrender.common.content.effect.SurrenderMobEffects;
import heckerpowered.surrender.common.core.util.player.PlayerUtil;
import heckerpowered.surrender.common.network.SurrenderNetwork;
import heckerpowered.surrender.common.network.clientbound.ClientboundDisplayItemActivationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Reverse fate is similar to {@link Items#TOTEM_OF_UNDYING}, The difference is that "Reverse Fate" only
 * needs to be in the item bar to be effective, and the effect is different.
 * <p> When you take a damage that can put you into dying state,clear the effects on you and the damage will
 * be converted into a poison effect that lasts for 20 seconds, while grants a short time of invisibility and
 * speed effect to allow you to escape, the above effect have no particle effect.
 */
@Mod.EventBusSubscriber
public final class ReverseFateItem extends Item {

    public ReverseFateItem() {
        super(new Properties().stacksTo(1).tab(SurrenderMod.CREATIVE_MODE_TAB).rarity(Rarity.UNCOMMON));
    }

    @SubscribeEvent
    public static final void onLivingDeath(@Nonnull final LivingDeathEvent event) {
        final var entity = event.getEntity();
        PlayerUtil.search(entity, stack -> stack.getItem() instanceof ReverseFateItem).ifPresent(stack -> {
            event.setCanceled(true);
            stack.shrink(1);

            entity.setHealth(0.5F);
            entity.removeAllEffects();
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 4, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1, false, false));
            entity.addEffect(new MobEffectInstance(SurrenderMobEffects.UNTARGETABLE.get(), 100, 0, false, false));

            if (entity instanceof final ServerPlayer player) {
                final var foodData = player.getFoodData();
                foodData.setFoodLevel(20);
                foodData.setSaturation(20.0F);
                foodData.setExhaustion(0.0F);
                SurrenderNetwork.sendTo(
                        new ClientboundDisplayItemActivationPacket(new ItemStack(SurrenderItems.REVERSE_FATE.get())),
                        player);
            }
        });
    }
}
