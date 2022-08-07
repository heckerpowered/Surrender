package heckerpowered.surrender.common.content.advancement;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import heckerpowered.surrender.common.SurrenderMod;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SurrenderCriteriaTriggers {
    private static final List<CriterionTrigger<?>> DEFERRED_REGISTER = new LinkedList<>();

    private SurrenderCriteriaTriggers() {
    }

    public static final PlayerTrigger LOGGED_IN = register("logged_in", PlayerTrigger::new);
    public static final PlayerTrigger NUCLEAR_IMPACT = register("nuclear_impact", PlayerTrigger::new);

    private static final <T extends CriterionTrigger<?>> T register(@Nonnull final String name,
            @Nonnull final Function<ResourceLocation, T> constructor) {
        final var criteria = constructor.apply(SurrenderMod.resource(name));
        DEFERRED_REGISTER.add(criteria);
        return criteria;
    }

    @SubscribeEvent
    public static final void onCommonSetup(@Nonnull final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DEFERRED_REGISTER.forEach(CriteriaTriggers::register));
    }

    @Mod.EventBusSubscriber
    private static final class PlayerLoggedInEventHandler {
        private PlayerLoggedInEventHandler() {
        }

        @SubscribeEvent
        public static final void onPlayerLoggedIn(@Nonnull final PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof final ServerPlayer player) {
                SurrenderCriteriaTriggers.LOGGED_IN.trigger(player);
            }
        }
    }
}
