package heckerpowered.surrender.common.content.tab;

import heckerpowered.surrender.common.SurrenderMod;
import heckerpowered.surrender.common.content.item.SurrenderItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class SurrenderCreativeModeTab extends CreativeModeTab {

    public SurrenderCreativeModeTab() {
        super(SurrenderMod.MODID);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(SurrenderItems.NUCLEAR_TNT.get());
    }

}
