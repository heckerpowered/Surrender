package heckerpowered.surrender.event;

import java.util.concurrent.ConcurrentLinkedDeque;

import heckerpowered.surrender.util.scheduled.ScheduledTickTask;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ScheduledTickEvent {
    private static final ConcurrentLinkedDeque<ScheduledTickTask> tasks = new ConcurrentLinkedDeque<ScheduledTickTask>();

    @SubscribeEvent
    public static final void onTick(ServerTickEvent event) {
        if (event.phase == Phase.START) {
            var iterator = tasks.iterator();
            while (iterator.hasNext()) {
                var task = iterator.next();
                if (task.shouldEnd()) {
                    task.onEnd();
                    iterator.remove();
                } else {
                    task.run();
                    task.tick();
                }
            }
        }
    }

    public static final ScheduledTickTask scheduled(ScheduledTickTask task) {
        tasks.add(task);
        return task;
    }
}
