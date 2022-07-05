package heckerpowered.surrender.common.core.util.scheduled;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public final class ScheduledTickTask {
    private final Runnable runnable;
    private Optional<Runnable> end;
    private final int tick;
    private int currentTick;
    private BooleanSupplier terminator;

    public ScheduledTickTask(int tick, Runnable runnable) {
        this.tick = tick;
        this.runnable = runnable;
        end = Optional.empty();
    }

    public void run() {
        runnable.run();
        if (terminator != null && terminator.getAsBoolean()) {
            currentTick = tick;
        }
    }

    public void tick() {
        currentTick++;
    }

    public boolean shouldEnd() {
        return currentTick >= tick;
    }

    public void onEnd() {
        if (end.isPresent()) {
            end.get().run();
        }
    }

    public ScheduledTickTask end(Runnable runnable) {
        end = Optional.of(runnable);
        return this;
    }

    public ScheduledTickTask terminate(BooleanSupplier terminator) {
        this.terminator = terminator;
        return this;
    }
}
