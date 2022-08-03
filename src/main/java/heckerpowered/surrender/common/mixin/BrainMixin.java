package heckerpowered.surrender.common.mixin;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import heckerpowered.surrender.common.content.effect.SurrenderMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

@Mixin(Brain.class)
public class BrainMixin {

    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories;

    @Inject(method = "hasMemoryValue", at = @At("TAIL"), cancellable = true)
    public void hasMemoryValue(MemoryModuleType<?> type, CallbackInfoReturnable<Boolean> info) {
        if (type == MemoryModuleType.ATTACK_TARGET && info.getReturnValue()) {
            if (memories.get(type).get().getValue() instanceof final LivingEntity entity) {
                if (entity.hasEffect(SurrenderMobEffects.UNTARGETABLE.get())) {
                    info.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "getMemory", at = @At("TAIL"), cancellable = true)
    public <T> void getMemory(MemoryModuleType<T> type, CallbackInfoReturnable<Optional<T>> info) {
        if (type == MemoryModuleType.ATTACK_TARGET) {
            final var returnValue = info.getReturnValue();
            if (returnValue.isPresent() && returnValue.get() instanceof final LivingEntity entity
                    && entity.hasEffect(SurrenderMobEffects.UNTARGETABLE.get())) {
                info.setReturnValue(Optional.empty());
            }
        }
    }

    @Inject(method = "checkMemory", at = @At("TAIL"), cancellable = true)
    public void checkMemory(MemoryModuleType<?> memoryModuleType, MemoryStatus memoryStatus,
            CallbackInfoReturnable<Boolean> info) {
        if (memoryModuleType == MemoryModuleType.ATTACK_TARGET && memoryStatus != MemoryStatus.REGISTERED
                && memoryStatus == MemoryStatus.VALUE_PRESENT ? info.getReturnValue() : !info.getReturnValue()) {
            final var memory = memories.get(memoryModuleType);
            if (memory.isPresent() && memory.get().getValue() instanceof final LivingEntity entity
                    && entity.hasEffect(SurrenderMobEffects.UNTARGETABLE.get())) {
                info.setReturnValue(memoryStatus != MemoryStatus.VALUE_PRESENT);
            }
        }
    }
}
