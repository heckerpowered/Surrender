package heckerpowered.surrender.client.event;

import heckerpowered.surrender.common.event.EnchantmentEventHandler;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {
    private ClientEventHandler() {

    }

    public static final void initialize() {
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
    }

    @SubscribeEvent
    public static final void onRenderPlayer(final RenderPlayerEvent event) {
        if (event.getPlayer().getEntityData().get(EnchantmentEventHandler.DATA_BLINK_ACTIVE)) {
            //
            // Make player invisible.
            //
            event.setCanceled(true);
        }
    }
}
