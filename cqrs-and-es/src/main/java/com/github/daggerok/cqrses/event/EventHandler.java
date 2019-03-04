package com.github.daggerok.cqrses.event;

import com.github.daggerok.cqrses.bus.Bus;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.greenrobot.eventbus.Subscribe;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Value
@Getter(PRIVATE)
public class EventHandler implements Bus {

    final EventStore eventStore;

    { subscribe(); }

    // EventHandler:
    // - find list of events by it's aggregate id
    // - add event to end of the list
    // - replace list of events for current aggregate in event store
    // - rise event created event to be handled in a EventStoreHandler
    @Subscribe
    public void on(Event event) {
        log.debug("{}", event);
        val aggregateId = event.getAggregateId();
        val oldEvents = eventStore.findAggregateEvents(aggregateId);
        val newEvents = List.ofAll(oldEvents)
                            .append(event)
                            .toJavaList();
        eventStore.updateAggregateEvents(aggregateId, newEvents);
        sync(() -> new EventSaved(event));
    }
}
