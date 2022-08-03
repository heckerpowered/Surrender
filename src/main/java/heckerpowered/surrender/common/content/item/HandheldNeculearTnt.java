package heckerpowered.surrender.common.content.item;

import heckerpowered.surrender.common.SurrenderMod;
import heckerpowered.surrender.common.content.block.SurrenderBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class HandheldNeculearTnt extends BlockItem {

    public HandheldNeculearTnt() {
        super(SurrenderBlocks.NECULEAR_TNT.get(), new Properties().tab(SurrenderMod.CREATIVE_MODE_TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level instanceof final ServerLevel serverLevel) {

        }

        return super.use(level, player, hand);
    }

}
