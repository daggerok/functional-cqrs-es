package com.github.daggerok.samples.aggregate.event;

import com.github.daggerok.cqrses.event.Event;
import lombok.Value;

import java.util.UUID;

@Value
public class CounterIncremented implements Event {
    final UUID aggregateId;
}
