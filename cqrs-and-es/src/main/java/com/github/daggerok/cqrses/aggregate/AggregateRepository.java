package com.github.daggerok.cqrses.aggregate;

import io.vavr.collection.HashMap;
import lombok.Getter;
import lombok.Value;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Value
@Getter(PRIVATE)
public class AggregateRepository {

    private final Map<UUID, Aggregate> store;

    public Aggregate save(Aggregate aggregate) {
        return store.put(aggregate.getAggregateId(), aggregate);
    }

    public Optional<Aggregate> findAggregate(UUID aggregateId) {
        return HashMap.ofAll(store)
                      .get(aggregateId)
                      .toJavaOptional();
    }
}
