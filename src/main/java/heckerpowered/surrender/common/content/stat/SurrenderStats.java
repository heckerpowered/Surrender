package heckerpowered.surrender.common.content.stat;

import heckerpowered.surrender.common.SurrenderMod;
import net.minecraft.stats.StatType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Surrender mod's stats, which is a static class, do not instantiate it.
 *
 * @author Heckerpowered
 */
public final class SurrenderStats {
    private SurrenderStats() {
    }

    public static final DeferredRegister<StatType<?>> DEFERRED_REGISTER = DeferredRegister
            .create(ForgeRegistries.STAT_TYPES, SurrenderMod.MODID);
}
