package heckerpowered.surrender.common;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import heckerpowered.surrender.client.event.ClientEventHandler;
import heckerpowered.surrender.common.content.SurrenderEnchantments;
import heckerpowered.surrender.common.content.SurrenderItems;
import heckerpowered.surrender.common.event.EnchantmentEventHandler;
import heckerpowered.surrender.common.event.ScheduledTickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SurrenderMod.MODID)
public class SurrenderMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "surrender";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SurrenderMod() {
        //
        // Register game contents.
        //
        setupContent(FMLJavaModLoadingContext.get().getModEventBus());

        //
        // Register events.
        //
        setupEvents();
    }

    private void setupContent(final IEventBus eventBus) {
        SurrenderEnchantments.DEFERRED_REGISTER.register(eventBus);
        SurrenderItems.DEFERRED_REGISTER.register(eventBus);
    }

    private void setupEvents() {
        MinecraftForge.EVENT_BUS.register(EnchantmentEventHandler.class);
        MinecraftForge.EVENT_BUS.register(ScheduledTickEvent.class);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::initialize);
    }
}
