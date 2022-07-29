package heckerpowered.surrender.client.event;

import heckerpowered.surrender.common.event.EnchantmentEventHandler;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientEventHandler {
    private ClientEventHandler() {

    }

    @SubscribeEvent
    public static final void onRenderPlayer(final RenderPlayerEvent event) {
        final var data = event.getEntity().getEntityData();
        if (data.get(EnchantmentEventHandler.DATA_BLINK_ACTIVE)
                || data.get(EnchantmentEventHandler.DATA_BLISTERING_ACTIVE)) {
            //
            // Make player invisible.
            //
            event.setCanceled(true);
        }
    }
}
