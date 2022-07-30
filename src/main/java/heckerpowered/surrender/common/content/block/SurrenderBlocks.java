package heckerpowered.surrender.common.content.block;

import heckerpowered.surrender.common.SurrenderMod;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Surrender mod's block register class, which is a static class, do not instantiate it.
 *
 * @author Heckerpowered
 */
public final class SurrenderBlocks {
    private SurrenderBlocks() {
    }

    public static final DeferredRegister<Block> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS,
            SurrenderMod.MODID);

    public static final RegistryObject<Block> NECULEAR_TNT = DEFERRED_REGISTER.register("neculear_tnt",
            MiniCivilianNeculearTntBlock::new);
}
