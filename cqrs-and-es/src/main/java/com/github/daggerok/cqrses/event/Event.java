package com.github.daggerok.cqrses.event;

import lombok.Value;

import java.util.UUID;

public interface Event {
    UUID getAggregateId();

    Event INVALID = new Invalid(UUID.fromString("00000000-0000-0000-0000-000000000000"));

    @Value
    class Invalid implements Event {
        final UUID aggregateId;
    }
}
