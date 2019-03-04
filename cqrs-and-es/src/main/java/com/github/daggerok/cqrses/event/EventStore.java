package com.github.daggerok.cqrses.event;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Value
@Getter(PRIVATE)
public class EventStore {

    private final Map<UUID, List<Event>> events;

    public List<Event> findAggregateEvents(UUID aggregateId) {
        return HashMap.ofAll(events)
                      .find(t -> t._1().equals(aggregateId))
                      .map(Tuple2::_2)
                      .getOrElse(CopyOnWriteArrayList::new);
    }

    public void updateAggregateEvents(UUID aggregateId, List<Event> newEvents) {
        log.debug("{}, {}", aggregateId, newEvents);
        events.put(aggregateId, newEvents);
    }

    // TODO: Implement me...
    //public <A extends Aggregate> A fromBeginning(UUID aggregateId) {
    //  List<Event> list = events.get(aggregateId);
    //  return foldLeft(list, A::apply);
    //}

    //public <A extends Aggregate, E extends Event> A fromSnapshot(A snapshot, List<E> list) {
    //  return foldLeft(list, snapshot::apply);
    //}
}
