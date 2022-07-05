package heckerpowered.surrender.common.content;

import heckerpowered.surrender.common.SurrenderMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class SurrenderItems {
    private SurrenderItems() {
    }

    public static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister
            .create(ForgeRegistries.ITEMS, SurrenderMod.MODID);
}
