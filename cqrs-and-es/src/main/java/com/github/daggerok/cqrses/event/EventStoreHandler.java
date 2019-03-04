package com.github.daggerok.cqrses.event;

import com.github.daggerok.cqrses.aggregate.AggregateRoot;
import com.github.daggerok.cqrses.bus.Bus;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Value
@Getter(PRIVATE)
public class EventStoreHandler implements Bus {

    final AggregateRoot aggregateRoot;

    { subscribe(); }

    // EventStoreHandler:
    // - find aggregate by it's id
    // - apply event created payload event apply aggregate
    @Subscribe
    public void on(EventSaved eventSavedEvent) {
        Event event = eventSavedEvent.getEvent();
        UUID aggregateId = event.getAggregateId();
        aggregateRoot.find(aggregateId)
                     .map(aggregate -> aggregate.apply(event))
                     .ifPresent(aggregateRoot::save);
    }
}
