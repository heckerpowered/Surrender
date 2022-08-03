package heckerpowered.surrender.common.content.item;

import heckerpowered.surrender.common.SurrenderMod;
import heckerpowered.surrender.common.content.block.SurrenderBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SurrenderItems {
    private SurrenderItems() {
    }

    public static final DeferredRegister<Item> DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS,
            SurrenderMod.MODID);

    public static final RegistryObject<BlockItem> NUCLEAR_TNT = DEFERRED_REGISTER.register("nuclear_tnt",
            () -> new BlockItem(SurrenderBlocks.NECULEAR_TNT.get(),
                    new Item.Properties().tab(SurrenderMod.CREATIVE_MODE_TAB)));

    public static final RegistryObject<ReverseFateItem> REVERSE_FATE = DEFERRED_REGISTER.register("reverse_fate",
            ReverseFateItem::new);
}
