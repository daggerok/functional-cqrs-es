package com.github.daggerok.cqrses.command;

import com.github.daggerok.cqrses.aggregate.AggregateRoot;
import com.github.daggerok.cqrses.bus.Bus;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Value
@Getter(PRIVATE)
public class CommandHandler implements Bus {
    { this.subscribe(); }

    private final AggregateRoot aggregateRoot;

    // CommandHandler:
    // - create new aggregate if initial command occur
    // - find aggregate by command id
    // - handle command and get result event
    // - send event to event store
    @Subscribe
    public <C extends Command> void handle(C command) {
        aggregateRoot.createNewAggregateIfNeededFor(command);
        aggregateRoot.find(command.getAggregateId())
                     .map(aggregate -> aggregate.handle(command))
                     .ifPresent(event -> sync(() -> event));
    }
}
