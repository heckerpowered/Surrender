package heckerpowered.surrender.client;

import javax.annotation.Nonnull;

import heckerpowered.surrender.client.renderer.NeculearTntRenderer;
import heckerpowered.surrender.common.content.entity.SurrenderEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static final void onRegisterRenderers(@Nonnull final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SurrenderEntityType.NECULEAR_TNT.get(), NeculearTntRenderer::new);
    }

}
