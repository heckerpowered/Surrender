package heckerpowered.surrender.common.content.block;

import org.jetbrains.annotations.Nullable;

import heckerpowered.surrender.common.content.entity.NuclearTnt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Mini-Civilian Neculear Tnt block, when ignited, it generates an explosion with a
 * range of 50m does not cause damage to the entity that ignited it, but destroys the
 * blocks and has the same appearance and time of ignition as Tnt.
 *
 * @author Heckerpowered
 */
public final class MiniCivilianNeculearTntBlock extends Block {

    public MiniCivilianNeculearTntBlock() {
        super(BlockBehaviour.Properties.of(Material.EXPLOSIVE).instabreak().sound(SoundType.GRASS));
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.UNSTABLE, false));
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction,
            @Nullable LivingEntity igniter) {
        level.removeBlock(pos, false);
        final var entity = new NuclearTnt(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, igniter);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS,
                1.0F, 1.0F);
        level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
        level.addFreshEntity(entity);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos location, Player player,
            InteractionHand hand, BlockHitResult hit) {
        final var stack = player.getItemInHand(hand);
        if (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE)) {
            onCaughtFire(state, level, location, hit.getDirection(), player);

            final var item = stack.getItem();
            if (!player.isCreative()) {
                if (stack.is(Items.FLINT_AND_STEEL)) {
                    stack.hurtAndBreak(1, player, v -> {
                        v.broadcastBreakEvent(hand);
                    });
                } else {
                    stack.shrink(1);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(item));

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos location, BlockState previousState, boolean moving) {
        if (!previousState.is(state.getBlock())) {
            if (level.hasNeighborSignal(location)) {
                onCaughtFire(state, level, location, null, null);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos location, Block block, BlockPos from,
            boolean moving) {
        if (level.hasNeighborSignal(location)) {
            onCaughtFire(state, level, location, null, null);
        }
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (level.isClientSide) {
            return;
        }

        final var location = hit.getBlockPos();
        final var entity = projectile.getOwner();
        if (projectile.isOnFire() && projectile.mayInteract(level, location)) {
            onCaughtFire(state, level, location, null, entity instanceof LivingEntity living ? living : null);
        }
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.UNSTABLE);
    }
}
