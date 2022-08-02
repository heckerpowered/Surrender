package heckerpowered.surrender.common.content.effect;

import heckerpowered.surrender.common.SurrenderMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SurrenderMobEffects {
    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private SurrenderMobEffects() {
    }

    public static final DeferredRegister<MobEffect> DEFERRED_REGISTER = DeferredRegister
            .create(ForgeRegistries.MOB_EFFECTS, SurrenderMod.MODID);

    public static final RegistryObject<UntargetableMobEffect> UNTARGETABLE = DEFERRED_REGISTER.register("untargetable",
            UntargetableMobEffect::new);
}
