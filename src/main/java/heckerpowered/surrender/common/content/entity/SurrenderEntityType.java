package heckerpowered.surrender.common.content.entity;

import heckerpowered.surrender.common.SurrenderMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Surrender mod's entity type, which is a static class, do not instantiate it.
 *
 * @author Heckerpowered
 */
public final class SurrenderEntityType {
    /**
     * Prevent from create a new instance.
     */
    private SurrenderEntityType() {
    }

    public static final DeferredRegister<EntityType<?>> DEFERRED_REGISTER = DeferredRegister
            .create(ForgeRegistries.ENTITY_TYPES, SurrenderMod.MODID);

    public static final RegistryObject<EntityType<NeculearTnt>> NECULEAR_TNT = DEFERRED_REGISTER
            .register("neculear_tnt",
                    () -> EntityType.Builder.<NeculearTnt>of(NeculearTnt::new, MobCategory.MISC).fireImmune()
                            .sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10).build("neculear_tnt"));
}
